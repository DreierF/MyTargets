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

package de.dreier.mytargets.shared.analysis.aggregation.cluster;

import android.os.AsyncTask;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import java.util.List;

import de.dreier.mytargets.shared.analysis.aggregation.IAggregationResultRenderer;
import de.dreier.mytargets.shared.analysis.aggregation.IAggregationStrategy;
import de.dreier.mytargets.shared.analysis.aggregation.NOPResultRenderer;
import de.dreier.mytargets.shared.models.db.Shot;

public abstract class AggregationStrategyBase implements IAggregationStrategy {

    @NonNull
    private IAggregationResultRenderer result = new NOPResultRenderer();
    protected boolean isDirty;
    @Nullable
    private OnAggregationResult resultListener;
    private AsyncTask<List<Shot>, Integer, IAggregationResultRenderer> computeTask;
    private int color;

    @CallSuper
    protected void reset() {
        result = new NOPResultRenderer();
        if (computeTask != null) {
            computeTask.cancel(true);
        }
        isDirty = true;
    }

    @NonNull
    @Override
    public IAggregationResultRenderer getResult() {
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
    @NonNull
    protected abstract IAggregationResultRenderer compute(List<Shot> shots);

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
        return computeTask == null || computeTask.isCancelled();
    }

    private class ComputeTask extends AsyncTask<List<Shot>, Integer, IAggregationResultRenderer> {

        @NonNull
        protected IAggregationResultRenderer doInBackground(final List<Shot>... array) {
            return compute(array[0]);
        }

        protected void onPostExecute(@NonNull final IAggregationResultRenderer clusterResultRenderer) {
            super.onPostExecute(clusterResultRenderer);
            clusterResultRenderer.setColor(color);
            result = clusterResultRenderer;
            isDirty = false;
            if (resultListener != null) {
                resultListener.onResult();
            }
        }
    }
}
