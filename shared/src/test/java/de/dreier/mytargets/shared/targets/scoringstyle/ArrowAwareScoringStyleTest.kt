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

import com.google.common.truth.Truth.assertThat
import de.dreier.mytargets.shared.models.Score
import de.dreier.mytargets.shared.models.db.Shot
import org.junit.Test

class ArrowAwareScoringStyleTest {

    private val scoringStyle = ArrowAwareScoringStyle(
        false,
        arrayOf(intArrayOf(20, 18), intArrayOf(16, 14), intArrayOf(12, 10))
    )

    @Test
    fun getPoints() {
        assertThat(scoringStyle.getPointsByScoringRing(0, 0)).isEqualTo(20)
        assertThat(scoringStyle.getPointsByScoringRing(1, 0)).isEqualTo(18)
        assertThat(scoringStyle.getPointsByScoringRing(0, 1)).isEqualTo(16)
        assertThat(scoringStyle.getPointsByScoringRing(1, 1)).isEqualTo(14)
        assertThat(scoringStyle.getPointsByScoringRing(0, 2)).isEqualTo(12)
        assertThat(scoringStyle.getPointsByScoringRing(1, 2)).isEqualTo(10)
        assertThat(scoringStyle.getPointsByScoringRing(0, 3)).isEqualTo(12)
        assertThat(scoringStyle.getPointsByScoringRing(1, 3)).isEqualTo(10)
    }

    @Test
    fun getReachedScore() {
        assertThat(scoringStyle.getReachedScore(Shot(scoringRing = 0, index = 0))).isEqualTo(
            Score(
                20,
                20,
                1
            )
        )

        assertThat(scoringStyle.getReachedScore(listOf(
            Shot(scoringRing = Shot.MISS, index = 0),
            Shot(scoringRing = 0, index = 1)
        ))).isEqualTo(
            Score(
                16,
                20,
                2
            )
        )
    }
}