package Main;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.StdCallLibrary;

import java.awt.*;

public class Account {
    private final int skill;
    private final int pet;
    private final double scale;
    private final HWND handle;
    private boolean terminateFlag;
    private boolean catchPet;
    private final Object lock = new Object();
    public static final Color inMapColor = new Color(90, 46, 2);
    public static final Color moveBar = new Color(81, 71, 34);
    public static final Color petMoveBar = new Color(49, 41, 15);
    public static final Color white = new Color(254, 254, 254);
    private static final Color dialogueBoxColor = new Color(20, 17, 0);
    private static final Color relogColor1 = new Color(89, 80, 50);
    private static final Color relogColor2 = new Color(92, 100, 84);
    private static final Color relogColor3 = new Color(180, 161, 112);
    private static final Color relogColor4 = new Color(21, 39, 82);


    public Account(int skill, int pet, HWND handle, double scale) {
        this.skill = skill;
        this.pet = pet;
        this.scale = scale;
        this.handle = handle;
        this.terminateFlag = handle == null;
        this.catchPet = false;
    }

    public void run() {
        try {
            if (catchPet) click(557, 266);
            while (!terminateFlag && isInBattle()) {
                while (!terminateFlag && !getPixelColor(782, 380).equals(moveBar)) {
                    if (!isInBattle() || hasDialogueBox() || isRelogged()) {
                        return;
                    }
                    Thread.sleep(200);
                }

                if (catchPet) {
                    ropeIn();
                    if (pet < 12 && waitForPetPrompt()) click(760, 246);
                } else {
                    characterAttack();
                    if (pet < 12 && waitForPetPrompt()) petAttack();
                }

                while (!terminateFlag && (getPixelColor(378, 90).equals(white) || getPixelColor(405, 325).equals(white))) {
                    Thread.sleep(200);
                }
            }
            catchPet = false;
        } catch (Exception _) {

        }
    }

    public boolean isRelogged() {
        Color color1 = getPixelColor(553, 301);
        Color color2 = getPixelColor(264, 301);
        Color color3 = getPixelColor(552, 418);
        Color color4 = getPixelColor(384, 391);
        return color1.equals(relogColor1) && color2.equals(relogColor2) && color3.equals(relogColor3) && color4.equals(relogColor4);
    }

    private void ropeIn() throws InterruptedException {
        click(759, 359);
        clickOnNpc(231, 201);
    }

    private void characterAttack() throws InterruptedException {
        if (skill == 11) {
            click(760, 292);
            return;
        }
        if (skill != 0) {
            click(375 + skill * 35, 548);
        }
        Thread.sleep(200);
        clickOnNpc(231, 201);
    }

    private void petAttack() throws InterruptedException {
        if (pet == 11) {
            click(760, 246);
            return;
        }
        if (pet != 0) {
            click(759, 209);
            click(254 + pet * 37, 290);
        }
        Thread.sleep(200);
        clickOnNpc(231, 201);
    }

    private boolean waitForPetPrompt() throws InterruptedException {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 7000 && !terminateFlag) {
            if (getPixelColor(746, 229).equals(petMoveBar)) {
                return true;
            }
            Thread.sleep(200);
        }
        return false;
    }

    public boolean hasDialogueBox() {
        Color color1 = getPixelColor(216, 304);
        Color color2 = getPixelColor(588, 317);
        Color color3 = getPixelColor(419, 248);
        return color1.equals(Color.BLACK) && color2.equals(Color.BLACK) && color3.equals(dialogueBoxColor);
    }

    public boolean isInBattle() {
        return !getPixelColor(778, 38).equals(inMapColor);
    }

    public void setCatchPet() {
        this.catchPet = true;
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
            Color color = getPixelColor(a, b);
            int count = 0;
            do {
                User32.INSTANCE.SendMessage(handle, WinUser.WM_MOUSEMOVE, new WinDef.WPARAM(0), lParam);
                Thread.sleep(200);
            } while (!terminateFlag && count++ < 5 && getPixelColor(a, b).equals(color));
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

    public Color getPixelColor(int x, int y) {
        x -= 3;
        y -= 26;
        // Get the device context of the window
        WinDef.HDC hdc = User32.INSTANCE.GetDC(handle);

        // Get the color of the specified pixel
        int pixelColor = MyGDI32.INSTANCE.GetPixel(hdc, x, y);
        User32.INSTANCE.ReleaseDC(handle, hdc); // Release the DC

        // Return the color as a Color object
        return new Color(pixelColor & 0xFF, (pixelColor >> 8) & 0xFF, (pixelColor >> 16) & 0xFF);
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
