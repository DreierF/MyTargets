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
}
