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
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


public class PhanDo extends Program {
    Account[] accounts;
    BufferedImage traitor;
    private final JButton startButton;
    private static final Color traitorColor1 = new Color(249, 179, 127);
    private static final Color traitorColor2 = new Color(81, 58, 44);
    private static final Color traitorColor3 = new Color(168, 145, 130);

    public PhanDo(int[] skills, int[] pets, HWND[] handles, double scale, JButton startButton) {
        try {
            this.traitor = ImageIO.read(new File("app/data/traitor.png"));
        } catch (Exception _) {

        }
        this.lr = new LocationReader(handles[0]);
        this.cr = new CoordinatesReader(handles[0]);
        this.visited = new boolean[2];

        this.accounts = new Account[5];
        for (int i = 0; i < 5; i++) {
            if (handles[i] == null) continue;
            this.accounts[i] = new Account(skills[i], pets[i], handles[i], scale);
        }
        this.account = accounts[0];
        this.terminateFlag = false;

        this.startButton = startButton;
    }

    public void run() {
        try {
            Location location = new Location(lr.read());
            int index = 0;
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
                findEnemies(stack, traitor, -3, 250);
                while (!stack.isEmpty() && isAtLocation(coordinates[0], coordinates[1])) {
                    if (account.isInBattle()) continue;
                    int[] arr = stack.pop();
                    if (arr[0] < 0 || arr[1] < 0 || arr[0] >= 800 || arr[1] >= 600 || (arr[0] > 630 && arr[1] < 220)) {
                        continue;
                    }
                    account.clickOnNpc(arr);
                    waitUntilStationary();
                    if (isAtLocation(coordinates[0], coordinates[1]) && waitForDialogueBox()) {
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
            startButton.setText("Phản Đồ");
        }
    }

    public void getCheer(Location location) throws InterruptedException {
        int[] cth = location.cth;
        while (!terminateFlag && !isAtLocation(cth[2], cth[3])) {
            if (!lr.read().equals(location.name)) goBack(location);
            useMap(cth[0], cth[1]);
        }
        account.clickOnNpc(cth[4], cth[5]);
        if (waitForDialogueBox()) {
            account.click(245, 305);
            if (waitForDialogueBox()) account.click(557, 266);
        }
    }

    private void progressMatch() throws InterruptedException {
        accounts[0].click(255, 304);
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
                    return;
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
        if (account != null && account.hasDialogueBox()) {
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
        Thread.sleep(500);
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
        while (!map.containsKey(cur)) cur = lr.read();
        int[] arr = map.get(cur);
        if (arr[0] < 0) {
            useMap(-arr[0], arr[1]);
        } else {
            account.click(arr);
        }
    }

    private boolean isCorrectEnemy() {
        Color color1 = account.getPixelColor(304, 150);
        Color color2 = account.getPixelColor(304, 103);
        Color color3 = account.getPixelColor(303, 218);
        return color1.equals(traitorColor1) && color2.equals(traitorColor2) && color3.equals(traitorColor3);
    }

    public void setTerminateFlag() {
        this.terminateFlag = true;
        for (Account account : accounts) {
            if (account != null) account.setTerminateFlag();
        }
    }
}