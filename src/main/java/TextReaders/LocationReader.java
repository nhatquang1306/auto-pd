package TextReaders;

import com.sun.jna.Memory;

import com.sun.jna.platform.win32.WinDef.HWND;
import java.util.HashMap;
import java.util.Map;

public class LocationReader extends Reader {
    private static final int[][] locationPoints = new int[][] {
            {58, 7}, {27, 3}, {84, 9}, {1, 6}, {47, 3}, {21, 2}, {20, 8}, {76, 10}, {80, 5},
            {9, 4}, {49, 2}, {29, 7}, {54, 7}, {91, 3}, {9, 0}, {38, 4}, {70, 4}, {99, 9}
    };
    private static final Map<Integer, String> locationHashes = new HashMap<>();
    public LocationReader(HWND handle) {
        initialize();
        this.handle = handle;
    }

    public String read() {
        String location = locationHashes.get(getHash());
        return location == null ? "" : location;
    }

    public int getHash() {
        Memory buffer = getBuffer(658, 33, 112, 14);
        int hash = 0;
        for (int i = 0; i < locationPoints.length; i++) {
            int pixelOffset = (locationPoints[i][1] * 112 + locationPoints[i][0]) * 4;
            int green = buffer.getByte(pixelOffset + 1) & 0xFF;
            int red = buffer.getByte(pixelOffset + 2) & 0xFF;
            if (red >= 240 && green >= 240) hash |= (1 << i);
        }
        return hash;
    }

    private void initialize() {
        if (locationHashes.isEmpty()) {
            String[] locations = new String[]{
                    "kt", "ktng", "bnvp", "hyl", "tgt", "nnl",
                    "bhc", "gn", "ptv", "td", "nl",
                    "tvd", "vdd", "vdnd", "htks", "vdtl", "hsc",
                    "bvt", "vul", "dc",
                    "vmn", "hht", "hhks", "ktp", "hhtq",
                    "dps", "pvl", "tptl", "btdbt",
                    "khl", "htt", "tt",

            };
            int[] hashes = new int[]{
                    36872, 233736, 100748, 65544, 99458, 77390,
                    66217, 33898, 33289, 7681, 33354,
                    32800, 2601, 137768, 393, 68137, 25,
                    6154, 66057, 584,
                    908, 33290, 107406, 2376, 164362,
                    4888, 34312, 99073, 39309,
                    6857, 65609, 4112,
            };
            for (int i = 0; i < locations.length; i++) {
                locationHashes.put(hashes[i], locations[i]);
            }
        }
    }
}
