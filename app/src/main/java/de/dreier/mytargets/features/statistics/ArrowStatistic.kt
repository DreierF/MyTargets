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

package de.dreier.mytargets.features.statistics

import android.os.Parcelable
import de.dreier.mytargets.R
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.shared.SharedApplicationInstance
import de.dreier.mytargets.shared.analysis.aggregation.average.Average
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.Score
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.Shot
import de.dreier.mytargets.shared.models.sum
import kotlinx.android.parcel.Parcelize
import java.lang.Math.ceil

@Parcelize
data class ArrowStatistic(
    var arrowName: String? = null,
    var arrowNumber: String? = null,
    var target: Target,
    var shots: MutableList<Shot> = mutableListOf(),
    var average: Average = Average(),
    var totalScore: Score = Score(),
    var arrowDiameter: Dimension = Dimension(5f, Dimension.Unit.MILLIMETER),
    var exportFileName: String? = null
) : Parcelable {

    val appropriateBgColor: Int
        get() = BG_COLORS[ceil(((BG_COLORS.size - 1) * totalScore.percent).toDouble()).toInt()]

    val appropriateTextColor: Int
        get() = TEXT_COLORS[ceil(((TEXT_COLORS.size - 1) * totalScore.percent).toDouble()).toInt()]

    constructor(target: Target, shots: List<Shot>) : this(null, null, target, shots)

    private constructor(
        arrowName: String?,
        arrowNumber: String?,
        target: Target,
        shots: List<Shot>
    ) : this(
        arrowName = arrowName,
        arrowNumber = arrowNumber,
        target = target
    ) {
        this.average.computeAll(shots)
        this.shots.addAll(shots)
        this.totalScore = shots
            .map { shot -> target.getScoringStyle().getReachedScore(shot) }
            .sum()
    }


    companion object {
        private val BG_COLORS = intArrayOf(
            -0xbbcca,
            -0xa8de,
            -0x6800,
            -0x3ef9,
            -0x14c5,
            -0x3223c7,
            -0x743cb6,
            -0xb350b0
        )
        private val TEXT_COLORS =
            intArrayOf(-0x1, -0x1, -0xfffffe, -0xfffffe, -0xfffffe, -0xfffffe, -0xfffffe, -0xfffffe)

        fun getAll(target: Target, rounds: List<Round>): List<ArrowStatistic> {
            val db = ApplicationInstance.db
            return rounds
                .groupBy { r -> db.trainingDAO().loadTraining(r.trainingId!!).arrowId ?: 0 }
                .flatMap { t ->
                    val arrow = db.arrowDAO().loadArrowOrNull(t.key)
                    val name = arrow?.name ?: SharedApplicationInstance.getStr(R.string.unknown)
                    t.value.flatMap { db.roundDAO().loadEnds(it.id) }
                        .flatMap { db.endDAO().loadShots(it.id) }
                        .filter { it.arrowNumber != null }
                        .groupBy { it.arrowNumber!! }
                        .filter { it.value.size > 1 }
                        .map { (key, value) -> ArrowStatistic(name, key, target, value) }
                }
        }
    }
}
