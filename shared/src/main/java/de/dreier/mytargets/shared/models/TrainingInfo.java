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

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Training;

public class TrainingInfo {
    public String title;
    public int roundCount;
    public Round round;
    public TimerSettings timerSettings;

    public TrainingInfo() {
    }

    public TrainingInfo(Training training, Round round, TimerSettings timerSettings) {
        this.round = round;
        this.title = training.title;
        this.roundCount = training.getRounds().size();
        this.timerSettings = timerSettings;
    }

    public String getRoundDetails(Context context) {
        if (round.getEnds().isEmpty()) {
            return context.getResources()
                    .getQuantityString(R.plurals.rounds, roundCount, roundCount);
        } else {
            return round.getReachedScore().toString();
        }
    }

    public String getEndDetails(Context context) {
        if (round.getEnds().isEmpty()) {
            if (round.maxEndCount == null) {
                return context.getResources()
                        .getQuantityString(R.plurals.arrows_per_end, round.shotsPerEnd, round.shotsPerEnd);
            } else {
                return context.getResources()
                        .getQuantityString(R.plurals.ends_arrow, round.shotsPerEnd, round.maxEndCount, round.shotsPerEnd);
            }
        } else {
            return context.getResources()
                    .getQuantityString(R.plurals.ends_arrow, round.shotsPerEnd, round.getEnds()
                            .size(), round.shotsPerEnd);
        }
    }
}
