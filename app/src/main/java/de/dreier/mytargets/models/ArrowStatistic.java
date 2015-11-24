package de.dreier.mytargets.models;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.target.Target;

public class ArrowStatistic implements Comparable<ArrowStatistic>, Serializable {

    public String arrowName;
    public int arrowNumber;
    public int count = 0;
    public float xSum = 0;
    public float ySum = 0;
    public float reachedPointsSum = 0;
    public float maxPointsSum = 0;
    private static final int[] BG_COLORS = {0xFFF44336, 0xFFFF5722, 0xFFFF9800, 0xFFFFC107, 0xFFFFEB3B, 0xFFCDDC39, 0xFF8BC34A, 0xFF4CAF50};
    private static final int[] TEXT_COLORS = {0xFFFFFFFF, 0xFFFFFFFF, 0xFF000002, 0xFF000002, 0xFF000002, 0xFF000002, 0xFF000002, 0xFF000002};
    public Target target;
    public ArrayList<Shot> shots = new ArrayList<>();

    public float avgX() {
        return xSum / count;
    }

    public float avgY() {
        return ySum / count;
    }

    public float avgPoints() {
        return reachedPointsSum / count;
    }

    public int getAppropriateBgColor() {
        return BG_COLORS[((int) Math.ceil(reachedPointsSum * (BG_COLORS.length-1) / maxPointsSum))];
    }

    public int getAppropriateTextColor() {
        return TEXT_COLORS[((int) Math.ceil(reachedPointsSum * (TEXT_COLORS.length-1) / maxPointsSum))];
    }

    @Override
    public int compareTo(@NonNull ArrowStatistic another) {
        return Float.compare(another.avgPoints(), avgPoints());
    }
}
