package de.dreier.mytargets.shared.targets;

public class ScoringStyle {

    public final boolean showAsX;
    public final int[][] points;

    protected ScoringStyle(boolean showAsX, int[][] points) {
        this.showAsX = showAsX;
        this.points = points;
    }

    public ScoringStyle(boolean showAsX, int... points) {
        this(showAsX, new int[][]{points});
    }

    @Override
    public String toString() {
        String style = "";
        for (int i = 0; i < points[0].length; i++) {
            if (i + 1 < points[0].length && points[0][i] <= points[0][i + 1] && !(i == 0 && showAsX)) {
                continue;
            }
            if (!style.isEmpty()) {
                style += ", ";
            }
            style += zoneToString(i, 0);
            for (int a = 1; a < points.length; a++) {
                style += "/" + zoneToString(i, a);
            }
        }
        return style;
    }


    public String zoneToString(int zone, int arrow) {
        if (isOutOfRange(zone)) {
            return "M";
        } else if (zone == 0 && showAsX) {
            return "X";
        } else {
            int value = getPointsByZone(zone, arrow);
            if (value == 0) {
                return "M";
            }
            return String.valueOf(value);
        }
    }

    public int getPointsByZone(int zone, int arrow) {
        if (isOutOfRange(zone)) {
            return 0;
        }
        return getPoints(zone, arrow);
    }

    protected int getPoints(int zone, int arrow) {
        return points[0][zone];
    }

    private boolean isOutOfRange(int zone) {
        return zone < 0 || zone >= points[0].length;
    }

    public int getMaxPoints() {
        int max = 0;
        for (int[] arrowPoints : points) {
            for (int point : arrowPoints) {
                if (point > max)
                    max = point;
            }
        }
        return max;
    }
}
