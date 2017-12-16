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

package de.dreier.mytargets.shared.analysis.aggregation;

import de.dreier.mytargets.shared.analysis.aggregation.average.AverageStrategy;
import de.dreier.mytargets.shared.analysis.aggregation.cluster.ClusterStrategy;

public enum EAggregationStrategy {
    NONE(NoneStrategy.class),
    AVERAGE(AverageStrategy.class),
    CLUSTER(ClusterStrategy.class);
    public final Class<? extends IAggregationStrategy> strategyClass;

    EAggregationStrategy(Class<? extends IAggregationStrategy> strategyClass) {
        this.strategyClass = strategyClass;
    }

    public IAggregationStrategy newInstance() {
        try {
            return strategyClass.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(
                    "Strategy must have zero argument constructor!");
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(
                    "Strategy must have a public zero argument constructor!");
        }
    }
}