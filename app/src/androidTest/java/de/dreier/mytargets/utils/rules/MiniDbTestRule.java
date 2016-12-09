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

package de.dreier.mytargets.utils.rules;

import java.util.Random;

import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.targets.models.WAFull;
import de.dreier.mytargets.shared.views.TargetViewBase;

public class MiniDbTestRule extends DbTestRuleBase {

    @Override
    protected void addDatabaseContent() {
        SettingsManager.setTarget(
                new Target(WAFull.ID, 0, new Dimension(122, Dimension.Unit.CENTIMETER)));
        SettingsManager.setDistance(new Dimension(50, Dimension.Unit.METER));
        SettingsManager.setIndoor(false);
        SettingsManager.setInputMethod(TargetViewBase.EInputMethod.PLOTTING);
        SettingsManager.setTimerEnabled(true);
        SettingsManager.setShotsPerEnd(6);
        addRandomTraining(578459341);
        addRandomTraining(454459456);
    }

    private void addRandomTraining(int seed) {
        Random generator = new Random(seed);
        StandardRound standardRound = StandardRound.get(32L);

        Training training = insertDefaultTraining(standardRound, generator);

        Round round1 = new Round();
        round1.trainingId = training.getId();
        round1.info = standardRound.getRounds().get(0);
        round1.setTarget(round1.info.getTargetTemplate());
        round1.comment = "";
        round1.insert();

        Round round2 = new Round();
        round2.trainingId = training.getId();
        round2.info = standardRound.getRounds().get(1);
        round2.setTarget(round2.info.getTargetTemplate());
        round2.comment = "";
        round2.insert();

        randomEnd(training, round1, 6, generator, 0).insert();
        randomEnd(training, round1, 6, generator, 1).insert();

        randomEnd(training, round2, 6, generator, 0).insert();
        randomEnd(training, round2, 6, generator, 1).insert();
    }

}