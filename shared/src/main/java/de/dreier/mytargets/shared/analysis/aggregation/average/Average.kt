/*
 * Copyright (C) 2018 Florian Dreier
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

package de.dreier.mytargets.shared.analysis.aggregation.average

import android.graphics.PointF
import android.graphics.RectF
import android.os.Parcelable

import de.dreier.mytargets.shared.models.db.Shot
import kotlinx.android.parcel.Parcelize

@Parcelize
class Average constructor(
    var dataPointCount: Int = 0,
    var average: PointF = PointF(0.0f, 0.0f),
    var weightedAverage: PointF = PointF(0.0f, 0.0f),
    var nonUniformStdDev: RectF = RectF(-1.0f, -1.0f, -1.0f, -1.0f),
    private var stdDevX: Double = -1.0,
    private var stdDevY: Double = -1.0
) : Parcelable {

    val stdDev: Double
        get() = (stdDevX + stdDevY) / 2.0

    fun computeAll(shots: List<Shot>) {
        dataPointCount = shots.size
        computeAverage(shots)
        computeNonUniformStdDeviations(shots)
        computeStdDevX(shots)
        computeStdDevY(shots)
        computeWeightedAverage(shots)
    }

    private fun computeWeightedAverage(data: List<Shot>) {
        var sumX = 0.0
        var sumY = 0.0
        var i = 0

        for (point in data) {
            ++i
            sumX += (i.toFloat() * point.x).toDouble()
            sumY += (i.toFloat() * point.y).toDouble()
        }

        i = (i + 1) * i / 2
        weightedAverage.set((sumX / i.toDouble()).toFloat(), (sumY / i.toDouble()).toFloat())
    }

    private fun computeNonUniformStdDeviations(data: List<Shot>) {
        var negCountX = 0
        var posCountX = 0
        var posCountY = 0
        var negCountY = 0
        var negSquaredXError = 0.0
        var posSquaredXError = 0.0
        var posSquaredYError = 0.0
        var negSquaredYError = 0.0

        for (point in data) {
            var error = (point.x - average.x).toDouble()
            if (error < 0.0) {
                negSquaredXError += error * error
                ++negCountX
            } else {
                posSquaredXError += error * error
                ++posCountX
            }

            error = (point.y - average.y).toDouble()
            if (error >= 0.0) {
                posSquaredYError += error * error
                ++posCountY
            } else {
                negSquaredYError += error * error
                ++negCountY
            }
        }

        nonUniformStdDev.set(
            Math.sqrt(negSquaredXError / negCountX.toDouble()).toFloat(),
            Math.sqrt(posSquaredYError / posCountY.toDouble()).toFloat(),
            Math.sqrt(posSquaredXError / posCountX.toDouble()).toFloat(),
            Math.sqrt(negSquaredYError / negCountY.toDouble()).toFloat()
        )
    }

    fun computeStdDevX(data: List<Shot>) {
        val sumSquaredXError = data
            .map { (it.x - average.x).toDouble() }
            .sumByDouble { it * it }

        stdDevX = Math.sqrt(sumSquaredXError / data.size.toDouble())
    }

    fun computeStdDevY(data: List<Shot>) {
        val sumSquaredYError = data
            .map { (it.y - average.y).toDouble() }
            .sumByDouble { it * it }

        stdDevY = Math.sqrt(sumSquaredYError / data.size.toDouble())
    }

    fun computeAverage(data: List<Shot>) {
        var sumX = 0.0
        var sumY = 0.0

        for (point in data) {
            sumX += point.x.toDouble()
            sumY += point.y.toDouble()
        }

        average.set(
            (sumX / data.size.toDouble()).toFloat(),
            (sumY / data.size.toDouble()).toFloat()
        )
    }
}
