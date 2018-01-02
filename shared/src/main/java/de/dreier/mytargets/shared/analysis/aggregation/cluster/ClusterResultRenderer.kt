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

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import de.dreier.mytargets.shared.analysis.aggregation.IAggregationResultRenderer
import de.dreier.mytargets.shared.targets.drawable.CanvasWrapper

class ClusterResultRenderer(private val clusters: List<Cluster>) : IAggregationResultRenderer {

    private val clusterPaths = Array(clusters.size) { Path() }
    private val outerClusterPaths = Array(clusters.size) { Path() }
    private var innerPaints = Array(clusters.size) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.strokeWidth = 0.1f
        paint.setARGB(255, 255, 0, 0)
        paint.style = Paint.Style.FILL
        paint
    }

    override fun onPrepareDraw() {
        for (index in clusters.indices) {
            clusterPaths[index].rewind()
            val cluster = clusters[index]
            val center = cluster.getCenterOfGroup()
            var v = cluster.stdDev.toFloat()
            clusterPaths[index]
                    .addOval(RectF(center.x - v, center.y - v, center.x + v, center.y + v),
                            Path.Direction.CW)
            v *= 2f
            outerClusterPaths[index]
                    .addOval(RectF(center.x - v, center.y - v, center.x + v, center.y + v),
                            Path.Direction.CW)
        }
    }

    override fun onDraw(canvas: CanvasWrapper) {
        for (index in clusters.indices) {
            canvas.drawPath(outerClusterPaths[index], innerPaints[index])
            canvas.drawPath(clusterPaths[index], innerPaints[index])
        }
    }

    override fun setColor(color: Int) {
        for (innerPaint in innerPaints) {
            innerPaint.setARGB(120, Color.red(color), Color.green(color), Color.blue(color))
        }
    }
}
