/*
 * Copyright (C) 2017 Florian Dreier
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

import android.support.annotation.Nullable;

import java.util.List;

import de.dreier.mytargets.shared.analysis.aggregation.IAggregationResultRenderer;
import de.dreier.mytargets.shared.analysis.aggregation.NOPResultRenderer;
import de.dreier.mytargets.shared.analysis.aggregation.cluster.AggregationStrategyBase;
import de.dreier.mytargets.shared.models.db.Shot;

public class AverageStrategy extends AggregationStrategyBase {

    @Nullable
    @Override
    protected IAggregationResultRenderer compute(List<Shot> shots) {
        if (shots.size() == 0) {
            return new NOPResultRenderer();
        }
        Average average = new Average();
        average.computeAll(shots);
        final AverageResultRenderer averageResultRenderer = new AverageResultRenderer(average);
        averageResultRenderer.onPrepareDraw();
        return averageResultRenderer;
    }
}
