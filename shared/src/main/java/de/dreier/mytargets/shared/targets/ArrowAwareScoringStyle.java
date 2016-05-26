package de.dreier.mytargets.shared.targets;

public class ArrowAwareScoringStyle extends ScoringStyle {

    public ArrowAwareScoringStyle(boolean showAsX, int[][] points) {
        super(showAsX, points);
    }

    protected int getPoints(int zone, int arrow) {
        return points[arrow < points.length ? arrow : points.length - 1][zone];
    }
}
