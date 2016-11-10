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

package de.dreier.mytargets.shared.analysis.aggregation.average;

import android.graphics.PointF;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import de.dreier.mytargets.shared.analysis.aggregation.cluster.AggregationStrategyBase;

public class AverageStrategy extends AggregationStrategyBase<AverageResultRenderer> {
    @Override
    protected void add(float x, float y) {
        data.add(new PointF(x, y));
        isDirty = true;
    }

    @Nullable
    @Override
    protected AverageResultRenderer compute(ArrayList<PointF> data) {
        if (data.size() == 0) {
            return null;
        }
        Average average = new CumulativeAverage();
        average.dataPointCount = data.size();
        average.computeAverage(data);
        average.computeNonUniformStdDeviations(data);
        average.computeCenterStdDev(data);
        average.computeStdDevX(data);
        average.computeStdDevY(data);
        average.computeDirectionalVariance(data);
        average.computeWeightedAverage(data);
        final AverageResultRenderer averageResultRenderer = new AverageResultRenderer(average);
        averageResultRenderer.onPrepareDraw();
        return averageResultRenderer;
    }
}
