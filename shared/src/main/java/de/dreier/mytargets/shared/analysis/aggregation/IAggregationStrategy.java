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

import java.util.List;

import de.dreier.mytargets.shared.models.Shot;

public interface IAggregationStrategy {
    void registerOnAggregationResultListener(final ClusterStrategy.OnAggregationResult onAggregationResult);
    void unregisterOnAggregationResultListener(final ClusterStrategy.OnAggregationResult onAggregationResult);

    void calculate(List<Shot> shots);

    interface OnAggregationResult {
        void onResult();
        void onProgressUpdate(int paramInt);
    }

    class AggregationDrawable {
    }
}
