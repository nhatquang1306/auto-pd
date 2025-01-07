package Main;

import TextReaders.CoordinatesReader;
import TextReaders.LocationReader;
import com.sun.jna.Memory;
import com.sun.jna.platform.win32.WinDef.HWND;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;


public class PhanDo extends Program {
    BufferedImage[] enemies;
    int[][] enemiesInfo;
    private static final int[] traitorColors = new int[] {8369145, 2898513, 8556968};

    public PhanDo(int[] skills, int[] pets, HWND[] handles, double scale, JButton startButton) {
        try {
            this.enemies = new BufferedImage[] {ImageIO.read(new File("app/data/traitor1.png")), ImageIO.read(new File("app/data/traitor2.png"))};
            this.enemiesInfo = new int[][] {{-985088, 250, -3, 0, 60, -10}, {-13095652, 200, 0, 0, 0, 0}};
        } catch (Exception _) {

        }
        this.lr = new LocationReader(handles[0], 0);
        this.cr = new CoordinatesReader(handles[0]);
        this.visited = new boolean[2];

        this.accounts = new Account[5];
        for (int i = 0; i < 5; i++) {
            if (handles[i] == null) continue;
            this.accounts[i] = new Account(skills[i], pets[i], handles[i], scale);
        }
        this.account = accounts[0];
        this.flagHash = -1;
        this.itemQueues = new Queue[3];
        for (int i = 0; i < 3; i++) {
            this.itemQueues[i] = new LinkedList<>();
        }
        this.battleOrder = new int[] {2, 0, 1, 3, 4, 5, 6, 7, 8, 9, 11, 12};
        this.terminateFlag = false;

        this.startButton = startButton;
    }

    @Override
    public void run() {
        try {
            Location location = new Location(lr.read());
            int index = 0;
            detectItems(true);
            useIncense();
            getCheer(location);
            long start = System.currentTimeMillis();
            long cheerStart = System.currentTimeMillis();
            int[] coordinates = new int[2];
            int increment = 1;
            while (!terminateFlag) {
                if (account.isInBattle()) continue;
                if (System.currentTimeMillis() - start >= 1500000) {
                    useIncense();
                    start = System.currentTimeMillis();
                }
                if (System.currentTimeMillis() - cheerStart >= 900000) {
                    getCheer(location);
                    cheerStart = System.currentTimeMillis();
                }
                int[] newCoordinates = cr.read();
                if (newCoordinates[0] == coordinates[0] && newCoordinates[1] == coordinates[1]) {
                    moveToNextLocation(location, index);
                    if (!lr.read().equals(location.name)) goBack(location);
                    if (index == 0) increment = 1;
                    else if (index == location.mapCoordinates.length - 1) increment = -1;
                    index += increment;
                }
                coordinates = newCoordinates;
                Stack<int[]> stack = new Stack<>();
                for (int i = 0; i < enemies.length && stack.isEmpty(); i++) {
                    findEnemies(stack, enemies[i], enemiesInfo[i]);
                }
                while (!terminateFlag && !stack.isEmpty() && isAtLocation(coordinates[0], coordinates[1])) {
                    if (account.isInBattle()) continue;
                    int[] arr = stack.pop();
                    if (arr[0] < 0 || arr[1] <= 10 || arr[0] >= 800 || arr[1] >= 600 || (arr[0] > 630 && arr[1] < 220)) {
                        continue;
                    }
                    account.clickOnNpc(arr);
                    waitUntilStationary();
                    if (isAtLocation(coordinates[0], coordinates[1]) && waitForDialogueBox(3)) {
                        if (isCorrectEnemy()) {
                            progressMatch();
                        } else {
                            account.click(557, 266);
                            account.click(findExit());
                        }
                    }
                    if (!account.isInBattle() && !lr.read().equals(location.name)) {
                        goBack(location);
                        break;
                    } else if (System.currentTimeMillis() - start >= 1500000) {
                        break;
                    }
                }
            }
        } catch (Exception _) {

        } finally {
            startButton.setBackground(null);
            startButton.setText("Start");
        }
    }

    public void getCheer(Location location) throws InterruptedException {
        int[] cth = location.cth;
        while (!terminateFlag && !isAtLocation(cth[2], cth[3])) {
            if (!lr.read().equals(location.name)) goBack(location);
            useMap(cth[0], cth[1]);
        }
        account.clickOnNpc(cth[4], cth[5]);
        if (waitForDialogueBox(3)) {
            account.click(245, 305);
            if (waitForDialogueBox(3)) account.click(557, 266);
        }
    }

    private void progressMatch() throws InterruptedException {
        if (terminateFlag) return;
        account.click(255, 304);
        for (int i = 0; i < 5; i++) {
            if (accounts[i] != null && accounts[i].isRelogged()) {
                accounts[i].click(411, 392);
                accounts[i] = null;
            }
        }
        long start = System.currentTimeMillis();
        while (!terminateFlag && !account.isInBattle()) {
            if (System.currentTimeMillis() - start >= 5000) {
                return;
            }
            Thread.sleep(200);
        }
        battle(false);
        if (account != null && account.hasDialogueBox()) {
            battle(true);
        }
    }

    public void goBack(Location location) throws InterruptedException {
        Map<String, int[]> map = switch (location.name) {
            case "bhc" -> Map.of(
                    "ktng", new int[]{-470, 478},
                    "gn", new int[]{-184, 164},
                    "ptv", new int[]{-417, 497},
                    "td", new int[]{-581, 448},
                    "nl", new int[]{270, 482}
            );
            case "ktng" -> Map.of(
                    "kt", new int[]{-700, 493},
                    "bhc", new int[]{-440, 174},
                    "nnl", new int[]{-271, 154},
                    "bnvp", new int[]{267, 459},
                    "hyl", new int[]{241, 491},
                    "tgt", new int[]{-288, 411}
            );
            case "bvt" -> Map.of(
                    "vdd", new int[]{-424, 494},
                    "vul", new int[]{-596, 471},
                    "dc", new int[]{314, 448}
            );
            case "vul" -> Map.of("bvt", new int[]{-185, 360});
            default -> new HashMap<>();
        };
        String cur = lr.read();
        while (!terminateFlag && !map.containsKey(cur)) cur = lr.read();
        int[] arr = map.get(cur);
        if (arr[0] < 0) {
            useMap(-arr[0], arr[1]);
        } else {
            account.click(arr);
        }
    }

    private boolean isCorrectEnemy() {
        int[] hashes = new int[] {account.getPixelHash(304, 150), account.getPixelHash(304, 103), account.getPixelHash(303, 218)};
        return Arrays.equals(hashes, traitorColors);
    }

    @Override
    public void setTerminateFlag() {
        this.terminateFlag = true;
        for (Account account : accounts) {
            if (account != null) account.setTerminateFlag();
        }
    }
}