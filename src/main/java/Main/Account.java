package Main;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.StdCallLibrary;

import java.awt.*;
import java.util.Arrays;

public class Account {
    private final int skill;
    private final int pet;
    private final double scale;
    private final HWND handle;
    private boolean terminateFlag;
    private final Object lock = new Object();
    public static final int inMapColor = 142938;
    public static final int petMoveBar = 993585;
    private int[] enemy;
//    public static final int white = 16711422;
    private static final int[] dialogueBoxColors = new int[] {0, 0, 4372};
    private static final int[] relogColors = new int[] {3297369, 5530716, 7381428, 5383957};


    public Account(int skill, int pet, HWND handle, double scale, boolean isVTTD) {
        this.skill = skill;
        this.pet = pet;
        this.scale = scale;
        this.handle = handle;
        this.terminateFlag = handle == null;
    }

    public void setEnemy(int[] enemy) {
        this.enemy = enemy;
    }

    public void catchPet() {
        try {
            if (getPixelHash(746, 229) != petMoveBar) {
                click(759, 359);
                clickOnNpc(223, 179);
            }
            if (pet < 12 && waitForPetPrompt()) click(760, 246);
        } catch (Exception _) {

        }
    }

    public void execute() {
        try {
            characterAttack(enemy[0], enemy[1]);
            if (pet < 12 && waitForPetPrompt()) petAttack(enemy[0], enemy[1]);
        } catch (Exception _) {

        }

    }

    public boolean isRelogged() {
        int[] hashes = new int[] {getPixelHash(553, 301), getPixelHash(264, 301),
                getPixelHash(552, 418), getPixelHash(384, 391)};
        return Arrays.equals(hashes, relogColors);
    }

    private void characterAttack(int a, int b) throws InterruptedException {
        if (getPixelHash(746, 229) != petMoveBar) {
            if (skill == 11) {
                click(760, 292);
                return;
            }
            if (skill != 0) {
                click(375 + skill * 35, 548);
            }
            Thread.sleep(200);
            clickOnNpc(a, b);
        }
    }

    private void petAttack(int a, int b) throws InterruptedException {
        if (pet == 11) {
            click(760, 246);
            return;
        }
        if (pet != 0) {
            click(759, 209);
            click(254 + pet * 37, 290);
        }
        Thread.sleep(200);
        clickOnNpc(a, b);
    }

    private boolean waitForPetPrompt() throws InterruptedException {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 3000 && !terminateFlag) {
            if (getPixelHash(746, 229) == petMoveBar) {
                return true;
            }
            Thread.sleep(200);
        }
        return false;
    }

    public boolean hasDialogueBox() {
        int[] hashes = new int[] {getPixelHash(216, 304), getPixelHash(588, 317), getPixelHash(419, 248)};
        return Arrays.equals(hashes, dialogueBoxColors);
    }

    public boolean isInBattle() {
        return getPixelHash(778, 38) != inMapColor;
    }

    public void setTerminateFlag() {
        this.terminateFlag = true;
    }

    public void click(int a, int b) throws InterruptedException {
        synchronized (lock) {
            long x = (long)Math.floor((a - 3) * scale);
            long y = (long)Math.floor((b - 26) * scale);
            WinDef.LPARAM lParam = new WinDef.LPARAM((y << 16) | (x & 0xFFFF));
            User32.INSTANCE.SendMessage(handle, WinUser.WM_MOUSEMOVE, new WinDef.WPARAM(0), lParam);
            Thread.sleep(200);
            User32.INSTANCE.SendMessage(handle, WinUser.WM_LBUTTONDOWN, new WinDef.WPARAM(WinUser.MK_LBUTTON), lParam);
            Thread.sleep(100);
            User32.INSTANCE.SendMessage(handle, WinUser.WM_LBUTTONUP, new WinDef.WPARAM(0), lParam);
            Thread.sleep(300);
        }
    }

    public void clickOnNpc(int a, int b) throws InterruptedException {
        synchronized (lock) {
            long x = (long)Math.floor((a - 3) * scale);
            long y = (long)Math.floor((b - 26) * scale);
            WinDef.LPARAM lParam = new WinDef.LPARAM((y << 16) | (x & 0xFFFF));
            int hash = getPixelHash(a, b);
            int count = 0;
            do {
                User32.INSTANCE.SendMessage(handle, WinUser.WM_MOUSEMOVE, new WinDef.WPARAM(0), lParam);
                Thread.sleep(200);
            } while (!terminateFlag && count++ < 5 && getPixelHash(a, b) == hash);
            User32.INSTANCE.SendMessage(handle, WinUser.WM_LBUTTONDOWN, new WinDef.WPARAM(WinUser.MK_LBUTTON), lParam);
            Thread.sleep(100);
            User32.INSTANCE.SendMessage(handle, WinUser.WM_LBUTTONUP, new WinDef.WPARAM(0), lParam);
            Thread.sleep(300);
        }
    }

    public void rightClick(int a, int b) throws InterruptedException {
        synchronized (lock) {
            long x = (long)Math.floor((a - 3) * scale);
            long y = (long)Math.floor((b - 26) * scale);
            WinDef.LPARAM lParam = new WinDef.LPARAM((y << 16) | (x & 0xFFFF));
            User32.INSTANCE.SendMessage(handle, WinUser.WM_MOUSEMOVE, new WinDef.WPARAM(0), lParam);
            Thread.sleep(200);
            User32.INSTANCE.SendMessage(handle, WinUser.WM_RBUTTONDOWN, new WinDef.WPARAM(WinUser.MK_RBUTTON), lParam);
            Thread.sleep(100);
            User32.INSTANCE.SendMessage(handle, WinUser.WM_RBUTTONUP, new WinDef.WPARAM(0), lParam);
            Thread.sleep(300);
        }
    }

    public void mouseMove(int a, int b) throws InterruptedException {
        synchronized (lock) {
            long x = (long)Math.floor((a - 3) * scale);
            long y = (long)Math.floor((b - 26) * scale);
            WinDef.LPARAM lParam = new WinDef.LPARAM((y << 16) | (x & 0xFFFF));
            User32.INSTANCE.SendMessage(handle, WinUser.WM_MOUSEMOVE, new WinDef.WPARAM(0), lParam);
            Thread.sleep(200);
        }
    }

    public void click(int[] arr) throws InterruptedException {
        click(arr[0], arr[1]);
    }

    public void clickOnNpc(int[] arr) throws InterruptedException {
        clickOnNpc(arr[0], arr[1]);
    }

    public void rightClick(int[] arr) throws InterruptedException {
        rightClick(arr[0], arr[1]);
    }

    public void mouseMove(int[] arr) throws InterruptedException {
        mouseMove(arr[0], arr[1]);
    }

    public void clickRandomLocation(int xStart, int xLength, int yStart, int yLength) throws InterruptedException {
        int x, y, hash;
        do {
            x = xStart + (int)(Math.random() * (xLength + 1));
            y = yStart + (int)(Math.random() * (yLength + 1));
            hash = getPixelHash(x, y);
            mouseMove(x, y);
        } while (!terminateFlag && getPixelHash(x, y) != hash);
        click(x, y);
    }

    public int[] getMouseLocation() throws InterruptedException {
        Thread.sleep(2000);
        WinDef.RECT r = new WinDef.RECT();
        User32.INSTANCE.GetWindowRect(handle, r);
        Rectangle rect = r.toRectangle();
        rect.x = (int) Math.round(rect.x / scale);
        rect.y = (int) Math.round(rect.y / scale);
        Point m = MouseInfo.getPointerInfo().getLocation();
        return new int[]{m.x - rect.x, m.y - rect.y};
    }

    public int getPixelHash(int x, int y) {
        x -= 3;
        y -= 26;
        // Get the device context of the window
        WinDef.HDC hdc = User32.INSTANCE.GetDC(handle);

        // Get the color of the specified pixel
        int pixelColor = MyGDI32.INSTANCE.GetPixel(hdc, x, y);
        User32.INSTANCE.ReleaseDC(handle, hdc); // Release the DC
        return pixelColor;
    }

    public interface MyGDI32 extends StdCallLibrary {
        MyGDI32 INSTANCE = Native.load("gdi32", MyGDI32.class);

        int GetPixel(WinDef.HDC hdc, int nXPos, int nYPos);
    }

    public interface WinUser {
        int WM_LBUTTONDOWN = 0x0201; // Left mouse button down
        int WM_LBUTTONUP = 0x0202; // Left mouse button up
        int MK_LBUTTON = 0x0001; // Left button state
        int WM_RBUTTONDOWN = 0x0204; // Right mouse button down
        int WM_RBUTTONUP = 0x0205;
        int MK_RBUTTON = 0x0002;
        int WM_MOUSEMOVE = 0x0200;
        int WM_KEYDOWN = 0x0100;
        int WM_KEYUP = 0x0101;
    }
}
