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

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import org.threeten.bp.LocalDate;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import de.dreier.mytargets.R;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.EWeather;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.RoundTemplate;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.targets.models.WAFull;
import de.dreier.mytargets.shared.views.TargetViewBase;

public class SimpleDbTestRule extends DbTestRuleBase {

    @Override
    protected void addDatabaseContent() {
        SettingsManager.INSTANCE.setTarget(
                new Target(WAFull.ID, 0, new Dimension(122, Dimension.Unit.CENTIMETER)));
        SettingsManager.INSTANCE.setDistance(new Dimension(50, Dimension.Unit.METER));
        SettingsManager.INSTANCE.setIndoor(false);
        SettingsManager.INSTANCE.setInputMethod(TargetViewBase.EInputMethod.PLOTTING);
        SettingsManager.INSTANCE.setTimerEnabled(true);
        SettingsManager.INSTANCE.setShotsPerEnd(6);
        Bow bow = addBow("PSE Fever");
        addBow("PSE Something");
        addArrow("Arrow 1");
        addArrow("Arrow 2");
        addRandomTraining(578459341);
        addRandomTraining(454459456);
        addRandomTraining(763478984);
        addRandomTraining(453891238);
        addRandomTraining(719789367);
        addRandomTraining(658795439);
        addFullTraining(bow);
        addPracticeTraining(438573454);
    }

    private void addPracticeTraining(int seed) {
        Random generator = new Random(seed);
        List<RoundTemplate> rounds = getCustomRounds();

        Training training = saveDefaultTraining(null, generator);

        Round round1 = new Round(rounds.get(0));
        round1.setTrainingId(training.getId());
        round1.save();

        Round round2 = new Round(rounds.get(1));
        round2.setTrainingId(training.getId());
        round2.save();

        randomEnd(round1, 6, generator, 0).save();
        randomEnd(round1, 6, generator, 1).save();
        randomEnd(round1, 6, generator, 2).save();
        randomEnd(round1, 6, generator, 3).save();
        randomEnd(round1, 6, generator, 4).save();
        randomEnd(round1, 6, generator, 5).save();

        randomEnd(round2, 6, generator, 0).save();
        randomEnd(round2, 6, generator, 1).save();
        randomEnd(round2, 6, generator, 2).save();
        randomEnd(round2, 6, generator, 3).save();
        randomEnd(round2, 6, generator, 4).save();
        randomEnd(round2, 6, generator, 5).save();
    }

    @NonNull
    private List<RoundTemplate> getCustomRounds() {
        return Arrays.asList(getRoundTemplate(0, 50), getRoundTemplate(1, 30));
    }

    @NonNull
    private RoundTemplate getRoundTemplate(int index, int distance) {
        RoundTemplate roundTemplate = new RoundTemplate();
        roundTemplate.setIndex(index);
        roundTemplate.setTargetTemplate(
                new Target(WAFull.ID, 0, new Dimension(60, Dimension.Unit.CENTIMETER)));
        roundTemplate.setShotsPerEnd(6);
        roundTemplate.setEndCount(6);
        roundTemplate.setDistance(new Dimension(distance, Dimension.Unit.METER));
        return roundTemplate;
    }

    private void addRandomTraining(int seed) {
        Random generator = new Random(seed);
        StandardRound standardRound = StandardRound.Companion.get(32L);

        Training training = saveDefaultTraining(standardRound.getId(), generator);

        Round round1 = new Round(standardRound.loadRounds().get(0));
        round1.setTrainingId(training.getId());
        round1.save();

        Round round2 = new Round(standardRound.loadRounds().get(1));
        round2.setTrainingId(training.getId());
        round2.save();

        randomEnd(round1, 6, generator, 0).save();
        randomEnd(round1, 6, generator, 1).save();
        randomEnd(round1, 6, generator, 2).save();
        randomEnd(round1, 6, generator, 3).save();
        randomEnd(round1, 6, generator, 4).save();
        randomEnd(round1, 6, generator, 5).save();

        randomEnd(round2, 6, generator, 0).save();
        randomEnd(round2, 6, generator, 1).save();
        randomEnd(round2, 6, generator, 2).save();
        randomEnd(round2, 6, generator, 3).save();
        randomEnd(round2, 6, generator, 4).save();
        randomEnd(round2, 6, generator, 5).save();
    }

    private void addFullTraining(@NonNull Bow bow) {
        StandardRound standardRound = StandardRound.Companion.get(32L);

        Training training = new Training();
        training.setTitle(InstrumentationRegistry.getTargetContext().getString(R.string.training));
        training.setDate(LocalDate.of(2016, 7, 15));
        training.setWeather(EWeather.SUNNY);
        training.setWindSpeed(1);
        training.setWindDirection(0);
        training.setStandardRoundId(standardRound.getId());
        training.setBowId(bow.getId());
        training.setArrowId(null);
        training.setArrowNumbering(false);
        training.save();

        Round round1 = new Round(standardRound.loadRounds().get(0));
        round1.setTrainingId(training.getId());
        round1.save();

        Round round2 = new Round(standardRound.loadRounds().get(1));
        round2.setTrainingId(training.getId());
        round2.save();

        buildEnd(round1, 1, 1, 2, 3, 3, 4).save();
        buildEnd(round1, 0, 0, 1, 2, 2, 3).save();
        buildEnd(round1, 1, 1, 1, 3, 4, 4).save();
        buildEnd(round1, 0, 1, 1, 1, 2, 3).save();
        buildEnd(round1, 1, 2, 3, 3, 4, 5).save();
        buildEnd(round1, 1, 2, 2, 3, 3, 3).save();

        buildEnd(round2, 1, 2, 2, 3, 4, 5).save();
        buildEnd(round2, 0, 0, 1, 2, 2, 3).save();
        buildEnd(round2, 0, 1, 2, 2, 2, 3).save();
        buildEnd(round2, 1, 1, 2, 3, 4, 4).save();
        buildEnd(round2, 1, 2, 2, 3, 3, 3).save();
        buildEnd(round2, 1, 2, 2, 3, 3, 4).save();
    }
}
