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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.Locale;

@Parcel
public class Score {
    public int reachedScore;
    public int totalScore;
    public int shotCount;

    public Score() {
        this.reachedScore = 0;
        this.totalScore = 0;
        this.shotCount = 0;
    }

    @ParcelConstructor
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

    @NonNull
    public Score add(@NonNull Score other) {
        reachedScore += other.reachedScore;
        totalScore += other.totalScore;
        shotCount += other.shotCount;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return reachedScore + "/" + totalScore;
    }

    @NonNull
    public String format(Locale locale, @NonNull Configuration config) {
        if (!config.showReachedScore) {
            return "";
        }
        String score = String.valueOf(reachedScore);
        if (config.showTotalScore) {
            score += "/" + totalScore;
        }
        if ((config.showPercentage || config.showAverage) && totalScore > 0) {
            score += " (";
            if (config.showPercentage) {
                score += getPercentString();
                if (config.showAverage) {
                    score += ", ";
                }
            }
            if (config.showAverage) {
                score += getShotAverageFormatted(locale) + "∅";
            }
            score += ")";
        }
        return score;
    }

    public float getShotAverage() {
        if (shotCount == 0) {
            return -1;
        }
        return reachedScore / (float) shotCount;
    }

    public String getShotAverageFormatted(Locale locale) {
        if (shotCount == 0) {
            return "-";
        }
        return String.format(locale, "%.2f", getShotAverage());
    }

    /**
     * @return The percent of points reached relative to the total reachable score.
     */
    public float getPercent() {
        if (totalScore > 0) {
            return reachedScore / (float) totalScore;
        }
        return 0;
    }

    private String getPercentString() {
        if (totalScore > 0) {
            return String.valueOf(reachedScore * 100 / totalScore) + "%";
        }
        return "";
    }

    public static class Configuration {
        public boolean showReachedScore;
        public boolean showTotalScore;
        public boolean showPercentage;
        public boolean showAverage;

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Configuration that = (Configuration) o;

            return showReachedScore == that.showReachedScore &&
                    showTotalScore == that.showTotalScore &&
                    showPercentage == that.showPercentage &&
                    showAverage == that.showAverage;

        }
    }
}
