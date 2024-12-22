package Main;

public class Location {
    int[][] mapCoordinates;
    int[][] coordinates;
    int[] cth;
    String nextMap;
    int[] nextMapInfo;
    public Location(String location) {
        switch (location) {
            case "bhc":
                mapCoordinates = new int[][] {
                        {598, 193}, {559, 216}, {514, 239}, {525, 292}, {594, 307},
                        {561, 346}, {596, 373}, {561, 431}, {571, 470}, {445, 197},
                        {462, 252}, {457, 304}, {499, 345}, {448, 402}, {384, 449},
                        {307, 470}, {209, 458}, {293, 413}, {373, 398}, {367, 330},
                        {313, 272}, {333, 209}, {279, 332}, {254, 278}, {229, 214}
                };
                coordinates = new int[][] {
                        {109, 32}, {99, 54}, {88, 77}, {91, 128}, {108, 144},
                        {100, 182}, {108, 209}, {99, 265}, {102, 303}, {71, 35},
                        {75, 89}, {74, 140}, {84, 181}, {72, 236}, {56, 283},
                        {37, 303}, {13, 292}, {34, 248}, {53, 233}, {52, 166},
                        {38, 109}, {43, 47}, {30, 167}, {24, 115}, {18, 52}
                };
                nextMap = "ktng";
                nextMapInfo = new int[] {439, 171, 64, 270};
                cth = new int[] {567, 388, 101, 223, 285, 148};
                break;
            case "ktng":
                mapCoordinates = new int[][] {
                        {570, 296}, {541, 347}, {536, 387}, {573, 455}, {462, 378},
                        {457, 301}, {504, 249}, {576, 223}, {398, 252}, {309, 219},
                        {219, 206}, {277, 264}, {259, 343}, {307, 394}, {225, 433},
                        {332, 469}, {345, 374}, {415, 387}, {459, 451}
                };
                coordinates = new int[][] {
                        {88, 121}, {82, 166}, {81, 202}, {89, 264}, {64, 195},
                        {63, 126}, {73, 79}, {90, 56}, {50, 82}, {30, 52},
                        {9, 41}, {23, 92}, {18, 163}, {29, 209}, {12, 244},
                        {35, 276}, {37, 191}, {53, 203}, {64, 260}
                };
                nextMap = "bhc";
                nextMapInfo = new int[] {471, 478, 71, 33};
                cth = new int[] {275, 313, 22, 136, 315, 235};
                break;
            case "hht":
                mapCoordinates = new int[][] {
                        {615, 247}, {535, 296}, {590, 343}, {617, 423}, {526, 386},
                        {450, 334}, {428, 412}, {330, 376}, {484, 244}, {366, 227},
                        {279, 245}, {182, 237}, {174, 294}, {139, 363}, {254, 361},
                        {247, 413}
                };
                coordinates = new int[][] {
                        {102, 49}, {85, 89}, {96, 129}, {102, 195}, {83, 165},
                        {68, 122}, {63, 187}, {42, 157}, {75, 46}, {50, 32},
                        {32, 48}, {12, 40}, {10, 88}, {3, 146}, {27, 144},
                        {25, 187}
                };
                cth = null;
                break;
            case "vdd":
                mapCoordinates = new int[][] {
                        {625, 189}, {572, 222}, {508, 240}, {451, 205}, {376, 220},
                        {288, 232}, {215, 197}, {268, 266}, {235, 330}, {203, 392},
                        {206, 463}, {281, 425}, {347, 382}, {402, 444}, {449, 481},
                        {472, 396}, {397, 315}, {466, 357}, {504, 317}, {561, 272},
                        {624, 327}, {616, 379}, {542, 350}, {566, 433}, {590, 478}
                };
                coordinates = new int[][] {
                        {123, 44}, {109, 78}, {92, 97}, {77, 61}, {58, 76},
                        {34, 89}, {16, 52}, {30, 124}, {21, 192}, {13, 256},
                        {13, 330}, {33, 290}, {50, 246}, {65, 310}, {77, 348},
                        {83, 260}, {63, 175}, {81, 219}, {91, 177}, {106, 131},
                        {123, 188}, {121, 242}, {101, 212}, {107, 299}, {114, 346}
                };
                cth = null;
                break;
            case "khl":
                mapCoordinates = new int[][] {
                        {557, 469}, {541, 402}, {551, 349}, {432, 308}, {381, 282},
                        {492, 287}, {507, 206}, {386, 210}, {300, 190}, {321, 269},
                        {244, 289}, {315, 325}, {314, 396}, {270, 435}, {352, 439},
                        {442, 475}, {441, 406}
                };
                coordinates = new int[][] {
                        {81, 301}, {77, 237}, {80, 186}, {51, 147}, {39, 121},
                        {66, 126}, {70, 48}, {40, 52}, {19, 33}, {24, 109},
                        {6, 128}, {23, 163}, {23, 230}, {12, 269}, {32, 272},
                        {54, 306}, {53, 241}
                };
                cth = new int[] {553, 341, 81, 178, 624, 255};
                break;
            case "dps":
                mapCoordinates = new int[][] {
                        {621, 215}, {663, 271} ,{619, 310}, {555, 352}, {532, 408},
                        {601, 450}, {474, 461}, {680, 436}, {648, 408}, {477, 352},
                        {400, 397}, {352, 447}, {277, 468}, {439, 300}, {362, 346},
                        {382, 270}, {471, 254}, {413, 208}, {325, 261}, {220, 240},
                        {156, 229}, {220, 301}, {188, 363}, {110, 376}
                };
                coordinates = new int[][] {
                        {141, 41}, {152, 101}, {141, 140}, {124, 185}, {118, 243},
                        {136, 287}, {103, 298}, {157, 272}, {148, 243}, {104, 184},
                        {84, 232}, {71, 283}, {52, 306}, {94, 130}, {74, 178},
                        {79, 99}, {102, 83}, {87, 35}, {64, 89}, {37, 68},
                        {20, 57}, {37, 131}, {29, 196}, {8, 209}
                };
                cth = new int[] {553, 372, 124, 206, 223, 336};
        }
    }
}
