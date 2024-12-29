package Main;

import TextReaders.CoordinatesReader;
import TextReaders.LocationReader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.*;

public abstract class Program {
    public boolean terminateFlag;
    public Account account;
    public LocationReader lr;
    public CoordinatesReader cr;
    public boolean[] visited;
    public JButton startButton;
    public Queue<int[]>[] itemQueues;
    public static Map<Integer, BufferedImage> itemMap = null;

    public void detectItems(boolean openInventory) throws IOException, InterruptedException {
        if (itemMap == null) {
            BufferedImage incense1 = ImageIO.read(new File("app/data/incense1.png"));
            BufferedImage incense2 = ImageIO.read(new File("app/data/incense2.png"));
            BufferedImage ticket = ImageIO.read(new File("app/data/ticket.png"));
            itemMap = new HashMap<>();
            itemMap.put(-991200, incense1);
            itemMap.put(-4153312, incense2);
            itemMap.put(-3108720, ticket);
        }
        if (openInventory) account.click(569, 586);
        BufferedImage fullScreen = lr.captureWindow(3, 26, 800, 600);
        boolean[][] visited = new boolean[800][600];
        for (int i = 0; i < fullScreen.getWidth(); i++) {
            for (int j = 0; j < fullScreen.getHeight(); j++) {
                int rgb = fullScreen.getRGB(i, j);
                if (!visited[i][j] && itemMap.containsKey(rgb) &&
                        imageMatch(i, j, fullScreen, itemMap.get(rgb), 300)) {
                    int index = (rgb == -3108720 ? 1 : 0);
                    itemQueues[index].offer(new int[] {i + 3, j + 26});
                    for (int k = i; k < i + 18; k++) {
                        for (int l = j; l < j + 18; l++) {
                            visited[k][l] = true;
                        }
                    }
                }
            }
        }
        if (openInventory) account.click(569, 586);
    }

    public boolean hasItems(int i) throws IOException, InterruptedException {
        int a = (i == 0 ? 2154736 : 9474256), b = (i == 0 ? 2138304 : 9474256);
        for (int j = 0; j < 2; j++) {
            while (!itemQueues[i].isEmpty()) {
                int x = itemQueues[i].peek()[0], y = itemQueues[i].peek()[1];
                int hash = account.getPixelHash(x, y);
                if (hash == a || hash == b) {
                    return true;
                }
                itemQueues[i].poll();
            }
            detectItems(false);
        }
        return false;
    }

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

    public boolean isAtLocation(int x, int y, String location) {
        int[] coords = cr.read();
        return coords[0] == x && coords[1] == y && location.equals(lr.read());
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

    public void useIncense() throws InterruptedException, IOException {
        if (terminateFlag) return;
        account.click(569, 586);
        if (!visited[1]) {
            if (account.hasDialogueBox()) account.click(557, 266);
            visited[1] = true;
        }
        if (hasItems(0)) account.rightClick(itemQueues[0].peek());
        account.click(569, 586);
    }

    public void moveToNextLocation(Location location, int index) throws InterruptedException {
        if (terminateFlag) return;
        int[] mc = location.mapCoordinates[index];
        useMap(mc[0], mc[1]);
    }

    public void findEnemies(Stack<int[]> stack, BufferedImage enemy, int[] info) {
        BufferedImage fullScreen = lr.captureWindow(3, 26, 800, 600);
        boolean[][] matched = new boolean[800][600];
        for (int i = 0; i < fullScreen.getWidth(); i++) {
            for (int j = 0; j < fullScreen.getHeight(); j++) {
                if (matched[i][j] || fullScreen.getRGB(i, j) != info[0] ||
                        !imageMatch(i + info[2], j + info[3], fullScreen, enemy, info[1])) {
                    continue;
                }
                stack.push(new int[] {i + info[4], j + info[5]});
                for (int l = 0; l < enemy.getWidth(); l++) {
                    for (int m = 0; m < enemy.getHeight(); m++) {
                        if (i + l >= 0 && j + m >= 0 && i + l < 800 && j + m < 600) {
                            matched[i + l][j + m] = true;
                        }
                    }
                }
            }
        }
    }

    public int[] findExit() throws InterruptedException {
        while (account.hasDialogueBox()) {
            BufferedImage screen = cr.captureWindow(227, 280, 1, 180);
            for (int row = screen.getHeight() - 1; row >= 0; row--) {
                int rgb = screen.getRGB(0, row);
                if (rgb == -16711936) return new int[] {251, row + 280};
            }
            Thread.sleep(500);
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

    public boolean waitForDialogueBox(int time) throws InterruptedException {
        long start = System.currentTimeMillis();
        time *= 1000;
        while (!terminateFlag && System.currentTimeMillis() - start < time) {
            if (account.hasDialogueBox()) {
                return true;
            }
            Thread.sleep(200);
        }
        return false;
    }

    public void setTerminateFlag() {

    }

    public void run() {

    }
}
