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

package de.dreier.mytargets.shared.analysis.aggregation.cluster;

import android.graphics.PointF;

import java.util.ArrayList;

import de.dreier.mytargets.shared.analysis.aggregation.average.Average;
import de.dreier.mytargets.shared.models.Shot;

public class Cluster {
    public final ArrayList<Shot> points = new ArrayList<>();
    private final PointF centerOfGroup = new PointF();
    private final int totalNumber;
    private boolean isDirty;
    private double weight = 0.0;
    public double stdDev;

    public Cluster(int paramInt) {
        totalNumber = paramInt;
        isDirty = true;
    }

    private void compute() {
        if (!isDirty) {
            return;
        }
        Average average = new Average();
        average.computeAverage(points);
        average.computeStdDevX(points);
        average.computeStdDevY(points);
        centerOfGroup.set(average.getAverage());
        stdDev = average.getStdDev();
        isDirty = false;
    }

    public void add(Shot paramPointF) {
        points.add(paramPointF);
        isDirty = true;
    }

    public PointF getCenterOfGroup() {
        compute();
        return centerOfGroup;
    }

    public int getSize() {
        return points.size();
    }

    public double getWeight() {
        compute();
        return weight;
    }

    @Override
    public String toString() {
        return "Cluster{" +
                "points=" + points +
                ", centerOfGroup=" + centerOfGroup +
                ", totalNumber=" + totalNumber +
                ", isDirty=" + isDirty +
                ", weight=" + weight +
                '}';
    }
}
