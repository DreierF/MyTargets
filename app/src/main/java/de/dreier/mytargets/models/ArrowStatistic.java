package de.dreier.mytargets.models;

import android.support.annotation.NonNull;

import de.dreier.mytargets.shared.models.Arrow;

public class ArrowStatistic implements Comparable<ArrowStatistic> {
    public Arrow arrow;
    public int arrowNumber;
    public int count = 0;
    public float xSum = 0;
    public float ySum = 0;
    public float pointsSum = 0;

    public float avgX() {
        return xSum / count;
    }

    public float avgY() {
        return ySum / count;
    }

    public float avgPoints() {
        return pointsSum / count;
    }

    @Override
    public int compareTo(@NonNull ArrowStatistic another) {
        return Float.compare(another.avgPoints(),avgPoints());
    }
}
