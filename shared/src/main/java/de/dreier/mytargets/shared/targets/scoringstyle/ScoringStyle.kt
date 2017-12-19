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

package de.dreier.mytargets.shared.targets.scoringstyle

import android.support.annotation.StringRes
import de.dreier.mytargets.shared.SharedApplicationInstance
import de.dreier.mytargets.shared.models.Score
import de.dreier.mytargets.shared.models.db.End
import de.dreier.mytargets.shared.models.db.Shot
import de.dreier.mytargets.shared.models.sum

open class ScoringStyle private constructor(title: String?, private val showAsX: Boolean, protected val points: Array<IntArray>) {
    private val title: String?
    private var maxScorePerShot: Int = 0

    internal constructor(showAsX: Boolean, points: Array<IntArray>) : this(null, showAsX, points)

    constructor(@StringRes title: Int, showAsX: Boolean, vararg points: Int) : this(SharedApplicationInstance.getStr(title), showAsX, arrayOf<IntArray>(points))

    private val maxPoints: Int
        get() {
            maxScorePerShot = 0
            for (arrowPoints in points) {
                for (point in arrowPoints) {
                    if (point > maxScorePerShot) {
                        maxScorePerShot = point
                    }
                }
            }
            return maxScorePerShot
        }

    private val descriptionString: String
        get() {
            var style = ""
            for (i in 0 until points[0].size) {
                if (i + 1 < points[0].size && points[0][i] <= points[0][i + 1] &&
                        !(i == 0 && showAsX)) {
                    continue
                }
                if (!style.isEmpty()) {
                    style += ", "
                }
                style += zoneToString(i, 0)
                for (a in 1 until points.size) {
                    style += "/" + zoneToString(i, a)
                }
            }
            return style
        }

    init {
        maxPoints
        if (title == null) {
            this.title = descriptionString
        } else {
            this.title = title
        }
    }

    constructor(showAsX: Boolean, vararg points: Int) : this(showAsX, arrayOf<IntArray>(points)) {}

    override fun toString(): String {
        return title!!
    }

    fun zoneToString(zone: Int, arrow: Int): String {
        return if (isOutOfRange(zone)) {
            MISS_SYMBOL
        } else if (zone == 0 && showAsX) {
            X_SYMBOL
        } else {
            val value = getScoreByScoringRing(zone, arrow)
            if (value == 0) {
                MISS_SYMBOL
            } else value.toString()
        }
    }

    fun getScoreByScoringRing(zone: Int, arrow: Int): Int {
        return if (isOutOfRange(zone)) 0 else getPoints(zone, arrow)
    }

    protected open fun getPoints(zone: Int, arrow: Int): Int {
        return points[0][zone]
    }

    private fun isOutOfRange(zone: Int): Boolean {
        return zone < 0 || zone >= points[0].size
    }

    fun getReachedScore(shot: Shot): Score {
        if (shot.scoringRing == Shot.NOTHING_SELECTED) {
            return Score(maxScorePerShot)
        }
        val reachedScore = getScoreByScoringRing(shot.scoringRing, shot.index)
        return Score(reachedScore, maxScorePerShot)
    }

    open fun getReachedScore(end: End): Score {
        return end.loadShots()!!
                .map { shot: Shot -> this.getReachedScore(shot) }
                .sum()
    }

    companion object {
        val MISS_SYMBOL = "M"
        private val X_SYMBOL = "X"
    }
}
