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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClusterStrategy extends AggregationStrategyBase<ClusterResultRenderer> {

    private static final double EPS = 1.0;
    private static final int MINIMUM_POINTS_FOR_CLUSTER = 3;
    private final ArrayList<Cluster> clusters;

    public ClusterStrategy() {
        super();
        this.clusters = new ArrayList<>();
        this.isDirty = false;
    }

    public void add(final float n1, final float n2) {
        data.add(new PointF(n1, n2));
        isDirty = true;
    }

    @Override
    protected void reset() {
        super.reset();
        clusters.clear();
    }

    @Override
    protected ClusterResultRenderer compute(ArrayList<PointF> list) {
        // DBSCAN
        clusters.clear();
        final Map<PointF, PointStatus> visited = new HashMap<>();

        for (final PointF point : list) {
            if (visited.get(point) != null) {
                continue;
            }
            final List<PointF> neighbors = getNeighbors(point, list);
            if (neighbors.size() >= MINIMUM_POINTS_FOR_CLUSTER) {
                // DBSCAN does not care about center points
                final Cluster cluster = new Cluster(list.size());
                clusters.add(expandCluster(cluster, point, neighbors, list, visited));
            } else {
                visited.put(point, PointStatus.NOISE);
            }
        }
        final ClusterResultRenderer clusterResultRenderer = new ClusterResultRenderer(clusters);
        clusterResultRenderer.onPrepareDraw();
        return clusterResultRenderer;
    }

    /**
     * Expands the cluster to include density-reachable items.
     *
     * @param cluster   Cluster to expand
     * @param point     Point to add to cluster
     * @param neighbors List of neighbors
     * @param points    the data set
     * @param visited   the set of already visited points
     * @return the expanded cluster
     */
    private Cluster expandCluster(final Cluster cluster,
                                  final PointF point,
                                  final List<PointF> neighbors,
                                  final Collection<PointF> points,
                                  final Map<PointF, PointStatus> visited) {
        cluster.add(point);
        visited.put(point, PointStatus.PART_OF_CLUSTER);

        List<PointF> seeds = new ArrayList<>(neighbors);
        int index = 0;
        while (index < seeds.size()) {
            final PointF current = seeds.get(index);
            PointStatus pStatus = visited.get(current);
            // only check non-visited points
            if (pStatus == null) {
                final List<PointF> currentNeighbors = getNeighbors(current, points);
                if (currentNeighbors.size() >= MINIMUM_POINTS_FOR_CLUSTER) {
                    seeds = merge(seeds, currentNeighbors);
                }
            }

            if (pStatus != PointStatus.PART_OF_CLUSTER) {
                visited.put(current, PointStatus.PART_OF_CLUSTER);
                cluster.add(current);
            }

            index++;
        }
        return cluster;
    }

    /**
     * Returns a list of density-reachable neighbors of a {@code point}.
     *
     * @param point  the point to look for
     * @param points possible neighbors
     * @return the List of neighbors
     */
    private List<PointF> getNeighbors(final PointF point, final Collection<PointF> points) {
        final List<PointF> neighbors = new ArrayList<>();
        for (final PointF neighbor : points) {
            if (point != neighbor && distanceFrom(neighbor, point) <= EPS) {
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }

    /**
     * Merges two lists together.
     *
     * @param one first list
     * @param two second list
     * @return merged lists
     */
    private List<PointF> merge(final List<PointF> one, final List<PointF> two) {
        final Set<PointF> oneSet = new HashSet<>(one);
        for (PointF item : two) {
            if (!oneSet.contains(item)) {
                one.add(item);
            }
        }
        return one;
    }

    private double distanceFrom(PointF p1, PointF p2) {
        final double diffX = p1.x - p2.x;
        final double diffY = p1.y - p2.y;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private enum PointStatus {
        /**
         * The point has is considered to be noise.
         */
        NOISE,
        /**
         * The point is already part of a cluster.
         */
        PART_OF_CLUSTER
    }
}
