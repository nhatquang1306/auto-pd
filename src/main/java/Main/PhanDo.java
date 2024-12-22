package Main;

import TextReaders.CoordinatesReader;
import TextReaders.LocationReader;
import com.sun.jna.platform.win32.WinDef.HWND;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Stack;


public class PhanDo {
    Account[] accounts;
    BufferedImage traitor;
    private final JButton startButton;
    private boolean terminateFlag;
    private final LocationReader lr;
    private final CoordinatesReader cr;
    boolean[] visited;
    private static final Color mapBoxColor1 = new Color(123, 249, 47);
    private static final Color mapBoxColor2 = new Color(141, 125, 81);
    private static final Color mapBoxColor3 = new Color(1, 112, 136);

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
                if (System.currentTimeMillis() - start >= 1500000) {
                    useIncense();
                    changeLocation(location);
                    location = new Location(location.nextMap);
                    index = Math.min(index, location.coordinates.length - 1);
                    start = System.currentTimeMillis();
                }
                if (System.currentTimeMillis() - cheerStart >= 900000) {
                    getCheer(location);
                    cheerStart = System.currentTimeMillis();
                }
                int[] newCoordinates = cr.read();
                if (newCoordinates[0] == coordinates[0] && newCoordinates[1] == coordinates[1]) {
                    moveToNextLocation(location, index);
                    if (index == 0) increment = 1;
                    else if (index == location.mapCoordinates.length - 1) increment = -1;
                    index += increment;
                }
                coordinates = newCoordinates;
                Stack<int[]> stack = new Stack<>();
                findEnemies(stack);
                while (!stack.isEmpty() && isAtLocation(coordinates[0], coordinates[1])) {
                    int[] arr = stack.pop();
                    if (arr[0] < 0 || arr[1] < 0 || arr[0] >= 800 || arr[1] >= 600) {
                        continue;
                    }
                    if (arr[0] > 600 && arr[1] < 250) accounts[0].click(779, 38);
                    accounts[0].clickOnNpc(arr);
                    if (isAtLocation(coordinates[0], coordinates[1]) && waitForDialogueBox()) {
                        startMatch();
                    }
                    if (arr[0] > 600 && arr[1] < 250 && !hasMapBox()) accounts[0].click(779, 38);
                    if (System.currentTimeMillis() - start >= 1500000) {
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

    public boolean hasMapBox() {
        Color color1 = accounts[0].getPixelColor(782, 112);
        Color color2 = accounts[0].getPixelColor(665, 100);
        Color color3 = accounts[0].getPixelColor(650, 144);
        return color1.equals(mapBoxColor1) && color2.equals(mapBoxColor2) && color3.equals(mapBoxColor3);
    }

    public void changeLocation(Location location) throws InterruptedException {
        int[] arr = location.nextMapInfo;
        while (!lr.read().equals(location.nextMap)) {
            useMap(arr[0], arr[1], arr[2], arr[3]);
        }
    }

    private void startMatch() throws InterruptedException {

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
                if (System.currentTimeMillis() - start >= 10000) {
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
        if (accounts[0] != null && accounts[0].hasDialogueBox()) {
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
    }

    public void getCheer(Location location) throws InterruptedException {
        int[] cth = location.cth;
        useMap(cth[0], cth[1], cth[2], cth[3]);
        accounts[0].clickOnNpc(cth[4], cth[5]);
        if (waitForDialogueBox()) {
            accounts[0].click(245, 305);
            if (waitForDialogueBox()) accounts[0].click(557, 266);
        }
    }

    private void useMap(int a, int b, int x, int y) throws InterruptedException {
        accounts[0].click(766, 183);
        if (!visited[0]) {
            if (accounts[0].hasDialogueBox()) accounts[0].click(557, 266);
            visited[0] = true;
        }
        accounts[0].click(a, b);
        accounts[0].click(766, 183);
        long start = System.currentTimeMillis();
        while (!terminateFlag && !isAtLocation(x, y)
                && System.currentTimeMillis() - start < 50000) {
            Thread.sleep(500);
        }
        Thread.sleep(500);
    }

    private void useIncense() throws InterruptedException {
        if (terminateFlag) return;
        accounts[0].click(569, 586);
        if (!visited[1]) {
            if (accounts[0].hasDialogueBox()) accounts[0].click(557, 266);
            visited[1] = true;
        }
        accounts[0].rightClick(450, 367);
        accounts[0].click(569, 586);
    }

    private void moveToNextLocation(Location location, int index) throws InterruptedException {
        if (terminateFlag) return;
        int[] mc = location.mapCoordinates[index];
        int[] c = location.coordinates[index];
        useMap(mc[0], mc[1], c[0], c[1]);
    }

    private boolean isAtLocation(int x, int y) {
        int[] coords = cr.read();
        return coords[0] == x && coords[1] == y;
    }

    private void findEnemies(Stack<int[]> stack) {
        BufferedImage fullScreen = lr.captureWindow(3, 26, 800, 600);
        boolean[][] matched = new boolean[800][600];
        for (int i = 3; i < fullScreen.getWidth(); i++) {
            for (int j = 0; j < fullScreen.getHeight(); j++) {
                if (matched[i][j] || fullScreen.getRGB(i, j) != -985088 || !imageMatch(i - 3, j, fullScreen)) {
                    continue;
                }
                stack.push(new int[] {i + 60, j - 30});
                for (int l = 0; l < traitor.getWidth(); l++) {
                    for (int m = 0; m < traitor.getHeight(); m++) {
                        if (i + l >= 0 && j + m >= 0 && i + l < 800 && j + m < 600) {
                            matched[i + l][j + m] = true;
                        }
                    }
                }
            }
        }
    }

    private boolean imageMatch(int x, int y, BufferedImage screen) {
        int match = 0;
        for (int i = 0; i < traitor.getWidth(); i++) {
            for (int j = 0; j < traitor.getHeight(); j++) {
                int color = traitor.getRGB(i, j);
                if (color == 0x00000000 || i + x < 0 || j + y < 0 || i + x >= 800 || j + y >= 600) {
                    continue;
                }
                if (screen.getRGB(i + x, j + y) == color) {
                    if (++match >= 150) return true;
                }
            }
        }
        return false;
    }

    public boolean waitForDialogueBox() throws InterruptedException {
        long start = System.currentTimeMillis();
        while (!terminateFlag && System.currentTimeMillis() - start < 5000) {
            if (accounts[0].hasDialogueBox()) {
                return true;
            }
            Thread.sleep(200);
        }
        return false;
    }

    public void setTerminateFlag() {
        this.terminateFlag = true;
        for (Account account : accounts) {
            if (account != null) account.setTerminateFlag();
        }
    }
}