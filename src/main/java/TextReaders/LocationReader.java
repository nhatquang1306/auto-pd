package TextReaders;

import Main.Account;
import com.sun.jna.Memory;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LocationReader extends Reader {
    private final int a, b, x, y, z;
    private static final int[][] locationPoints = new int[][] {
            {58, 7}, {27, 3}, {84, 9}, {1, 6}, {47, 3}, {21, 2}, {20, 8}, {76, 10}, {80, 5},
            {9, 4}, {49, 2}, {29, 7}, {54, 7}, {91, 3}, {9, 0}, {38, 4}, {70, 4}, {99, 9}
    };
    private static final Map<Integer, String> locationHashes = new HashMap<>();
    public LocationReader(HWND handle, int i) {
        initialize();
        this.handle = handle;
        if (i == 0) {
            a = 658;
            b = 33;
            x = 255;
            y = 255;
            z = 255;
        } else {
            a = 317;
            b = 293;
            x = 240;
            y = 4;
            z = 240;
        }
    }

    public String read() {
        String location = locationHashes.get(getHash());
        if (location != null) {
            if (location.equals("bbd$")) {
                return getBBDLevel();
            } else if (location.equals("bbdt2")) {
                return getPixelHash(659, 36) != 15131358 ? "bbdt1" : "bbdt2";
            }
        }
//        BufferedImage image = cr.captureWindow(659, 36, 1, 1);
//        ImageIO.write(image, "png", new File("screenshot.png"));
//        System.out.println(image.getRGB(0, 0) == -1);

        //15131358
        return location == null ? "" : location;
    }

    public String getBBDLevel() {
        BufferedImage image = captureWindow(430, 295, 6, 9);
        if (image.getRGB(1, 6) == -1047312) {
            return "bbdt2";
        } else if (image.getRGB(5, 0) == -1047312) {
            return "bbdtd";
        } else if (image.getRGB(3, 0) == -1047312) {
            return "bbdt3";
        } else {
            return "bbdt1";
        }
    }

    public int getHash() {
        Memory buffer = getBuffer(a, b, 112, 14);
        int hash = 0;
        for (int i = 0; i < locationPoints.length; i++) {
            int pixelOffset = (locationPoints[i][1] * 112 + locationPoints[i][0]) * 4;
            int green = buffer.getByte(pixelOffset + 1) & 0xFF;
            int red = buffer.getByte(pixelOffset + 2) & 0xFF;
            int blue = buffer.getByte(pixelOffset) & 0xFF;
            if (red == x && green == y && blue == z) hash |= (1 << i);
        }
        return hash;
    }

    private void initialize() {
        if (locationHashes.isEmpty()) {
            String[] locations = new String[]{
                    "bbdt2", "bbdtd", "bbdt3", "bbd$",
                    "kt", "ktng", "bnvp", "hyl", "tgt", "nnl",
                    "bhc", "gn", "ptv", "td", "nl",
                    "tvd", "vdd", "vdnd", "htks", "vdtl", "hsc",
                    "bvt", "vul", "dc",
                    "vmn", "hht", "hhks", "ktp", "hhtq",
                    "dps", "pvl", "tptl", "btdbt",
                    "khl", "htt", "tt",
                    "tttc", "lstk", "lscd", "lm",
                    "bbd", "ktdg", "lssl"
            };
            int[] hashes = new int[]{
                    65671, 71816, 137344, 202888,
                    36872, 233736, 100748, 65544, 66690, 77390,
                    66217, 33898, 33289, 7681, 586,
                    32768, 553, 135720, 66089, 68137, 25,
                    6154, 66057, 72,
                    908, 32778, 106894, 2376, 163850,
                    4888, 34312, 99073, 35213,
                    2761, 65608, 4112,
                    34384, 131080, 8472, 33288,
                    71688, 37000, 8220
            };
            for (int i = 0; i < locations.length; i++) {
                locationHashes.put(hashes[i], locations[i]);
            }
        }
    }

    public int getPixelHash(int x, int y) {
        x -= 3;
        y -= 26;
        // Get the device context of the window
        WinDef.HDC hdc = User32.INSTANCE.GetDC(handle);

        // Get the color of the specified pixel
        int pixelColor = Account.MyGDI32.INSTANCE.GetPixel(hdc, x, y);
        User32.INSTANCE.ReleaseDC(handle, hdc); // Release the DC
        return pixelColor;
    }
}
