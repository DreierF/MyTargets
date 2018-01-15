/*
 * Copyright (C) 2018 Florian Dreier
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

package de.dreier.mytargets.test.utils.rules

import android.support.test.InstrumentationRegistry
import de.dreier.mytargets.R
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.EWeather
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.models.augmented.AugmentedRound
import de.dreier.mytargets.shared.models.dao.RoundDAO
import de.dreier.mytargets.shared.models.dao.StandardRoundDAO
import de.dreier.mytargets.shared.models.dao.TrainingDAO
import de.dreier.mytargets.shared.models.db.Bow
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.RoundTemplate
import de.dreier.mytargets.shared.models.db.Training
import de.dreier.mytargets.shared.targets.models.WAFull
import de.dreier.mytargets.shared.views.TargetViewBase
import org.threeten.bp.LocalDate
import java.util.*

class SimpleDbTestRule : DbTestRuleBase() {

    private val customRounds: List<RoundTemplate>
        get() = Arrays.asList(getRoundTemplate(0, 50), getRoundTemplate(1, 30))

    override fun addDatabaseContent() {
        SettingsManager.target = Target(WAFull.ID, 0, Dimension(122f, Dimension.Unit.CENTIMETER))
        SettingsManager.distance = Dimension(50f, Dimension.Unit.METER)
        SettingsManager.indoor = false
        SettingsManager.inputMethod = TargetViewBase.EInputMethod.PLOTTING
        SettingsManager.timerEnabled = true
        SettingsManager.shotsPerEnd = 6
        val bow = addBow("PSE Fever")
        addBow("PSE Something")
        addArrow("Arrow 1")
        addArrow("Arrow 2")
        addRandomTraining(578459341)
        addRandomTraining(454459456)
        addRandomTraining(763478984)
        addRandomTraining(453891238)
        addRandomTraining(719789367)
        addRandomTraining(658795439)
        addFullTraining(bow)
        addPracticeTraining(438573454)
    }

    private fun addPracticeTraining(seed: Int) {
        val generator = Random(seed.toLong())
        val rounds = customRounds

        val (id) = saveDefaultTraining(null, generator)

        val round1r = Round(rounds[0])
        round1r.trainingId = id
        RoundDAO.saveRound(round1r)
        val round1 = AugmentedRound(round1r, mutableListOf())

        val round2r = Round(rounds[1])
        round2r.trainingId = id
        RoundDAO.saveRound(round2r)
        val round2 = AugmentedRound(round2r, mutableListOf())

        round1.ends.add(randomEnd(round1, 6, generator))
        round1.ends.add(randomEnd(round1, 6, generator))
        round1.ends.add(randomEnd(round1, 6, generator))
        round1.ends.add(randomEnd(round1, 6, generator))
        round1.ends.add(randomEnd(round1, 6, generator))
        round1.ends.add(randomEnd(round1, 6, generator))
        RoundDAO.saveRound(round1)

        round2.ends.add(randomEnd(round2, 6, generator))
        round2.ends.add(randomEnd(round2, 6, generator))
        round2.ends.add(randomEnd(round2, 6, generator))
        round2.ends.add(randomEnd(round2, 6, generator))
        round2.ends.add(randomEnd(round2, 6, generator))
        round2.ends.add(randomEnd(round2, 6, generator))
        RoundDAO.saveRound(round2)
    }

    private fun getRoundTemplate(index: Int, distance: Int): RoundTemplate {
        val roundTemplate = RoundTemplate()
        roundTemplate.index = index
        roundTemplate.targetTemplate = Target(WAFull.ID, 0, Dimension(60f, Dimension.Unit.CENTIMETER))
        roundTemplate.shotsPerEnd = 6
        roundTemplate.endCount = 6
        roundTemplate.distance = Dimension(distance.toFloat(), Dimension.Unit.METER)
        return roundTemplate
    }

    private fun addRandomTraining(seed: Int) {
        val generator = Random(seed.toLong())
        val standardRound = StandardRoundDAO.loadAugmentedStandardRound(32L)

        val training = saveDefaultTraining(standardRound.id, generator)

        val rounds = standardRound.createRoundsFromTemplate()
        TrainingDAO.saveTraining(training, rounds)

        val round1 = AugmentedRound(rounds[0], mutableListOf())
        val round2 = AugmentedRound(rounds[1], mutableListOf())

        round1.ends.add(randomEnd(round1, 6, generator))
        round1.ends.add(randomEnd(round1, 6, generator))
        round1.ends.add(randomEnd(round1, 6, generator))
        round1.ends.add(randomEnd(round1, 6, generator))
        round1.ends.add(randomEnd(round1, 6, generator))
        round1.ends.add(randomEnd(round1, 6, generator))
        RoundDAO.saveRound(round1)

        round2.ends.add(randomEnd(round2, 6, generator))
        round2.ends.add(randomEnd(round2, 6, generator))
        round2.ends.add(randomEnd(round2, 6, generator))
        round2.ends.add(randomEnd(round2, 6, generator))
        round2.ends.add(randomEnd(round2, 6, generator))
        round2.ends.add(randomEnd(round2, 6, generator))
        RoundDAO.saveRound(round2)
    }

    private fun addFullTraining(bow: Bow) {
        val standardRound = StandardRoundDAO.loadAugmentedStandardRound(32L)

        val training = Training()
        training.title = InstrumentationRegistry.getTargetContext().getString(R.string.training)
        training.date = LocalDate.of(2016, 7, 15)
        training.weather = EWeather.SUNNY
        training.windSpeed = 1
        training.windDirection = 0
        training.standardRoundId = standardRound.id
        training.bowId = bow.id
        training.arrowId = null
        training.arrowNumbering = false

        val rounds = standardRound.createRoundsFromTemplate()
        TrainingDAO.saveTraining(training, rounds)

        val round1 = AugmentedRound(rounds[0], mutableListOf())
        val round2 = AugmentedRound(rounds[1], mutableListOf())

        round1.ends.add(buildEnd(round1, 1, 1, 2, 3, 3, 4))
        round1.ends.add(buildEnd(round1, 0, 0, 1, 2, 2, 3))
        round1.ends.add(buildEnd(round1, 1, 1, 1, 3, 4, 4))
        round1.ends.add(buildEnd(round1, 0, 1, 1, 1, 2, 3))
        round1.ends.add(buildEnd(round1, 1, 2, 3, 3, 4, 5))
        round1.ends.add(buildEnd(round1, 1, 2, 2, 3, 3, 3))
        RoundDAO.saveRound(round1)

        round2.ends.add(buildEnd(round2, 1, 2, 2, 3, 4, 5))
        round2.ends.add(buildEnd(round2, 0, 0, 1, 2, 2, 3))
        round2.ends.add(buildEnd(round2, 0, 1, 2, 2, 2, 3))
        round2.ends.add(buildEnd(round2, 1, 1, 2, 3, 4, 4))
        round2.ends.add(buildEnd(round2, 1, 2, 2, 3, 3, 3))
        round2.ends.add(buildEnd(round2, 1, 2, 2, 3, 3, 4))
        RoundDAO.saveRound(round2)
    }
}
