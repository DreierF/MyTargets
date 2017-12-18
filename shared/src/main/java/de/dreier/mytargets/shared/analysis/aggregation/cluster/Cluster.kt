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

import android.graphics.PointF
import de.dreier.mytargets.shared.analysis.aggregation.average.Average
import de.dreier.mytargets.shared.models.db.Shot
import java.util.*

class Cluster(private val totalNumber: Int) {
    val points = ArrayList<Shot>()
    private val centerOfGroup = PointF()
    private var isDirty = false
    private val weight = 0.0
    var stdDev: Double = 0.toDouble()

    val size: Int
        get() = points.size

    init {
        isDirty = true
    }

    private fun compute() {
        if (!isDirty) {
            return
        }
        val average = Average()
        average.computeAverage(points)
        average.computeStdDevX(points)
        average.computeStdDevY(points)
        centerOfGroup.set(average.average)
        stdDev = average.stdDev
        isDirty = false
    }

    fun add(paramPointF: Shot) {
        points.add(paramPointF)
        isDirty = true
    }

    fun getCenterOfGroup(): PointF {
        compute()
        return centerOfGroup
    }

    fun getWeight(): Double {
        compute()
        return weight
    }

    override fun toString(): String {
        return "Cluster{" +
                "points=" + points +
                ", centerOfGroup=" + centerOfGroup +
                ", totalNumber=" + totalNumber +
                ", isDirty=" + isDirty +
                ", weight=" + weight +
                '}'
    }
}
