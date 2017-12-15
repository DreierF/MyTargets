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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.augmented.AugmentedEnd;
import de.dreier.mytargets.shared.models.augmented.AugmentedRound;
import de.dreier.mytargets.shared.models.augmented.AugmentedTraining;
import de.dreier.mytargets.shared.models.db.Round;

public class TrainingInfo {
    @Nullable
    public String title;
    public int roundCount;
    public AugmentedRound round;

    public TrainingInfo() {
    }

    public TrainingInfo(@NonNull AugmentedTraining training, AugmentedRound round) {
        this.round = round;
        this.title = training.getTraining().getTitle();
        this.roundCount = training.getRounds().size();
    }

    @NonNull
    public String getRoundDetails(@NonNull Context context) {
        if (round.getEnds().isEmpty()) {
            return context.getResources()
                    .getQuantityString(R.plurals.rounds, roundCount, roundCount);
        } else {
            return round.getReachedScore().toString();
        }
    }

    @NonNull
    public String getEndDetails(@NonNull Context context) {
        Round simpleRound = round.getRound();
        List<AugmentedEnd> ends = round.getEnds();
        if (ends.isEmpty()) {
            if (simpleRound.getMaxEndCount() == null) {
                return context.getResources()
                        .getQuantityString(R.plurals.arrows_per_end, simpleRound.getShotsPerEnd(), simpleRound
                                .getShotsPerEnd());
            } else {
                return context.getResources()
                        .getQuantityString(R.plurals.ends_arrow, simpleRound.getShotsPerEnd(), simpleRound
                                .getMaxEndCount(), simpleRound.getShotsPerEnd());
            }
        } else {
            return context.getResources()
                    .getQuantityString(R.plurals.ends_arrow, simpleRound.getShotsPerEnd(), ends
                            .size(), simpleRound.getShotsPerEnd());
        }
    }
}
