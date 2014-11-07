package de.dreier.mytargets;

import android.graphics.Color;

public class Target {
    public static final int[] highlightColor = {
            0xFFFCEA0F, // Gelb
            0xFFE30513, // Rot
            0xFF1D70B7, //Blau
            0xFF050505, // Schwarz
            Color.WHITE,  // Weiß
            0xFF1C1C1B // Mistake
    };

    public static final int[] circleStrokeColor = {
            0xFFFCEA0F, // Gelb
            0xFFE30513, // Rot
            0xFF1D70B7, //Blau
            0xFF050505, // Schwarz
            0xFF1C1C1B, // Weiß wird schwarz dargestellt
            0xFF1C1C1B, // Mistake
    };

    public static final int[] rectColor = {
            0xFFFCEA0F, // Gelb
            0xFFE30513, // Rot
            0xFF1D70B7, //Blau
            0xFF1C1C1B, // Schwarz
            Color.WHITE, // Weiß
            0xFF1C1C1B // Mistake
    };

    public static final int[] grayColor = {
            0xFF7a7439, // Gelb
            0xFF7a2621, // Rot
            0xFF234466, //Blau
            0xFF1C1C1B, // Schwarz
            0xFF7d7a80 // Weiß
    };

    public static final int[][] target_rounds = {
            {0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4}, //WA
            {0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4},
            {0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4},
            {0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4},
            {0, 0, 0, 1, 1, 2}, // WA Spot
            {0, 0, 0, 1, 1, 2},
            {0, 0, 0, 1, 1, 2},
            {0, 0, 0, 1, 1, 2},
            {0, 0, 3, 3, 3, 3}, // WA Field
            {4, 4, 3, 3, 3, 3},  //DFBV Spiegel
            {4, 4, 3} //DFBV Spiegel Spot
    };

    public static final int[][] target_points = {
            {10, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1}, //WA
            {10, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1},
            {10, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1},
            {10, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1},
            {10, 10, 9, 8, 7, 6}, // WA Spot
            {10, 10, 9, 8, 7, 6},
            {10, 10, 9, 8, 7, 6},
            {10, 10, 9, 8, 7, 6},
            {5, 5, 4, 3, 2, 1}, // WA Field
            {5, 5, 4, 3, 2, 1},  //DFBV Spiegel
            {5, 5, 4} //DFBV Spiegel Spot
    };

    public static String getStringByZone(int target, int zone) {
        final int[] points = target_points[target];
        if (zone <= -1 || zone >= points.length) {
            return "M";
        } else if (zone == 0 && target < 8) {
            return "X";
        } else {
            return String.valueOf(points[zone]);
        }
    }
}
