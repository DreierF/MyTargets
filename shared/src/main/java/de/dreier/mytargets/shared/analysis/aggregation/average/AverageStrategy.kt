/*
 * Copyright (C) 2018 Florian Dreier
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

package de.dreier.mytargets.shared.analysis.aggregation.average

import de.dreier.mytargets.shared.analysis.aggregation.IAggregationResultRenderer
import de.dreier.mytargets.shared.analysis.aggregation.NOPResultRenderer
import de.dreier.mytargets.shared.analysis.aggregation.cluster.AggregationStrategyBase
import de.dreier.mytargets.shared.models.db.Shot

class AverageStrategy : AggregationStrategyBase() {

    override fun compute(shots: List<Shot>): IAggregationResultRenderer {
        if (shots.isEmpty()) {
            return NOPResultRenderer()
        }
        val average = Average()
        average.computeAll(shots)
        val averageResultRenderer = AverageResultRenderer(average)
        averageResultRenderer.onPrepareDraw()
        return averageResultRenderer
    }
}
