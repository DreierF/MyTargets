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

package de.dreier.mytargets.test.utils.rules;

import java.util.Random;

import de.dreier.mytargets.features.settings.SettingsManager;
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

        Training training = saveDefaultTraining(standardRound.getId(), generator);

        Round round1 = new Round(standardRound.getRounds().get(0));
        round1.trainingId = training.getId();
        round1.save();

        Round round2 = new Round(standardRound.getRounds().get(1));
        round2.trainingId = training.getId();
        round2.save();

        randomEnd(round1, 6, generator, 0).save();
        randomEnd(round1, 6, generator, 1).save();

        randomEnd(round2, 6, generator, 0).save();
        randomEnd(round2, 6, generator, 1).save();
    }

}
