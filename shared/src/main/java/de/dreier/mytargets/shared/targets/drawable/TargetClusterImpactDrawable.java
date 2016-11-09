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

package de.dreier.mytargets.shared.targets.drawable;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.util.Log;

import java.util.ArrayList;

import de.dreier.mytargets.shared.analysis.aggregation.Cluster;
import de.dreier.mytargets.shared.analysis.aggregation.ClusterStrategy;
import de.dreier.mytargets.shared.analysis.aggregation.IAggregationStrategy;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.utils.PathUtils;

public class TargetClusterImpactDrawable extends TargetImpactDrawable implements IAggregationStrategy.OnAggregationResult {

    private static final int N_CLUSTERS = 4;
    private static final String TAG = "TargetClusterImpactDraw";
    private final Path[] clusterPaths;
    private final Path[] drawClusterPaths;
    private final Paint[] innerPaints;
    private ArrayList<ClusterStrategy> faceAggregations;

    public TargetClusterImpactDrawable(Target target) {
        super(target);
        faceAggregations = new ArrayList<>();
        for (int i = 0; i < model.getFaceCount(); i++) {
            final ClusterStrategy clusterStrategy = new ClusterStrategy(N_CLUSTERS);
            clusterStrategy.registerOnAggregationResultListener(this);
            clusterStrategy.getNClusters();
            faceAggregations.add(clusterStrategy);
        }
        clusterPaths = new Path[N_CLUSTERS];
        drawClusterPaths = new Path[N_CLUSTERS];
        innerPaints = new Paint[N_CLUSTERS];

        for (int i = 0; i < N_CLUSTERS; ++i) {
            clusterPaths[i] = new Path();
            drawClusterPaths[i] = new Path();
            innerPaints[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
            innerPaints[i].setStrokeWidth(0.1F);
            innerPaints[i].setARGB(255, 255, 0, 0);
            innerPaints[i].setStyle(Paint.Style.FILL);
        }

        clear();
        setColor(0xAAAAAAAA);
    }

    public void clear() {
        for (ClusterStrategy strategy : faceAggregations) {
            for (int i = 0; i < strategy.getNClusters(); ++i) {
                clusterPaths[i].rewind();
            }
        }
    }

    @Override
    public void onResult() {
        for (int faceIndex = 0; faceIndex < model.getFaceCount(); faceIndex++) {
            final ClusterStrategy clusterStrategy = faceAggregations.get(faceIndex);
            for (int i = 0; i < clusterStrategy.getNClusters(); ++i) {
                onPrepareDraw(clusterStrategy, i);
            }
        }
        invalidateSelf();
    }

    @Override
    public void onProgressUpdate(int paramInt) {

    }

    @Override
    protected void onPostDraw(Canvas canvas, int faceIndex) {
        super.onPostDraw(canvas, faceIndex);
        final ClusterStrategy clusterStrategy = faceAggregations.get(faceIndex);
        for (int i = 0; i < clusterStrategy.getNClusters(); ++i) {
            onDraw(canvas, i);
        }
    }

    private void onPrepareDraw(ClusterStrategy clusterStrategy, int index) {
        Cluster cluster = clusterStrategy.getCluster(index);
        clusterPaths[index].rewind();
        if (cluster != null) {
            Log.i(TAG, "onPrepareDraw: " + cluster.toString());
            float v = (float) (Math.sqrt(cluster.getWeight()) * 0.2D);
            PointF center = cluster.getCenterOfGroup();
            clusterPaths[index]
                    .addOval(new RectF(center.x - v, center.y - v, center.x + v, center.y + v),
                            Path.Direction.CW);
        }
        drawClusterPaths[index].set(clusterPaths[index]);
    }

    private void onDraw(Canvas canvas, int index) {
        PathUtils.drawPath(canvas, drawClusterPaths[index], innerPaints[index]);
    }

    @Override
    public void cleanup() {
        for (ClusterStrategy cluster : faceAggregations) {
            cluster.unregisterOnAggregationResultListener(this);
            cluster.cleanup();
        }
    }

    public void setColor(@ColorInt int color) {
        for (int i = 0; i < N_CLUSTERS; ++i) {
            innerPaints[i].setColor(color);
        }
    }

    @Override
    public void notifyArrowSetChanged() {
        super.notifyArrowSetChanged();
        recalculateAggregation();
    }

    private void recalculateAggregation() {
        for (int faceIndex = 0; faceIndex < model.getFaceCount(); faceIndex++) {
            ArrayList<Shot> combinedList = new ArrayList<>();
            combinedList.addAll(transparentShots.get(faceIndex));
            combinedList.addAll(shots.get(faceIndex));
            faceAggregations.get(faceIndex).calculate(combinedList);
        }
    }
}
