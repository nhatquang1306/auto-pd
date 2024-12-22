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
import java.util.Stack;

public class TinhSu {
    Account account;
    BufferedImage lion;
    private final JButton startButton;
    private boolean terminateFlag;
    private final LocationReader lr;
    private final CoordinatesReader cr;
    boolean[] visited;
    private static final Color mapBoxColor1 = new Color(123, 249, 47);
    private static final Color mapBoxColor2 = new Color(141, 125, 81);
    private static final Color mapBoxColor3 = new Color(1, 112, 136);

    public TinhSu(HWND handle, double scale, JButton startButton) {
        try {
            this.lion = ImageIO.read(new File("app/data/lion.png"));
        } catch (Exception _) {

        }
        this.lr = new LocationReader(handle);
        this.cr = new CoordinatesReader(handle);
        this.visited = new boolean[2];

        this.account = new Account(0, 0, handle, scale);
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
                if (location.cth != null && System.currentTimeMillis() - start >= 1500000) {
                    useIncense();
                    getCheer(location);
                    start = System.currentTimeMillis();
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
                    if (arr[0] > 600 && arr[1] < 250) account.click(779, 38);
                    account.clickOnNpc(arr);
                    if (isAtLocation(coordinates[0], coordinates[1]) && waitForDialogueBox()) {
                        progressMatch();
                    }
                    if (arr[0] > 600 && arr[1] < 250 && !hasMapBox()) account.click(779, 38);
                    if (location.cth != null && System.currentTimeMillis() - start >= 1500000) {
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

    private boolean hasMapBox() {
        Color color1 = account.getPixelColor(782, 112);
        Color color2 = account.getPixelColor(665, 100);
        Color color3 = account.getPixelColor(650, 144);
        return color1.equals(mapBoxColor1) && color2.equals(mapBoxColor2) && color3.equals(mapBoxColor3);
    }

    private void progressMatch() throws InterruptedException {
        account.click(255, 325);
        long start = System.currentTimeMillis();
        while (!terminateFlag && !account.isInBattle()) {
            if (System.currentTimeMillis() - start >= 10000) {
                return;
            }
            Thread.sleep(200);
        }
        while (!terminateFlag && account.isInBattle()) {
            Thread.sleep(200);
        }
    }

    private void getCheer(Location location) throws InterruptedException {
        int[] cth = location.cth;
        useMap(cth[0], cth[1], cth[2], cth[3]);
        account.clickOnNpc(cth[4], cth[5]);
        if (waitForDialogueBox()) {
            account.click(245, 305);
            if (waitForDialogueBox()) account.click(557, 266);
        }
    }

    private void useMap(int a, int b, int x, int y) throws InterruptedException {
        account.click(766, 183);
        if (!visited[0]) {
            if (account.hasDialogueBox()) account.click(557, 266);
            visited[0] = true;
        }
        account.click(a, b);
        account.click(766, 183);
        long start = System.currentTimeMillis();
        while (!terminateFlag && !isAtLocation(x, y)
                && System.currentTimeMillis() - start < 50000) {
            Thread.sleep(500);
        }
        Thread.sleep(500);
    }

    private void useIncense() throws InterruptedException {
        if (terminateFlag) return;
        account.click(569, 586);
        if (!visited[1]) {
            if (account.hasDialogueBox()) account.click(557, 266);
            visited[1] = true;
        }
        account.rightClick(450, 367);
        account.click(569, 586);
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
                if (matched[i][j] || fullScreen.getRGB(i, j) != -985088 || !imageMatch(i - 1, j, fullScreen)) {
                    continue;
                }
                stack.push(new int[] {i + 60, j - 30});
                for (int l = 0; l < lion.getWidth(); l++) {
                    for (int m = 0; m < lion.getHeight(); m++) {
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
        for (int i = 0; i < lion.getWidth(); i++) {
            for (int j = 0; j < lion.getHeight(); j++) {
                int color = lion.getRGB(i, j);
                if (color == 0x00000000 || i + x < 0 || j + y < 0 || i + x >= 800 || j + y >= 600) {
                    continue;
                }
                if (screen.getRGB(i + x, j + y) == color) {
                    if (++match >= 200) return true;
                }
            }
        }
        return false;
    }

    private boolean waitForDialogueBox() throws InterruptedException {
        long start = System.currentTimeMillis();
        while (!terminateFlag && System.currentTimeMillis() - start < 5000) {
            if (account.hasDialogueBox()) {
                return true;
            }
            Thread.sleep(200);
        }
        return false;
    }

    public void setTerminateFlag() {
        this.terminateFlag = true;
    }
}
