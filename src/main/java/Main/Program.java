package Main;

import TextReaders.CoordinatesReader;
import TextReaders.LocationReader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Set;
import java.util.Stack;

public abstract class Program {
    public boolean terminateFlag;
    public Account account;
    public BufferedImage exit;
    public LocationReader lr;
    public CoordinatesReader cr;
    public boolean[] visited;

    public void waitUntilStationary() throws InterruptedException {
        int[] coordinates = new int[2];
        while (!terminateFlag && !isAtLocation(coordinates[0], coordinates[1])) {
            coordinates = cr.read();
            Thread.sleep(500);
        }
    }
    public boolean isAtLocation(int x, int y) {
        int[] coords = cr.read();
        return coords[0] == x && coords[1] == y;
    }

    public void useMap(int a, int b) throws InterruptedException {
        account.click(766, 183);
        if (!visited[0]) {
            if (account.hasDialogueBox()) account.click(557, 266);
            visited[0] = true;
        }
        account.click(a, b);
        account.click(766, 183);
        waitUntilStationary();
    }

    public void useIncense() throws InterruptedException {
        if (terminateFlag) return;
        account.click(569, 586);
        if (!visited[1]) {
            if (account.hasDialogueBox()) account.click(557, 266);
            visited[1] = true;
        }
        account.rightClick(450, 367);
        account.click(569, 586);
    }

    public void moveToNextLocation(Location location, int index) throws InterruptedException {
        if (terminateFlag) return;
        int[] mc = location.mapCoordinates[index];
        useMap(mc[0], mc[1]);
    }

    public void findEnemies(Stack<int[]> stack, BufferedImage template, int x, int match) {
        BufferedImage fullScreen = lr.captureWindow(3, 26, 800, 600);
        boolean[][] matched = new boolean[800][600];
        for (int i = 0; i < fullScreen.getWidth(); i++) {
            for (int j = 0; j < fullScreen.getHeight(); j++) {
                if (matched[i][j] || fullScreen.getRGB(i, j) != -985088 || !imageMatch(i + x, j, fullScreen, template, match)) {
                    continue;
                }
                stack.push(new int[] {i + 60, j - 30});
                for (int l = 0; l < template.getWidth(); l++) {
                    for (int m = 0; m < template.getHeight(); m++) {
                        if (i + l >= 0 && j + m >= 0 && i + l < 800 && j + m < 600) {
                            matched[i + l][j + m] = true;
                        }
                    }
                }
            }
        }
    }

    public int[] findExit() {
        BufferedImage screen = cr.captureWindow(227, 280, 45, 90);
        for (int row = 89; row >= 0; row--) {
            int rgb = screen.getRGB(0, row);
            if (rgb == -16711936 && imageMatch(0, row - 2, screen, exit, 120)) {
                return new int[] {251, row + 287};
            }
        }
        return new int[2];
    }

    private boolean imageMatch(int x, int y, BufferedImage screen, BufferedImage template, int match) {
        for (int col = 0; col < template.getWidth(); col++) {
            for (int row = 0; row < template.getHeight(); row++) {
                int color = template.getRGB(col, row);
                if (color == 0x00000000 || col + x < 0 || row + y < 0 || col + x >= screen.getWidth() || row + y >= screen.getHeight()) {
                    continue;
                }
                if (screen.getRGB(col + x, row + y) == color && --match < 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean waitForDialogueBox() throws InterruptedException {
        long start = System.currentTimeMillis();
        while (!terminateFlag && System.currentTimeMillis() - start < 5000) {
            if (account.hasDialogueBox()) {
                return true;
            }
            Thread.sleep(200);
        }
        return false;
    }
}
