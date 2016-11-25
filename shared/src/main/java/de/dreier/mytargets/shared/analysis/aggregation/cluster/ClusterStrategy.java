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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.dreier.mytargets.shared.models.db.Shot;

public class ClusterStrategy extends AggregationStrategyBase<ClusterResultRenderer> {

    private static final double EPS = 0.4;
    private static final int MINIMUM_POINTS_FOR_CLUSTER = 3;
    private final ArrayList<Cluster> clusters;

    public ClusterStrategy() {
        super();
        this.clusters = new ArrayList<>();
        this.isDirty = false;
    }

    @Override
    protected void reset() {
        super.reset();
        clusters.clear();
    }

    @Override
    protected ClusterResultRenderer compute(List<Shot> shots) {
        // DBSCAN
        clusters.clear();
        final Map<Shot, PointStatus> visited = new HashMap<>();

        for (final Shot point : shots) {
            if (isCancelled()) {
                break;
            }
            if (visited.get(point) != null) {
                continue;
            }
            final List<Shot> neighbors = getNeighbors(point, shots);
            if (neighbors.size() + 1 >= MINIMUM_POINTS_FOR_CLUSTER) {
                // DBSCAN does not care about center points
                final Cluster cluster = new Cluster(shots.size());
                clusters.add(expandCluster(cluster, point, neighbors, shots, visited));
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
                                  final Shot point,
                                  final List<Shot> neighbors,
                                  final Collection<Shot> points,
                                  final Map<Shot, PointStatus> visited) {
        cluster.add(point);
        visited.put(point, PointStatus.PART_OF_CLUSTER);

        List<Shot> seeds = new ArrayList<>(neighbors);
        int index = 0;
        while (index < seeds.size()) {
            final Shot current = seeds.get(index);
            PointStatus pStatus = visited.get(current);
            // only check non-visited points
            if (pStatus == null) {
                final List<Shot> currentNeighbors = getNeighbors(current, points);
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
    private List<Shot> getNeighbors(final Shot point, final Collection<Shot> points) {
        final List<Shot> neighbors = new ArrayList<>();
        for (final Shot neighbor : points) {
            if (!point.equals(neighbor) && distanceFrom(neighbor, point) <= EPS) {
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
    private List<Shot> merge(final List<Shot> one, final List<Shot> two) {
        final Set<Shot> oneSet = new HashSet<>(one);
        for (Shot item : two) {
            if (!oneSet.contains(item)) {
                one.add(item);
            }
        }
        return one;
    }

    private double distanceFrom(Shot p1, Shot p2) {
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
