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

import de.dreier.mytargets.shared.models.Score
import de.dreier.mytargets.shared.models.db.Shot
import kotlin.math.min

/**
 * Scoring style used for 3D targets, where the score is dependant on the arrow's index.
 * @param points Describes the scoring schema.
 * E.g. arrayOf(intArrayOf(20, 18), intArrayOf(16, 14), intArrayOf(12, 10))
 * The first arrow scores 20 points in the inner zone and 18 in the outer zone.
 * The second arrow 16 and 14 respectively.
 */
class ArrowAwareScoringStyle(showAsX: Boolean, points: Array<IntArray>) :
    ScoringStyle(showAsX, points) {

    override fun getPoints(zone: Int, arrow: Int): Int {
        return points[min(arrow, points.size - 1)][zone]
    }

    override fun getReachedScore(shots: List<Shot>): Score {
        val reachedScore = shots
            .map { s -> getPointsByScoringRing(s.scoringRing, s.index) }
            .firstOrNull { it > 0 } ?: 0
        return Score(reachedScore, getMaxPointsPerShot(0), shots.size)
    }
}
