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

package de.dreier.mytargets.shared.targets.scoringstyle

import android.support.annotation.StringRes
import de.dreier.mytargets.shared.models.Score
import de.dreier.mytargets.shared.models.db.Shot

class ColorScoringStyle(@StringRes title: Int, private val maxEndPoints: Int, vararg points: Int) : ScoringStyle(title, false, *points) {

    override fun getReachedScore(shots: List<Shot>): Score {
        val reachedScore = shots
                .map { s -> getPointsByScoringRing(s.scoringRing, s.index) }
                .distinct()
                .fold(0) { a, b -> a + b }
        return Score(reachedScore, maxEndPoints)
    }
}
