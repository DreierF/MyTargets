/*
 * Copyright (C) 2016 Florian Dreier
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

public class Score {
    public int reachedScore;
    public int totalScore;

    public Score add(Score other) {
        reachedScore += other.reachedScore;
        totalScore += other.totalScore;
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

    private int getPercent() {
        return reachedScore * 100 / totalScore;
    }
}
