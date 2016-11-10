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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import java.util.List;

import de.dreier.mytargets.shared.analysis.aggregation.IAggregationResultRenderer;
import de.dreier.mytargets.shared.utils.PathUtils;

public class ClusterResultRenderer implements IAggregationResultRenderer {

    private final Path[] clusterPaths;
    private final Paint[] innerPaints;
    private final List<Cluster> clusters;

    public ClusterResultRenderer(List<Cluster> clusters) {
        this.clusters = clusters;
        clusterPaths = new Path[clusters.size()];
        innerPaints = new Paint[clusters.size()];

        for (int i = 0; i < clusters.size(); ++i) {
            clusterPaths[i] = new Path();
            innerPaints[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
            innerPaints[i].setStrokeWidth(0.1F);
            innerPaints[i].setARGB(255, 255, 0, 0);
            innerPaints[i].setStyle(Paint.Style.FILL);
        }
    }

    @Override
    public void onPrepareDraw() {
        for (int index = 0; index < clusters.size(); index++) {
            clusterPaths[index].rewind();
            Cluster cluster = clusters.get(index);
            float v = (float) (Math.sqrt(cluster.getWeight()) * 0.2D);
            PointF center = cluster.getCenterOfGroup();
            clusterPaths[index]
                    .addOval(new RectF(center.x - v, center.y - v, center.x + v, center.y + v),
                            Path.Direction.CW);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        for (int index = 0; index < clusters.size(); index++) {
            PathUtils.drawPath(canvas, clusterPaths[index], innerPaints[index]);
        }
    }

    public void setColor(int color) {
        for (Paint innerPaint : innerPaints) {
            innerPaint.setColor(color);
        }
    }
}
