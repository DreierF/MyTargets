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

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class ScoreTest {

    @Test
    @Throws(Exception::class)
    fun zeroShots() {
        val s = Score(10)
        assertEquals(0, s.reachedScore)
        assertEquals(10, s.totalScore)
        assertEquals(0, s.shotCount)
    }

    @Test
    @Throws(Exception::class)
    fun singleShot() {
        val s = Score(9, 10)
        assertEquals(9, s.reachedScore)
        assertEquals(10, s.totalScore)
        assertEquals(1, s.shotCount)
    }

    @Test
    @Throws(Exception::class)
    fun add() {
        val s = Score(9, 10)
        s.add(Score(8, 10))
        assertEquals(17, s.reachedScore)
        assertEquals(20, s.totalScore)
        assertEquals(2, s.shotCount)
    }

    @Test
    @Throws(Exception::class)
    fun string() {
        val s = Score(7, 10)
        assertEquals("7/10", s.toString())
    }

    @Test
    @Throws(Exception::class)
    fun format() {
        val s = Score(9, 10)
        val config = Score.Configuration()
        config.showAverage = true
        config.showPercentage = true
        config.showReachedScore = true
        config.showTotalScore = true
        assertEquals("9/10 (90%, 9.00∅)", s.format(Locale.US, config))
        config.showPercentage = false
        assertEquals("9/10 (9,00∅)", s.format(Locale.GERMAN, config))
        config.showTotalScore = false
        assertEquals("9 (9,00∅)", s.format(Locale.GERMAN, config))
        config.showReachedScore = false
        config.showTotalScore = true
        assertEquals("", s.format(Locale.GERMAN, config))
        config.showReachedScore = true
        config.showAverage = false
        assertEquals("9/10", s.format(Locale.GERMAN, config))
        config.showReachedScore = false
        config.showTotalScore = false
        assertEquals("", s.format(Locale.GERMAN, config))
    }

    @Test
    @Throws(Exception::class)
    fun getShotAverage() {
        val score = Score(9, 10)
        assertEquals(9.0f, score.shotAverage)
        score.add(Score(6, 10))
        assertEquals(7.5f, score.shotAverage)
        assertEquals(-1.0f, Score(10).shotAverage)
    }

    @Test
    @Throws(Exception::class)
    fun getShotAverageFormatted() {
        val score = Score(9, 11)
        assertEquals("9,00", score.getShotAverageFormatted(Locale.GERMAN))
        score.add(Score(6, 11))
        assertEquals("7.50", score.getShotAverageFormatted(Locale.US))
        assertEquals("-", Score(11).getShotAverageFormatted(Locale.US))
    }

    @Test
    @Throws(Exception::class)
    fun getPercent() {
        val score = Score(9, 10)
        assertEquals(0.9f, score.percent)
        score.add(Score(6, 10))
        assertEquals(0.75f, score.percent)
        assertEquals(0f, Score(0).percent)
        assertEquals(0f, Score(0, 10).percent)
    }

}
