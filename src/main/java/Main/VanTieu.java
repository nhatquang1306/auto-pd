package Main;

import TextReaders.CoordinatesReader;
import TextReaders.LocationReader;
import com.sun.jna.platform.win32.WinDef;

import javax.swing.*;
import java.io.IOException;
import java.util.*;

public class VanTieu extends Program {
    Account[] accounts;
    LocationReader dr;
    Set<String> visitedLocations;
    private static final Map<String, int[]> mapInfos = new HashMap<>();
    private static final int[] npcColors = new int[] {7619655, 9426943, 12550759};

    public VanTieu(int[] skills, int[] pets, WinDef.HWND[] handles, double scale, JButton startButton) {
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
        this.itemQueues = new Queue[] {new LinkedList<>(), new LinkedList<>()};
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
                do {
                    account.click(642, 268);
                } while (!terminateFlag && !waitForDialogueBox(50));
                progressMatch();
            }
        } catch (Exception _) {

        } finally {
            startButton.setBackground(null);
            startButton.setText("Start");
        }

    }

    private boolean goToTTTC() throws InterruptedException, IOException {
        if (terminateFlag) return true;
        while (!terminateFlag && !isAtLocation(24, 77, "tttc")) {
            if (!lr.read().equals("kt")) {
                if (!goByTicket("lm", mapInfos.get("lm"))){
                    return false;
                }
                useTransport(1);
            }
            long start = -30000;
            while (!terminateFlag && !lr.read().equals("tttc")) {
                if (System.currentTimeMillis() - start > 30000) {
                    useMap(102, 421);
                    start = System.currentTimeMillis();
                }
            }
            account.click(171, 240);
            while (!terminateFlag && !isAtLocation(24, 77)) {
                Thread.sleep(500);
            }
        }
        return true;
    }

    private String receiveQuest() throws InterruptedException, IOException {
        if (terminateFlag) return "";
        do {
            if (!isAtLocation(24, 77, "tttc")) {
                if (!goToTTTC()) return "";
            }
            account.clickOnNpc(97, 126);
        } while (!terminateFlag && !waitForDialogueBox(5));
        account.click(261, 325);
        waitForDialogueBox(5);
        account.click(261, 325);
        waitForDialogueBox(5);
        return dr.read();
    }

    private boolean goToDestination(String destination, int[] mapInfo) throws InterruptedException, IOException {
        if (terminateFlag) return true;
        if (mapInfo[0] == 0) {
            if (destination.equals("tvd")) {
                if (goByTicket("lm", mapInfos.get("lm"))) {
                    useTransport(0);
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

    private void useTransport(int i) throws InterruptedException {
        if (terminateFlag) return;
        long start = -10000;
        int limit;
        do {
            if (!isAtLocation(9, 96)) {
                if (System.currentTimeMillis() - start > 10000) {
                    useMap(275, 299);
                    start = System.currentTimeMillis();
                }
                limit = 1;
            } else {
                account.clickOnNpc(386, 168);
                limit = 5;
            }
        } while (!terminateFlag && !waitForDialogueBox(limit));
        account.click(252, 286 + 18 * i);
        String destination = i == 0 ? "tvd" : "kt";
        while (!terminateFlag && !lr.read().equals(destination)) {
            Thread.sleep(500);
        }
    }

    private boolean goByTicket(String destination, int[] mapInfo) throws InterruptedException, IOException {
        account.click(569, 586);
        if (!visited[1]) {
            if (account.hasDialogueBox()) account.click(557, 266);
            visited[1] = true;
        }
        if (hasItems(1)) {
            account.rightClick(itemQueues[1].peek());
        } else {
            return false;
        }
        waitForDialogueBox(5);
        account.click(254, 304 + 18 * mapInfo[1]);
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

    public void goByDP(String destination, int[] mapInfo) throws InterruptedException {
        if (terminateFlag) return;
        getOut();
        long start = -50000;
        int limit;
        do {
            if (!isAtLocation(149, 206)) {
                if (System.currentTimeMillis() - start > 50000) {
                    useMap(436, 251);
                    start = System.currentTimeMillis();
                }
                limit = 1;
            } else {
                account.clickOnNpc(241, 193);
                limit = 5;
            }
        } while (!terminateFlag && !waitForDialogueBox(limit));
        account.click(254, 344);
        waitForDialogueBox(5);
        account.click(254, 362 + 18 * mapInfo[1]);
        waitForDialogueBox(5);
        account.click(254, 285 + 18 * mapInfo[2]);
        while (!terminateFlag && !lr.read().equals(destination)) {
            Thread.sleep(500);
        }
        if (!visitedLocations.contains(destination)) {
            if (account.hasDialogueBox()) account.click(557, 266);
            visitedLocations.add(destination);
        }
    }
    private void getOut() throws InterruptedException {
        account.click(752, 512);
        while (!terminateFlag && !lr.read().equals("kt")) {
            Thread.sleep(500);
        }
        if (!visitedLocations.contains("kt")) {
            if (account.hasDialogueBox()) account.click(557, 266);
            visitedLocations.add("kt");
        }
    }

    private boolean progressMatch() throws InterruptedException {
        if (terminateFlag) return true;
        account.click(261, 307);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 5; i++) {
            if (accounts[i] == null || accounts[i].isRelogged()) {
                if (accounts[i] != null) {
                    accounts[i].click(411, 392);
                    accounts[i] = null;
                }
                continue;
            }
            while (!terminateFlag && !accounts[i].isInBattle()) {
                if (System.currentTimeMillis() - start >= 7500) {
                    return false;
                }
                Thread.sleep(200);
            }
        }
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            if (accounts[i] == null) continue;
            threads[i] = new Thread(accounts[i]::run);
            threads[i].start();
        }
        for (Thread thread : threads) {
            if (thread != null) thread.join();
        }
        if (account != null && account.hasDialogueBox() && !endMatch()) {
            for (int i = 0; i < 5; i++) {
                if (accounts[i] == null || accounts[i].isRelogged()) {
                    if (accounts[i] != null) {
                        accounts[i].click(411, 392);
                        accounts[i] = null;
                    }
                    continue;
                }
                accounts[i].setCatchPet();
                threads[i] = new Thread(accounts[i]::run);
                threads[i].start();
            }
            for (Thread thread : threads) {
                if (thread != null) thread.join();
            }
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
                "tvd", "vdd", "lm", "hht", "htt"
        };
        int[][] infos = new int[][] {
                {1, 0, 0}, {1, 0, 1}, {1, 0, 2}, {1, 0, 3}, {1, 0, 4}, {1, 0, 5},
                {1, 1, 0}, {1, 1, 1}, {1, 1, 2}, {1, 1, 3}, {1, 1, 4},
                {1, 2, 0}, {1, 2, 1}, {1, 2, 2}, {1, 2, 3}, {1, 2, 4},
                {1, 3, 0}, {1, 3, 1}, {1, 3, 2}, {1, 3, 3}, {1, 3, 4},
                {0, 1}, {0, 0}, {0, 1}, {0, 2}, {0, 3}
        };
        for (int i = 0; i < locations.length; i++) {
            mapInfos.put(locations[i], infos[i]);
        }
    }
}
