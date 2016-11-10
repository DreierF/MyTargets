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

import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;

public class ClusterStrategy extends AggregationStrategyBase<ClusterResultRenderer> {

    private static final int N_CLUSTERS = 4;

    private final ArrayList<Cluster> clusters;
    private final int nClusters;
    private boolean overflow;
    private double distance;

    public ClusterStrategy() {
        super();
        this.clusters = new ArrayList<>();
        this.isDirty = false;
        this.isDirty = true;
        this.overflow = false;
        this.nClusters = N_CLUSTERS;
    }

    public void add(final float n1, final float n2) {
        if (!overflow) {
            data.add(new PointF(n1, n2));
            isDirty = true;
            if (data.size() > 500) {
                if (!overflow) {
                    Log.d("Cluster-analysis", "Cluster-analysis overflows (N>500)");
                }
                this.overflow = true;
            }
        }
    }

    @Override
    protected void reset() {
        super.reset();
        clusters.clear();
        overflow = false;
    }

    @Override
    protected ClusterResultRenderer compute(ArrayList<PointF> list) {
        // Agglomerative hierarchical clustering implementation
        final int size = list.size();
        clusters.clear();
        for (PointF point : list) {
            clusters.add(new Cluster(point, size));
        }
        while (clusters.size() > nClusters) {
            Cluster cluster1 = null;
            Cluster cluster2 = null;
            double distance = Double.MAX_VALUE;
            for (int i = 0; i < clusters.size() - 1; i++) {
                if (isCancelled()) {
                    return null;
                }
                for (int j = i + 1; j < clusters.size(); j++) {
                    if (isCloser(distance, clusters.get(i), clusters.get(j))) {
                        distance = this.distance;
                        cluster1 = clusters.get(i);
                        cluster2 = clusters.get(j);
                    }
                }
            }
            for (PointF point : cluster2.points) {
                cluster1.add(point);
            }
            clusters.remove(cluster2);
        }
        final ClusterResultRenderer clusterResultRenderer = new ClusterResultRenderer(clusters);
        clusterResultRenderer.onPrepareDraw();
        return clusterResultRenderer;
    }

    private boolean isCloser(final double n, final Cluster cluster1, final Cluster cluster2) {
        final PointF cog1 = cluster1.getCenterOfGroup();
        final PointF cog2 = cluster2.getCenterOfGroup();
        final double n2 = cog1.x - cog2.x;
        if (n2 > n || -n2 > n) {
            return false;
        }
        final double n3 = cog1.y - cog2.y;
        if (n3 > n || -n3 > n) {
            return false;
        }
        distance = Math.sqrt(n2 * n2 + n3 * n3);
        return distance < n;
    }
}
