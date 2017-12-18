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

package de.dreier.mytargets.shared.analysis.aggregation.cluster

import de.dreier.mytargets.shared.analysis.aggregation.IAggregationResultRenderer
import de.dreier.mytargets.shared.models.db.Shot
import java.util.*

class ClusterStrategy : AggregationStrategyBase() {
    private val clusters = ArrayList<Cluster>()

    override fun reset() {
        super.reset()
        clusters.clear()
    }

    /**
     * DBSCAN
     */
    override fun compute(shots: List<Shot>): IAggregationResultRenderer {
        clusters.clear()
        val visited = HashMap<Shot, PointStatus>()

        for (point in shots) {
            if (isCancelled) {
                break
            }
            if (visited[point] != null) {
                continue
            }
            val neighbors = getNeighbors(point, shots)
            if (neighbors.size + 1 >= MINIMUM_POINTS_FOR_CLUSTER) {
                // DBSCAN does not care about center points
                val cluster = Cluster(shots.size)
                clusters.add(expandCluster(cluster, point, neighbors, shots, visited))
            } else {
                visited.put(point, PointStatus.NOISE)
            }
        }
        val clusterResultRenderer = ClusterResultRenderer(clusters)
        clusterResultRenderer.onPrepareDraw()
        return clusterResultRenderer
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
    private fun expandCluster(cluster: Cluster,
                              point: Shot,
                              neighbors: List<Shot>,
                              points: Collection<Shot>,
                              visited: MutableMap<Shot, PointStatus>): Cluster {
        cluster.add(point)
        visited.put(point, PointStatus.PART_OF_CLUSTER)

        var seeds: MutableList<Shot> = ArrayList(neighbors)
        var index = 0
        while (index < seeds.size) {
            val current = seeds[index]
            val pStatus = visited[current]
            // only check non-visited points
            if (pStatus == null) {
                val currentNeighbors = getNeighbors(current, points)
                if (currentNeighbors.size >= MINIMUM_POINTS_FOR_CLUSTER) {
                    seeds = merge(seeds, currentNeighbors)
                }
            }

            if (pStatus != PointStatus.PART_OF_CLUSTER) {
                visited.put(current, PointStatus.PART_OF_CLUSTER)
                cluster.add(current)
            }

            index++
        }
        return cluster
    }

    /**
     * Returns a list of density-reachable neighbors of a `point`.
     *
     * @param point  the point to look for
     * @param points possible neighbors
     * @return the List of neighbors
     */
    private fun getNeighbors(point: Shot, points: Collection<Shot>): List<Shot> {
        return points.filter { point != it && distanceFrom(it, point) <= EPS }
    }

    /**
     * Merges two lists together.
     *
     * @param one first list
     * @param two second list
     * @return merged lists
     */
    private fun merge(one: MutableList<Shot>, two: List<Shot>): MutableList<Shot> {
        val oneSet = HashSet(one)
        two.filterNotTo(one) { oneSet.contains(it) }
        return one
    }

    private fun distanceFrom(p1: Shot, p2: Shot): Double {
        val diffX = (p1.x - p2.x).toDouble()
        val diffY = (p1.y - p2.y).toDouble()
        return Math.sqrt(diffX * diffX + diffY * diffY)
    }

    private enum class PointStatus {
        /**
         * The point has is considered to be noise.
         */
        NOISE,
        /**
         * The point is already part of a cluster.
         */
        PART_OF_CLUSTER
    }

    companion object {

        private val EPS = 0.4
        private val MINIMUM_POINTS_FOR_CLUSTER = 2
    }
}
