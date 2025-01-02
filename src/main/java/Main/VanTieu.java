package Main;

import TextReaders.CoordinatesReader;
import TextReaders.LocationReader;
import com.sun.jna.platform.win32.WinDef.HWND;

import javax.swing.*;
import java.io.IOException;
import java.util.*;

public class VanTieu extends Program {
    LocationReader dr;
    Set<String> visitedLocations;
    private static final Map<String, int[]> mapInfos = new HashMap<>();
    private static final int[] npcColors = new int[] {7619655, 9426943, 12550759};

    public VanTieu(int[] skills, int[] pets, HWND[] handles, double scale, JButton startButton, int flagHash) {
        this.lr = new LocationReader(handles[0], 0);
        this.cr = new CoordinatesReader(handles[0]);
        this.dr = new LocationReader(handles[0], 1);
        this.visited = new boolean[2];
        this.visitedLocations = new HashSet<>();

        this.accounts = new Account[5];
        for (int i = 0; i < 5; i++) {
            if (handles[i] == null) continue;
            this.accounts[i] = new Account(skills[i], pets[i], handles[i], scale, true);
        }
        this.account = accounts[0];
        this.terminateFlag = false;
        this.flagHash = flagHash;
        this.itemQueues = new Queue[3];
        for (int i = 0; i < 3; i++) {
            this.itemQueues[i] = new LinkedList<>();
        }
        this.battleOrder = new int[] {8, 2, 0, 1, 3, 4, 6, 7};
        if (mapInfos.isEmpty()) fillMapInfos();

        this.startButton = startButton;
    }

    @Override
    public void run() {
        try {
            detectItems(true);
            useIncense();
            long start = System.currentTimeMillis();
            while (!terminateFlag) {
                if (System.currentTimeMillis() - start >= 1200000) {
                    useIncense();
                    start = System.currentTimeMillis();
                }
                if (!goToTTTC()) break;
                String destination = receiveQuest();
                account.click(557, 266);
                if (destination.isBlank() || !goToDestination(destination, mapInfos.get(destination))) break;
                boolean firstRun = true;
                while (true) {
                    if (!firstRun) {
                        account.clickRandomLocation(240, 380, 230, 170);
                        waitUntilStationary();
                    }
                    account.click(642, 268);
                    if (waitForDialogueBox(40) && progressMatch()) {
                        break;
                    }
                    firstRun = false;
                }
            }
        } catch (Exception _) {

        } finally {
            startButton.setBackground(null);
            startButton.setText("Start");
        }

    }

    private boolean goToTTTC() throws InterruptedException, IOException {
        if (terminateFlag) return false;
        while (!terminateFlag && !isAtLocation(18, 72, "tttc")) {
            account.click(569, 586);
            if (!visited[1]) {
                if (account.hasDialogueBox()) account.click(557, 266);
                visited[1] = true;
            }
            if (!hasItems(2)) {
                account.click(569, 586);
                if (!hasItems(2)) return false;
            }
            account.rightClick(itemQueues[2].peek());
            if (waitForDialogueBox(5)) {
                account.click(348, 287);
                if (waitForDialogueBox(5)) account.click(259, 286);
            }
            while (!terminateFlag && !isAtLocation(18, 72, "tttc")) {
                Thread.sleep(500);
            }
            account.click(569, 586);
        }
        return true;
    }

    private String receiveQuest() throws InterruptedException, IOException {
        if (terminateFlag) return "";
        do {
            if (!isAtLocation(18, 72, "tttc")) {
                if (!goToTTTC()) return "";
            }
            account.clickOnNpc(306, 145);
        } while (!terminateFlag && !waitForDialogueBox(5));
        account.click(261, 325);
        waitForDialogueBox(5);
        account.click(261, 325);
        waitForDialogueBox(5);
        return dr.read();
    }

    private boolean goToDestination(String destination, int[] mapInfo) throws InterruptedException, IOException {
        if (terminateFlag) return false;
        if (mapInfo[0] == 0) {
            if (destination.equals("tvd")) {
                if (goByTicket("lm", mapInfos.get("lm"))) {
                    goToTVD();
                    return true;
                } else {
                    return false;
                }
            } else {
                return goByTicket(destination, mapInfo);
            }
        } else {
            goByDP(destination, mapInfo);
            return true;
        }
    }

    private void goToTVD() throws InterruptedException {
        if (terminateFlag) return;
        long start = -20000;
        int limit;
        do {
            if (!isAtLocation(9, 96)) {
                if (System.currentTimeMillis() - start > 20000) {
                    useMap(275, 299);
                    start = System.currentTimeMillis();
                }
                limit = 1;
            } else {
                account.clickOnNpc(386, 168);
                limit = 5;
            }
        } while (!terminateFlag && !waitForDialogueBox(limit));
        account.click(252, 286);
        while (!terminateFlag && !lr.read().equals("tvd")) {
            Thread.sleep(500);
        }
    }

    private boolean goByTicket(String destination, int[] mapInfo) throws InterruptedException, IOException {
        account.click(569, 586);
        if (!visited[1]) {
            if (account.hasDialogueBox()) account.click(557, 266);
            visited[1] = true;
        }
        if (!hasItems(1)) {
            account.click(569, 586);
            if (!hasItems(1)) return false;
        }
        account.rightClick(itemQueues[1].peek());
        waitForDialogueBox(5);
        account.click(254, 286 + 18 * mapInfo[1]);
        while (!terminateFlag && !lr.read().equals(destination)) {
            Thread.sleep(500);
        }
        if (!visitedLocations.contains(destination)) {
            if (account.hasDialogueBox()) account.click(557, 266);
            visitedLocations.add(destination);
        }
        account.click(569, 586);
        return true;
    }

    public boolean goByDP(String destination, int[] mapInfo) throws InterruptedException, IOException {
        if (terminateFlag || !goByTicket("kt", mapInfos.get("kt"))) {
            return false;
        }
        long start = -20000;
        int limit;
        do {
            if (!isAtLocation(145, 177)) {
                if (System.currentTimeMillis() - start > 20000) {
                    useMap(428, 234);
                    start = System.currentTimeMillis();
                }
                limit = 1;
            } else {
                account.clickOnNpc(355, 392);
                limit = 5;
            }
        } while (!terminateFlag && !waitForDialogueBox(limit));
        account.click(254, 344);
        if (waitForDialogueBox(5)) {
            account.click(254, 362 + 18 * mapInfo[1]);
            if (waitForDialogueBox(5)) account.click(254, 285 + 18 * mapInfo[2]);
        }
        while (!terminateFlag && !lr.read().equals(destination)) {
            Thread.sleep(500);
        }
        if (!visitedLocations.contains(destination)) {
            if (account.hasDialogueBox()) account.click(557, 266);
            visitedLocations.add(destination);
        }
        return true;
    }

    private boolean progressMatch() throws InterruptedException {
        if (terminateFlag) return true;
        account.click(261, 307);
        for (int i = 0; i < 5; i++) {
            if (accounts[i] != null && accounts[i].isRelogged()) {
                accounts[i].click(411, 392);
                accounts[i] = null;
            }
        }
        long start = System.currentTimeMillis();
        while (!terminateFlag && !account.isInBattle()) {
            if (System.currentTimeMillis() - start >= 5000) {
                return false;
            }
            Thread.sleep(200);
        }
        battle(false);
        if (account != null && account.hasDialogueBox() && !endMatch()) {
            account.click(557, 266);
            battle(true);
        }
        account.click(557, 266);
        while (!terminateFlag && account.isInBattle()) {
            Thread.sleep(500);
        }
        return true;
    }
    private boolean endMatch() {
        int[] hashes = new int[] {account.getPixelHash(311, 89), account.getPixelHash(338, 164), account.getPixelHash(300, 226)};
        return Arrays.equals(hashes, npcColors);
    }

    @Override
    public void setTerminateFlag() {
        this.terminateFlag = true;
        for (Account account : accounts) {
            if (account != null) account.setTerminateFlag();
        }
    }
    private void fillMapInfos() {
        String[] locations = new String[] {
                "bvt" , "vul", "vdnd", "ktng", "bhc", "nnl",
                "gn", "lssl", "lstk", "lscd", "dps",
                "bbd", "bbdt1", "bbdtd", "bbdt2", "bbdt3",
                "pvl", "vmn", "ktp", "ktdg", "tt",
                "tvd", "kt", "vdd", "lm", "hht", "htt"
        };
        int[][] infos = new int[][] {
                {1, 0, 0}, {1, 0, 1}, {1, 0, 2}, {1, 0, 3}, {1, 0, 4}, {1, 0, 5},
                {1, 1, 0}, {1, 1, 1}, {1, 1, 2}, {1, 1, 3}, {1, 1, 4},
                {1, 2, 0}, {1, 2, 1}, {1, 2, 2}, {1, 2, 3}, {1, 2, 4},
                {1, 3, 0}, {1, 3, 1}, {1, 3, 2}, {1, 3, 3}, {1, 3, 4},
                {0, 0}, {0, 0}, {0, 1}, {0, 2}, {0, 3}, {0, 4}
        };
        for (int i = 0; i < locations.length; i++) {
            mapInfos.put(locations[i], infos[i]);
        }
    }
}
