package de.dreier.mytargets.models;

import android.graphics.Color;

public class Target {
    // Color used for drawing the target rings filling
    public static final int[] highlightColor = {
            0xFFFCEA0F, // yellow
            0xFFE30513, // red
            0xFF1D70B7, // blue
            0xFF050505, // black
            Color.WHITE,  // white
            0xFF1C1C1B // mistake
    };

    // Color used for drawing the indicator line and the circle around the points
    public static final int[] circleStrokeColor = {
            0xFFFCEA0F, // yellow
            0xFFE30513, // red
            0xFF1D70B7, // blue
            0xFF050505, // black
            0xFF1C1C1B, // white gets drawn black
            0xFF1C1C1B // mistake
    };

    // Background color for right bar indicator and circle background
    public static final int[] rectColor = {
            0xFFFCEA0F, // yellow
            0xFFE30513, // red
            0xFF1D70B7, // blue
            0xFF1C1C1B, // black
            Color.WHITE, // withe
            0xFF1C1C1B // mistake
    };

    // Color used for indicating a not selected ring
    public static final int[] grayColor = {
            0xFF7a7439, // yellow
            0xFF7a2621, // red
            0xFF234466, // blue
            0xFF1C1C1B, // black
            0xFF7d7a80 // white
    };

    public static final int[][] intersectionColor = {
            {0xFF473414, 0xFF10110B}, // yellow -> yellow/red
            {0xFF6A1D0B, 0xFF191E38}, // red -> red/blue
            {0xFF1E4C66, 0xFF12243A}, // blue -> blue/black
            {0xFF6A6869, 0xFF1C1C1B}, // black -> black/white
            {0xFF9A9A99, 0xFF505050}}; // white -> white/mistake

    // Indices for target colors starting with the middle one
    public static final int[][] target_rounds = {
            {0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4}, //WA
            {0, 0, 0, 1, 1, 2, 2}, // WA Spot 5-10
            {0, 0, 0, 1, 1, 2}, // WA Spot 65-10
            {0, 0, 0, 1, 1, 2}, // WA 3er Spot
            {0, 0, 3, 3, 3, 3}, // WA Field
            {4, 4, 3, 3, 3, 3},  // DFBV Spiegel
            {4, 4, 3}, // DFBV Spiegel Spot
            {3, 3, 4, 4, 3, 3} // DFBV Field
    };

    // Points for zone
    private static final int[][] target_points = {
            {10, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1}, //WA
            {10, 10, 9, 8, 7, 6, 5}, // WA Spot 5-10
            {10, 10, 9, 8, 7, 6}, // WA Spot 6-10
            {10, 10, 9, 8, 7, 6}, // WA 3er Spot
            {6, 5, 4, 3, 2, 1}, // WA Field
            {5, 5, 4, 3, 2, 1},  // DFBV Spiegel
            {5, 5, 4}, // DFBV Spiegel Spot
            {5, 5, 4, 3, 2, 1} // DFBV Field
    };

    public static String getStringByZone(int target, int zone) {
        final int[] points = target_points[target];
        if (zone <= -1 || zone >= points.length) {
            return "M";
        } else if (zone == 0) {
            return "X";
        } else {
            return String.valueOf(getPointsByZone(target, zone));
        }
    }

    public static int getPointsByZone(int target, int zone) {
        if (target < target_points.length) {
            if (target_points[target].length > zone && zone >= 0) {
                return target_points[target][zone];
            }
        }
        return 0;
    }

    public static int getMaxPoints(int target) {
        if (target < target_points.length) {
            return target_points[target][0];
        } else {
            return 0;
        }
    }

    public static float zoneToX(int target, int zone) {
        int zones = Target.target_points[target].length;
        if (zone < 0) {
            return (zones * 2 + 1) / (float) (zones * 2);
        } else {
            return (zone * 2 + 1) / (float) (zones * 2);
        }
    }
}
