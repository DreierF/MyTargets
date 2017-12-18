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

import de.dreier.mytargets.shared.models.db.Shot

class NoneStrategy : IAggregationStrategy {

    override var color: Int = 0
    override val result: IAggregationResultRenderer = NOPResultRenderer()
    private var resultListener: IAggregationStrategy.OnAggregationResult? = null

    override fun setOnAggregationResultListener(resultListener: IAggregationStrategy.OnAggregationResult) {
        this.resultListener = resultListener
    }

    override fun cleanup() {

    }

    override fun calculate(shots: List<Shot>) {
        resultListener?.onResult()
    }
}
