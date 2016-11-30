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

import android.support.annotation.UiThread;

import java.util.List;

import de.dreier.mytargets.shared.analysis.aggregation.cluster.ClusterStrategy;
import de.dreier.mytargets.shared.models.Shot;

public interface IAggregationStrategy<T extends IAggregationResultRenderer> {
    void setOnAggregationResultListener(final ClusterStrategy.OnAggregationResult onAggregationResult);

    void calculate(List<Shot> shots);

    T getResult();

    void cleanup();

    void setColor(int color);

    interface OnAggregationResult {
        @UiThread
        void onResult();
    }
}
