package Main;

import TextReaders.CoordinatesReader;
import TextReaders.LocationReader;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class TinhSu extends Program {
    BufferedImage[] enemies;
    int[][] enemiesInfo;
    private static final int[] lionColors = new int[] {14978649, 116, 6640};

    public TinhSu(HWND handle, double scale, JButton startButton) {
        try {
            this.enemies = new BufferedImage[] {ImageIO.read(new File("app/data/lion1.png")), ImageIO.read(new File("app/data/lion2.png"))};
            this.enemiesInfo = new int[][] {{-985088, 340, -1, 0, 60, -10}, {-3166449, 200, 0, 0, 0, 10}};
        } catch (Exception _) {

        }
        this.lr = new LocationReader(handle, 0);
        this.cr = new CoordinatesReader(handle);
        this.visited = new boolean[2];

        this.account = new Account(0, 0, handle, scale, false);
        this.flagHash = -1;
        this.terminateFlag = false;
        this.itemQueues = new Queue[3];
        for (int i = 0; i < 3; i++) {
            this.itemQueues[i] = new LinkedList<>();
        }
        this.battleOrder = new int[] {2, 0, 1, 3, 4, 6, 7, 8};
        this.startButton = startButton;
    }

    @Override
    public void run() {
        try {
            Location location = new Location(lr.read());
            int index = 0;
            detectItems(true);
            if (location.cth != null) {
                useIncense();
                getCheer(location);
            }
            long start = System.currentTimeMillis();
            int[] coordinates = new int[2];
            int increment = 1;
            while (!terminateFlag) {
                if (account.isInBattle()) continue;
                if (location.cth != null && System.currentTimeMillis() - start >= 1500000) {
                    useIncense();
                    getCheer(location);
                    start = System.currentTimeMillis();
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
                    } else if (location.cth != null && System.currentTimeMillis() - start >= 1500000) {
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

    private void goBack(Location location) throws InterruptedException {
        Map<String, int[]> map = switch (location.name) {
            case "vdd" -> Map.of(
                    "vdnd", new int[]{-613, 491},
                    "htks", new int[]{256, 402},
                    "vdtl", new int[]{533, 450},
                    "bvt", new int[]{-202, 177},
                    "hsc", new int[]{273, 455},
                    "tvd", new int[]{-163, 193}
            );
            case "hht" -> Map.of(
                    "vmn", new int[]{-151, 214},
                    "hhks", new int[]{547, 498},
                    "ktp", new int[]{-557, 438}
            );
            case "dps" -> Map.of(
                    "kt", new int[]{-101, 495},
                    "tptl", new int[]{614, 431},
                    "pvl", new int[]{-245, 191},
                    "btdbt", new int[]{299, 489},
                    "ktp", new int[]{-579, 264}
            );
            case "khl" -> Map.of(
                    "tt", new int[]{-164, 325},
                    "htt", new int[]{-253, 488}
            );
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

    public boolean isCorrectEnemy() {
        int[] hashes = new int[] {account.getPixelHash(333, 126), account.getPixelHash(339, 205), account.getPixelHash(306, 175)};
        return Arrays.equals(hashes, lionColors);
    }

    private void progressMatch() throws InterruptedException {
        account.click(255, 325);
        long start = System.currentTimeMillis();
        while (!terminateFlag && !account.isInBattle()) {
            if (System.currentTimeMillis() - start >= 5000) {
                return;
            }
            Thread.sleep(200);
        }
        while (!terminateFlag && account.isInBattle()) {
            Thread.sleep(200);
        }
        Thread.sleep(500);
    }

    private void getCheer(Location location) throws InterruptedException {
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

    @Override
    public void setTerminateFlag() {
        this.terminateFlag = true;
    }
}
