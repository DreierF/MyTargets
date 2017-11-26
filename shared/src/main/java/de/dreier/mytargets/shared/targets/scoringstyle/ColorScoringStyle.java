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

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import de.dreier.mytargets.shared.models.Score;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.streamwrapper.Stream;

public class ColorScoringStyle extends ScoringStyle {

    private final int maxEndPoints;

    public ColorScoringStyle(@StringRes int title, int maxEndPoints, int... points) {
        super(title, false, points);
        this.maxEndPoints = maxEndPoints;
    }

    @NonNull
    @Override
    public Score getReachedScore(@NonNull End end) {
        int reachedScore = Stream.of(end.getShots())
                .map(s -> getScoreByScoringRing(s.scoringRing, s.index))
                .distinct()
                .reducing(0, (a, b) -> a + b);
        return new Score(reachedScore, maxEndPoints);
    }
}
