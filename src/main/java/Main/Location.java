package Main;

public class Location {
    String name;
    int[][] mapCoordinates;
    int[] cth;

    public Location(String location) {
        name = location;
        switch (location) {
            case "bhc":
                mapCoordinates = new int[][]{
                        {598, 193}, {559, 216}, {514, 239}, {525, 292}, {594, 307},
                        {561, 346}, {596, 373}, {561, 431}, {571, 470}, {445, 197},
                        {462, 252}, {457, 304}, {499, 345}, {448, 402}, {384, 449},
                        {307, 470}, {209, 458}, {293, 413}, {373, 398}, {367, 330},
                        {313, 272}, {333, 209}, {279, 332}, {254, 278}, {229, 214}
                };
                cth = new int[]{567, 388, 101, 223, 285, 148};
                break;
            case "ktng":
                mapCoordinates = new int[][]{
                        {570, 296}, {541, 347}, {536, 387}, {573, 455}, {462, 378},
                        {457, 301}, {504, 249}, {576, 223}, {398, 252}, {309, 219},
                        {219, 206}, {277, 264}, {259, 343}, {307, 394}, {225, 433},
                        {332, 469}, {345, 374}, {415, 387}, {459, 451}
                };
                cth = new int[]{275, 313, 22, 136, 315, 235};
                break;
            case "hht":
                mapCoordinates = new int[][]{
                        {615, 247}, {535, 296}, {590, 343}, {617, 423}, {526, 386},
                        {450, 334}, {428, 412}, {330, 376}, {484, 244}, {366, 227},
                        {279, 245}, {182, 237}, {174, 294}, {139, 363}, {254, 361},
                        {247, 413}
                };
                cth = null;
                break;
            case "vdd":
                mapCoordinates = new int[][]{
                        {508, 240}, {451, 205}, {376, 220}, {288, 232}, {252, 216},
                        {268, 266}, {235, 330}, {203, 392}, {281, 425}, {347, 382},
                        {402, 444}, {449, 481}, {472, 396}, {397, 315}, {466, 357},
                        {504, 317}, {561, 272}, {624, 327}, {616, 379}, {542, 350},
                        {566, 433}, {590, 478}
                };
                cth = null;
                break;
            case "khl":
                mapCoordinates = new int[][]{
                        {557, 469}, {541, 402}, {551, 349}, {432, 308}, {381, 282},
                        {492, 287}, {507, 206}, {386, 210}, {300, 190}, {321, 269},
                        {244, 289}, {315, 325}, {314, 396}, {270, 435}, {352, 439},
                        {442, 475}, {441, 406}
                };
                cth = new int[]{553, 341, 81, 178, 624, 255};
                break;
            case "dps":
                mapCoordinates = new int[][]{
                        {621, 215}, {663, 271}, {619, 310}, {555, 352}, {532, 408},
                        {601, 450}, {474, 461}, {680, 436}, {648, 408}, {477, 352},
                        {400, 397}, {352, 447}, {277, 468}, {439, 300}, {362, 346},
                        {382, 270}, {471, 254}, {413, 208}, {325, 261}, {220, 240},
                        {156, 229}, {220, 301}, {188, 363}, {110, 376}
                };
                cth = new int[]{553, 372, 124, 206, 223, 336};
                break;
            case "bvt":
                mapCoordinates = new int[][] {
                        {267, 224}, {353, 232}, {314, 280}, {215, 274}, {229, 362},
                        {277, 422}, {369, 423}, {448, 398}, {504, 433}, {540, 374},
                        {533, 324}, {358, 356}, {419, 256}, {468, 216}, {548, 228},
                        {496, 258}
                };
                cth = new int[] {540, 368, 78, 170, 574, 151};
                break;
            case "vul":
                mapCoordinates = new int[][] {
                        {521, 438}, {443, 460}, {343, 429}, {220, 389}, {270, 365},
                        {322, 309}, {362, 208}, {219, 249}, {458, 203}, {565, 228},
                        {500, 288}, {522, 353}, {430, 370}, {367, 374}, {392, 260}
                };
                cth = new int[] {402, 335, 34, 109, 399, 79};
        }
    }
}
