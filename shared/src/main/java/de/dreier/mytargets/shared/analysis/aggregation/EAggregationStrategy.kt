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

package de.dreier.mytargets.shared.analysis.aggregation

import de.dreier.mytargets.shared.analysis.aggregation.average.AverageStrategy
import de.dreier.mytargets.shared.analysis.aggregation.cluster.ClusterStrategy

enum class EAggregationStrategy(private val strategyClass: Class<out IAggregationStrategy>) {
    NONE(NoneStrategy::class.java),
    AVERAGE(AverageStrategy::class.java),
    CLUSTER(ClusterStrategy::class.java);

    fun newInstance(): IAggregationStrategy {
        try {
            return strategyClass.newInstance()
        } catch (e: InstantiationException) {
            throw IllegalArgumentException(
                    "Strategy must have zero argument constructor!")
        } catch (e: IllegalAccessException) {
            throw IllegalArgumentException(
                    "Strategy must have a public zero argument constructor!")
        }

    }
}
