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
    public Account[] accounts;
    public Account account;
    public LocationReader lr;
    public CoordinatesReader cr;
    public boolean[] visited;
    public JButton startButton;
    public Queue<int[]>[] itemQueues;
    public int flagHash;
    public Map<Integer, TemplateInfo> itemMap = new HashMap<>();
    public int[] battleOrder;
    public static final int moveBar = 2246481;
    private final int[] pixelHashes = new int[] {2138320, 9474256, 0};
    private static final int[][] battlePositions = new int[][] {
            {7, 188}, {71, 156}, {135, 124}, {199, 92}, {263, 60},
            {71, 220}, {135, 188}, {199, 156}, {263, 124}, {331, 92},
            {135, 252}, {199, 220}, {263, 188}
    };

    private int[] findBattleEnemy() {
        BufferedImage enemies = cr.captureWindow(53, 106, 500, 275);
        for (int i : battleOrder) {
            int x = battlePositions[i][0], y = battlePositions[i][1];
            for (int j = x; j < x + 70; j++) {
                for (int k = y; k < y + 11; k++) {
                    if (enemies.getRGB(j, k) == -11477912) {
                        return new int[] {x + 85, y + 55};
                    }
                }
            }
        }
        return new int[] {223, 179};
    }

    public void battle(boolean catchPet) throws InterruptedException {
        for (int i = 0; catchPet && i < 5; i++) {
            if (accounts[i] == null) continue;
            accounts[i].click(557, 266);
        }
        while (!terminateFlag && account.isInBattle()) {
            while (!terminateFlag && account.getPixelHash(782, 380) != moveBar) {
                if (!account.isInBattle() || account.hasDialogueBox() || account.isRelogged()) {
                    return;
                } else if (account.getPixelHash(746, 229) == Account.petMoveBar) {
                    break;
                }
                Thread.sleep(200);
            }
            Thread[] threads = new Thread[5];
            int[] target = catchPet ? null : findBattleEnemy();
            for (int i = 0; i < 5; i++) {
                if (accounts[i] == null) continue;
                if (catchPet) {
                    threads[i] = new Thread(accounts[i]::catchPet);
                } else {
                    accounts[i].setEnemy(target);
                    threads[i] = new Thread(accounts[i]::execute);
                }
                threads[i].start();
            }
            for (Thread thread : threads) {
                if (thread != null) thread.join();
            }
        }
    }

    public void detectItems(boolean openInventory) throws IOException, InterruptedException {
        if (itemMap.isEmpty()) {
            itemMap.put(-3104736, new TemplateInfo(0));
            itemMap.put(-3108720, new TemplateInfo(1));
            if (flagHash != -1) {
                itemMap.put(flagHash, new TemplateInfo(2, flagHash));
                Color color = new Color(flagHash, true);
                pixelHashes[2] = (color.getBlue() << 16) | (color.getGreen() << 8) | (color.getRed());
            }
        }
        if (openInventory) account.click(569, 586);
        for (Queue<int[]> queue : itemQueues) {
            queue.clear();
        }
        BufferedImage fullScreen = lr.captureWindow(3, 26, 800, 600);
        boolean[][] visited = new boolean[800][600];
        for (int i = 0; i < fullScreen.getWidth(); i++) {
            for (int j = 0; j < fullScreen.getHeight(); j++) {
                int rgb = fullScreen.getRGB(i, j);
                TemplateInfo t = itemMap.get(rgb);
                if (!visited[i][j] && t != null &&
                        imageMatch(i, j, fullScreen, t.template, t.match)) {
                    itemQueues[t.index].offer(new int[] {i + 3, j + 26});
                    for (int k = i; k < i + t.template.getWidth(); k++) {
                        for (int l = j; l < j + t.template.getHeight(); l++) {
                            visited[k][l] = true;
                        }
                    }
                }
            }
        }
        if (openInventory) account.click(569, 586);
    }

    public boolean hasItems(int i) throws IOException, InterruptedException {
        for (int j = 0; j < 3; j++) {
            while (!itemQueues[i].isEmpty()) {
                int x = itemQueues[i].peek()[0], y = itemQueues[i].peek()[1];
                if (account.getPixelHash(x, y) == pixelHashes[i]) {
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
        while (!terminateFlag && account.hasDialogueBox()) {
            BufferedImage screen = cr.captureWindow(227, 280, 1, 180);
            for (int row = screen.getHeight() - 1; row >= 0; row--) {
                int rgb = screen.getRGB(0, row);
                if (rgb == -16711936) return new int[] {251, row + 280};
            }
            Thread.sleep(500);
        }
        return new int[2];
    }

    public boolean imageMatch(int x, int y, BufferedImage screen, BufferedImage template, int match) {
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

    public static class TemplateInfo {
        BufferedImage template;
        int match;
        int index;
        public TemplateInfo(int i) throws IOException {
            if (i == 0) {
                template = ImageIO.read(new File("app/data/incense.png"));
                match = 34;
            } else if (i == 1) {
                template = ImageIO.read(new File("app/data/ticket.png"));
                match = 300;
            }
            this.index = i;
        }
        public TemplateInfo(int i, int hash) throws IOException {
            index = i;
            template = ImageIO.read(new File("app/data/" + hash + ".png"));
            match = 100;
        }
    }

    public void setTerminateFlag() {

    }

    public void run() {

    }
}
