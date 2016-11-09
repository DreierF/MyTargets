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

package de.dreier.mytargets.shared.analysis.aggregation;

import android.graphics.PointF;

public class WeightedMovingAverage extends MovingAverage {
    public WeightedMovingAverage(int var1) {
        super(var1);
    }

    protected void computeAverage() {
        double var3 = 0.0D;
        double var1 = 0.0D;
        int var5 = 0;

        for (PointF var7 : data) {
            ++var5;
            var3 += (double) ((float) var5 * var7.x);
            var1 += (double) ((float) var5 * var7.y);
        }

        var5 = (var5 + 1) * var5 / 2;
        average.set((float) (var3 / (double) var5), (float) (var1 / (double) var5));
    }
}
