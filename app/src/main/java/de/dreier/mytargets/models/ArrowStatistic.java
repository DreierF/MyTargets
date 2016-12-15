/*
 * Copyright (C) 2016 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.models;

import android.support.annotation.NonNull;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.Target;

@Parcel
public class ArrowStatistic implements Comparable<ArrowStatistic> {

    private static final int[] BG_COLORS = {0xFFF44336, 0xFFFF5722, 0xFFFF9800, 0xFFFFC107, 0xFFFFEB3B, 0xFFCDDC39, 0xFF8BC34A, 0xFF4CAF50};
    private static final int[] TEXT_COLORS = {0xFFFFFFFF, 0xFFFFFFFF, 0xFF000002, 0xFF000002, 0xFF000002, 0xFF000002, 0xFF000002, 0xFF000002};
    public String arrowName;
    public int arrowNumber;
    public int count = 0;
    public float xSum = 0;
    public float ySum = 0;
    public float reachedPointsSum = 0;
    public float maxPointsSum = 0;
    public Target target;
    public ArrayList<Shot> shots = new ArrayList<>();
    public Dimension arrowDiameter = new Dimension(5, Dimension.Unit.MILLIMETER);

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
        return BG_COLORS[((int) Math
                .ceil(reachedPointsSum * (BG_COLORS.length - 1) / maxPointsSum))];
    }

    public int getAppropriateTextColor() {
        return TEXT_COLORS[((int) Math
                .ceil(reachedPointsSum * (TEXT_COLORS.length - 1) / maxPointsSum))];
    }

    @Override
    public int compareTo(@NonNull ArrowStatistic another) {
        return Float.compare(another.avgPoints(), avgPoints());
    }

    public void addShot(Shot shot) {
        reachedPointsSum += target.getPointsByZone(shot.zone, shot.index);
        maxPointsSum += target.getMaxPoints();
        xSum += shot.x;
        ySum += shot.y;
        shots.add(shot);
        count++;
    }

    public void addShots(List<Shot> shots) {
        for (Shot shot : shots) {
            addShot(shot);
        }
    }
}
