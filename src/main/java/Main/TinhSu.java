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
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class TinhSu extends Program {
    // check for right boss
    // make sure it doesn't go to another map
    BufferedImage lion;
    private final JButton startButton;
    private static final Color lionColor1 = new Color(89, 142, 228);
    private static final Color lionColor2 = new Color(116, 0, 0);
    private static final Color lionColor3 = new Color(240, 25, 0);

    public TinhSu(HWND handle, double scale, JButton startButton) {
        try {
            this.lion = ImageIO.read(new File("app/data/lion.png"));
            this.exit = ImageIO.read(new File("app/data/exit.png"));
        } catch (Exception _) {

        }
        this.lr = new LocationReader(handle);
        this.cr = new CoordinatesReader(handle);
        this.visited = new boolean[2];

        this.account = new Account(0, 0, handle, scale);
        this.terminateFlag = false;
        this.startButton = startButton;
    }

    public void run() {
        try {
            Location location = new Location(lr.read());
            int index = 0;
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
                findEnemies(stack, lion, -1, 340);
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
                    } else if (location.cth != null && System.currentTimeMillis() - start >= 1500000) {
                        break;
                    }
                }
            }
        } catch (Exception _) {

        } finally {
            startButton.setBackground(null);
            startButton.setText("Tỉnh Sư");
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

    private boolean isCorrectEnemy() {
        Color color1 = account.getPixelColor(333, 126);
        Color color2 = account.getPixelColor(339, 205);
        Color color3 = account.getPixelColor(306, 175);
        return color1.equals(lionColor1) && color2.equals(lionColor2) && color3.equals(lionColor3);
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
        if (waitForDialogueBox()) {
            account.click(245, 305);
            if (waitForDialogueBox()) account.click(557, 266);
        }
    }

    public void setTerminateFlag() {
        this.terminateFlag = true;
    }
}
