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

import com.annimon.stream.Collector;
import com.annimon.stream.function.BiConsumer;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Supplier;

import java.util.Locale;

public class Score {
    public int reachedScore;
    public int totalScore;
    public int shotCount;

    private Score() {
        this.reachedScore = 0;
        this.totalScore = 0;
        this.shotCount = 0;
    }

    public Score(int reachedScore, int totalScore) {
        this.reachedScore = reachedScore;
        this.totalScore = totalScore;
        this.shotCount = 1;
    }

    public Score(int totalScore) {
        this.reachedScore = 0;
        this.totalScore = totalScore;
        this.shotCount = 0;
    }

    public static Collector<Score, Score, Score> sum() {
        return new Collector<Score, Score, Score>() {
            @Override
            public Supplier<Score> supplier() {
                return Score::new;
            }

            @Override
            public BiConsumer<Score, Score> accumulator() {
                return Score::add;
            }

            @Override
            public Function<Score, Score> finisher() {
                return score -> score;
            }
        };
    }

    public Score add(Score other) {
        reachedScore += other.reachedScore;
        totalScore += other.totalScore;
        shotCount += other.shotCount;
        return this;
    }

    @Override
    public String toString() {
        return reachedScore + "/" + totalScore;
    }

    public String format(boolean appendPercent) {
        if (appendPercent && totalScore > 0) {
            return reachedScore + "/" + totalScore + " (" + getPercent() + ")";
        } else {
            return toString();
        }
    }

    public float getShotAverage() {
        if (shotCount == 0) {
            return -1;
        }
        return reachedScore / (float) shotCount;
    }

    public String getShotAverageFormatted() {
        if (shotCount == 0) {
            return "-";
        }
        return String.format(Locale.getDefault(), "%.2f", getShotAverage());
    }

    private int getPercent() {
        return reachedScore * 100 / totalScore;
    }
}
