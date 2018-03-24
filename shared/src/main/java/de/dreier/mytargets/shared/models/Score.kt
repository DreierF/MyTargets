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

package de.dreier.mytargets.shared.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Score(
    var reachedPoints: Int = 0,
    var totalPoints: Int = 0,
    var shotCount: Int = 0
) : Parcelable {

    constructor() : this(
        reachedPoints = 0,
        totalPoints = 0,
        shotCount = 0
    )

    constructor(reachedScore: Int, totalScore: Int) : this(
        reachedPoints = reachedScore,
        totalPoints = totalScore,
        shotCount = 1
    )

    constructor(totalScore: Int) : this(
        reachedPoints = 0,
        totalPoints = totalScore,
        shotCount = 0
    )

    val shotAverage: Float
        get() = if (shotCount == 0) {
            -1f
        } else reachedPoints / shotCount.toFloat()

    /**
     * @return The percent of points reached relative to the total reachable score.
     */
    val percent: Float
        get() = if (totalPoints > 0) {
            reachedPoints / totalPoints.toFloat()
        } else 0f

    private val percentString: String
        get() = if (totalPoints > 0) "${reachedPoints * 100 / totalPoints}%" else ""

    fun add(other: Score): Score {
        reachedPoints += other.reachedPoints
        totalPoints += other.totalPoints
        shotCount += other.shotCount
        return this
    }

    override fun toString(): String {
        return "$reachedPoints/$totalPoints"
    }

    fun format(locale: Locale, config: Configuration): String {
        if (!config.showReachedScore) {
            return ""
        }
        var score = reachedPoints.toString()
        if (config.showTotalScore) {
            score += "/" + totalPoints
        }
        if ((config.showPercentage || config.showAverage) && totalPoints > 0) {
            score += " ("
            if (config.showPercentage) {
                score += percentString
                if (config.showAverage) {
                    score += ", "
                }
            }
            if (config.showAverage) {
                score += getShotAverageFormatted(locale) + "∅"
            }
            score += ")"
        }
        return score
    }

    fun getShotAverageFormatted(locale: Locale): String {
        return if (shotCount == 0) {
            "-"
        } else String.format(locale, "%.2f", shotAverage)
    }

    data class Configuration(
        var showReachedScore: Boolean = false,
        var showTotalScore: Boolean = false,
        var showPercentage: Boolean = false,
        var showAverage: Boolean = false
    )
}

fun Iterable<Score>.sum() = fold(Score(), Score::add)
