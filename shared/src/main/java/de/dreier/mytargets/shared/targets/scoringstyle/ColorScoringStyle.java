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

package de.dreier.mytargets.shared.targets.scoringstyle;

import android.support.annotation.StringRes;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import de.dreier.mytargets.shared.models.Score;
import de.dreier.mytargets.shared.models.db.End;

public class ColorScoringStyle extends ScoringStyle {

    private final int maxEndPoints;

    public ColorScoringStyle(@StringRes int title, int maxEndPoints, int... points) {
        super(title, false, points);
        this.maxEndPoints = maxEndPoints;
    }

    @Override
    public Score getReachedScore(End end) {
        int reachedScore = Stream.of(end.getShots())
                .map(s -> getScoreByScoringRing(s.scoringRing, s.index))
                .distinct()
                .collect(Collectors.reducing(0, (a, b) -> a + b));
        return new Score(reachedScore, maxEndPoints);
    }
}
