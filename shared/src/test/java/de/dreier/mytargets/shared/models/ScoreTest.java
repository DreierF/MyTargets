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

package de.dreier.mytargets.shared.models;

import org.junit.Test;

import java.util.Locale;

import static junit.framework.Assert.assertEquals;

public class ScoreTest {

    @Test
    public void zeroShots() throws Exception {
        Score s = new Score(10);
        assertEquals(0, s.reachedScore);
        assertEquals(10, s.totalScore);
        assertEquals(0, s.shotCount);
    }

    @Test
    public void singleShot() throws Exception {
        Score s = new Score(9, 10);
        assertEquals(9, s.reachedScore);
        assertEquals(10, s.totalScore);
        assertEquals(1, s.shotCount);
    }

    @Test
    public void add() throws Exception {
        Score s = new Score(9, 10);
        s.add(new Score(8, 10));
        assertEquals(17, s.reachedScore);
        assertEquals(20, s.totalScore);
        assertEquals(2, s.shotCount);
    }

    @Test
    public void string() throws Exception {
        Score s = new Score(7, 10);
        assertEquals("7/10", s.toString());
    }

    @Test
    public void format() throws Exception {
        Score s = new Score(9, 10);
        Score.Configuration config = new Score.Configuration();
        config.showAverage = true;
        config.showPercentage = true;
        config.showReachedScore = true;
        config.showTotalScore = true;
        assertEquals("9/10 (90%, 9.00∅)", s.format(Locale.US, config));
        config.showPercentage = false;
        assertEquals("9/10 (9,00∅)", s.format(Locale.GERMAN, config));
        config.showTotalScore = false;
        assertEquals("9 (9,00∅)", s.format(Locale.GERMAN, config));
        config.showReachedScore = false;
        config.showTotalScore = true;
        assertEquals("", s.format(Locale.GERMAN, config));
        config.showReachedScore = true;
        config.showAverage = false;
        assertEquals("9/10", s.format(Locale.GERMAN, config));
        config.showReachedScore = false;
        config.showTotalScore = false;
        assertEquals("", s.format(Locale.GERMAN, config));
    }

    @Test
    public void getShotAverage() throws Exception {
        Score score = new Score(9, 10);
        assertEquals(9.0f, score.getShotAverage());
        score.add(new Score(6, 10));
        assertEquals(7.5f, score.getShotAverage());
        assertEquals(-1.0f, new Score(10).getShotAverage());
    }

    @Test
    public void getShotAverageFormatted() throws Exception {
        Score score = new Score(9, 11);
        assertEquals("9,00", score.getShotAverageFormatted(Locale.GERMAN));
        score.add(new Score(6, 11));
        assertEquals("7.50", score.getShotAverageFormatted(Locale.US));
        assertEquals("-", new Score(11).getShotAverageFormatted(Locale.US));
    }

    @Test
    public void getPercent() throws Exception {
        Score score = new Score(9, 10);
        assertEquals(0.9f, score.getPercent());
        score.add(new Score(6, 10));
        assertEquals(0.75f, score.getPercent());
        assertEquals(0f, new Score(0).getPercent());
        assertEquals(0f, new Score(0, 10).getPercent());
    }

}
