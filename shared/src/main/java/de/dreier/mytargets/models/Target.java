package de.dreier.mytargets.models;

import android.graphics.Color;

public class Target {
    public static final int[] highlightColor = {
            0xFFFCEA0F, // yellow
            0xFFE30513, // red
            0xFF1D70B7, // blue
            0xFF050505, // black
            Color.WHITE,  // white
            0xFF1C1C1B // mistake
    };

    public static final int[] circleStrokeColor = {
            0xFFFCEA0F, // yellow
            0xFFE30513, // red
            0xFF1D70B7, // blue
            0xFF050505, // black
            0xFF1C1C1B, // white gets drawn black
            0xFF1C1C1B, // mistake
    };

    public static final int[] rectColor = {
            0xFFFCEA0F, // yellow
            0xFFE30513, // red
            0xFF1D70B7, // blue
            0xFF1C1C1B, // black
            Color.WHITE, // withe
            0xFF1C1C1B // mistake
    };

    public static final int[] grayColor = {
            0xFF7a7439, // yellow
            0xFF7a2621, // red
            0xFF234466, // blue
            0xFF1C1C1B, // black
            0xFF7d7a80 // white
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

    private static final int[][] target_points = {
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

    public static String getStringByZone(int target, int zone, boolean compound) {
        final int[] points = target_points[target];
        if (zone <= -1 || zone >= points.length) {
            return "M";
        } else if (zone == 0) {
            return "X";
        } else {
            return String.valueOf(getPointsByZone(target, zone, compound));
        }
    }

    public static int getPointsByZone(int target, int zone, boolean compound) {
        if (target < target_points.length) {
            if (target_points[target].length > zone && zone >= 0) {
                if (target == 4 && compound && zone == 1) {
                    return 9;
                } else {
                    return target_points[target][zone];
                }
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
        if(zone<0) {
            return (zones * 2 + 1) / (float) (zones * 2);
        } else {
            return (zone * 2 + 1) / (float) (zones * 2);
        }
    }

    public static int pointsToZone(int target, int point) {
        for (int i = Target.target_points[target].length - 1; i >= 0; i--) {
            if (Target.target_points[target][i] == point)
                return i;
        }
        return Target.target_points[target].length;
    }
}
