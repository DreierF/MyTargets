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

package de.dreier.mytargets.shared.analysis.aggregation.cluster;

import android.os.AsyncTask;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.analysis.aggregation.IAggregationResultRenderer;
import de.dreier.mytargets.shared.analysis.aggregation.IAggregationStrategy;
import de.dreier.mytargets.shared.models.Shot;

public abstract class AggregationStrategyBase<R extends IAggregationResultRenderer> implements IAggregationStrategy<R> {
    protected final ArrayList<Shot> data;
    protected R result;
    protected boolean isDirty;
    private OnAggregationResult resultListener;
    private AsyncTask<List<Shot>, Integer, R> computeTask;
    private int color;

    public AggregationStrategyBase() {
        this.data = new ArrayList<>();
    }

    @CallSuper
    protected void reset() {
        data.clear();
        result = null;
        if (computeTask != null) {
            computeTask.cancel(true);
        }
        isDirty = true;
    }

    @Override
    public R getResult() {
        return result;
    }

    @Override
    public void setOnAggregationResultListener(final OnAggregationResult resultListener) {
        this.resultListener = resultListener;
    }

    @Override
    public void calculate(List<Shot> shots) {
        reset();
        computeTask = new ComputeTask().execute(shots);
    }

    @WorkerThread
    @Nullable
    protected abstract R compute(List<Shot> shots);

    @Override
    public void cleanup() {
        resultListener = null;
        if (computeTask != null) {
            computeTask.cancel(true);
        }
    }

    @Override
    public void setColor(int color) {
        this.color = color;
    }

    protected boolean isCancelled() {
        return computeTask.isCancelled();
    }

    private class ComputeTask extends AsyncTask<List<Shot>, Integer, R> {

        protected R doInBackground(final List<Shot>... array) {
            return compute(array[0]);
        }

        protected void onPostExecute(final R clusterResultRenderer) {
            super.onPostExecute(clusterResultRenderer);
            if (clusterResultRenderer != null) {
                clusterResultRenderer.setColor(color);
            }
            result = clusterResultRenderer;
            isDirty = false;
            if (resultListener != null) {
                resultListener.onResult();
            }
        }
    }
}
