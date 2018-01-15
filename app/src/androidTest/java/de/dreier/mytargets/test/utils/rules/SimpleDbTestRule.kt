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
import de.dreier.mytargets.shared.models.augmented.AugmentedStandardRound
import de.dreier.mytargets.shared.models.augmented.AugmentedTraining
import de.dreier.mytargets.shared.models.dao.StandardRoundDAO
import de.dreier.mytargets.shared.models.dao.TrainingDAO
import de.dreier.mytargets.shared.models.db.*
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

        val round1 = Round(rounds[0])
        round1.trainingId = id
        round1.save()
        val r1 = AugmentedRound(round1)

        val round2 = Round(rounds[1])
        round2.trainingId = id
        round2.save()
        val r2 = AugmentedRound(round2)

        randomEnd(r1, 6, generator).save()
        randomEnd(r1, 6, generator).save()
        randomEnd(r1, 6, generator).save()
        randomEnd(r1, 6, generator).save()
        randomEnd(r1, 6, generator).save()
        randomEnd(r1, 6, generator).save()

        randomEnd(r2, 6, generator).save()
        randomEnd(r2, 6, generator).save()
        randomEnd(r2, 6, generator).save()
        randomEnd(r2, 6, generator).save()
        randomEnd(r2, 6, generator).save()
        randomEnd(r2, 6, generator).save()
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
        val standardRound = AugmentedStandardRound(StandardRoundDAO.loadStandardRound(32L))

        val training = saveDefaultTraining(standardRound.id, generator)

        val at = AugmentedTraining(training)
        at.initRoundsFromTemplate(standardRound)
        TrainingDAO.saveTraining(at.training, at.rounds)

        val round1 = at.rounds[0]

        val round2 = at.rounds[1]

        randomEnd(round1, 6, generator).save()
        randomEnd(round1, 6, generator).save()
        randomEnd(round1, 6, generator).save()
        randomEnd(round1, 6, generator).save()
        randomEnd(round1, 6, generator).save()
        randomEnd(round1, 6, generator).save()

        randomEnd(round2, 6, generator).save()
        randomEnd(round2, 6, generator).save()
        randomEnd(round2, 6, generator).save()
        randomEnd(round2, 6, generator).save()
        randomEnd(round2, 6, generator).save()
        randomEnd(round2, 6, generator).save()
    }

    private fun addFullTraining(bow: Bow) {
        val standardRound = AugmentedStandardRound(StandardRoundDAO.loadStandardRound(32L))

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
        val at = AugmentedTraining(training)
        at.initRoundsFromTemplate(standardRound)
        TrainingDAO.saveTraining(at.training, at.rounds.map { it.round })

        val round1 = at.rounds[0]

        val round2 = at.rounds[1]

        buildEnd(round1, 1, 1, 2, 3, 3, 4).save()
        buildEnd(round1, 0, 0, 1, 2, 2, 3).save()
        buildEnd(round1, 1, 1, 1, 3, 4, 4).save()
        buildEnd(round1, 0, 1, 1, 1, 2, 3).save()
        buildEnd(round1, 1, 2, 3, 3, 4, 5).save()
        buildEnd(round1, 1, 2, 2, 3, 3, 3).save()

        buildEnd(round2, 1, 2, 2, 3, 4, 5).save()
        buildEnd(round2, 0, 0, 1, 2, 2, 3).save()
        buildEnd(round2, 0, 1, 2, 2, 2, 3).save()
        buildEnd(round2, 1, 1, 2, 3, 4, 4).save()
        buildEnd(round2, 1, 2, 2, 3, 3, 3).save()
        buildEnd(round2, 1, 2, 2, 3, 3, 4).save()
    }
}
