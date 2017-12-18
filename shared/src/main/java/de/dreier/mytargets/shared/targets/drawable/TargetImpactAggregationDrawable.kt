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

package de.dreier.mytargets.shared.targets.drawable

import android.support.annotation.ColorInt
import de.dreier.mytargets.shared.analysis.aggregation.EAggregationStrategy
import de.dreier.mytargets.shared.analysis.aggregation.EAggregationStrategy.NONE
import de.dreier.mytargets.shared.analysis.aggregation.IAggregationStrategy
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.models.db.Shot
import java.util.*

class TargetImpactAggregationDrawable(target: Target) : TargetImpactDrawable(target), IAggregationStrategy.OnAggregationResult {

    private val faceAggregations = ArrayList<IAggregationStrategy>()
    private var resultsMissing = 0

    init {
        setAggregationStrategy(NONE)
    }

    fun setAggregationStrategy(aggregation: EAggregationStrategy) {
        faceAggregations.clear()
        for (i in 0 until model.faceCount) {
            val strategy = aggregation.newInstance()
            strategy.setOnAggregationResultListener(this)
            faceAggregations.add(strategy)
        }
        setColor(-0x55555556)
        recalculateAggregation()
    }

    fun setColor(@ColorInt color: Int) {
        for (faceAggregation in faceAggregations) {
            faceAggregation.color = color
        }
    }

    override fun onResult() {
        resultsMissing--
        if (resultsMissing == 0) {
            invalidateSelf()
        }
    }

    override fun onPostDraw(canvas: CanvasWrapper, faceIndex: Int) {
        super.onPostDraw(canvas, faceIndex)
        val result = faceAggregations[faceIndex].result
        result.onDraw(canvas)
    }

    override fun cleanup() {
        for (cluster in faceAggregations) {
            cluster.cleanup()
        }
    }

    override fun notifyArrowSetChanged() {
        super.notifyArrowSetChanged()
        recalculateAggregation()
    }

    private fun recalculateAggregation() {
        resultsMissing = model.faceCount
        for (faceIndex in 0 until model.faceCount) {
            val combinedList = ArrayList<Shot>()
            combinedList.addAll(transparentShots[faceIndex])
            combinedList.addAll(shots[faceIndex])
            faceAggregations[faceIndex].calculate(combinedList)
        }
    }
}
