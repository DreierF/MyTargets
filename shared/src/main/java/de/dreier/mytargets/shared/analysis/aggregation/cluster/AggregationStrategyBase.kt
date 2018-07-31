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

package de.dreier.mytargets.shared.analysis.aggregation.cluster

import android.os.AsyncTask
import android.support.annotation.CallSuper
import android.support.annotation.WorkerThread

import de.dreier.mytargets.shared.analysis.aggregation.IAggregationResultRenderer
import de.dreier.mytargets.shared.analysis.aggregation.IAggregationStrategy
import de.dreier.mytargets.shared.analysis.aggregation.NOPResultRenderer
import de.dreier.mytargets.shared.models.db.Shot

abstract class AggregationStrategyBase : IAggregationStrategy {

    override var result: IAggregationResultRenderer = NOPResultRenderer()
    protected var isDirty = false
    private var resultListener: IAggregationStrategy.OnAggregationResult? = null
    private var computeTask: AsyncTask<List<Shot>, Int, IAggregationResultRenderer>? = null
    override var color: Int = 0

    protected val isCancelled: Boolean
        get() = computeTask?.isCancelled ?: true

    @CallSuper
    protected open fun reset() {
        result = NOPResultRenderer()
        computeTask?.cancel(true)
        isDirty = true
    }

    override fun setOnAggregationResultListener(resultListener: IAggregationStrategy.OnAggregationResult) {
        this.resultListener = resultListener
    }

    override fun calculate(shots: List<Shot>) {
        reset()
        computeTask = ComputeTask().execute(shots)
    }

    @WorkerThread
    protected abstract fun compute(shots: List<Shot>): IAggregationResultRenderer

    override fun cleanup() {
        resultListener = null
        computeTask?.cancel(true)

    }

    private inner class ComputeTask : AsyncTask<List<Shot>, Int, IAggregationResultRenderer>() {

        override fun doInBackground(vararg array: List<Shot>): IAggregationResultRenderer {
            return compute(array[0])
        }

        override fun onPostExecute(clusterResultRenderer: IAggregationResultRenderer) {
            super.onPostExecute(clusterResultRenderer)
            clusterResultRenderer.setColor(color)
            result = clusterResultRenderer
            isDirty = false
            resultListener?.onResult()
        }
    }
}
