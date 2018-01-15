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

import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.models.augmented.AugmentedRound
import de.dreier.mytargets.shared.models.dao.RoundDAO
import de.dreier.mytargets.shared.models.dao.StandardRoundDAO
import de.dreier.mytargets.shared.models.dao.TrainingDAO
import de.dreier.mytargets.shared.targets.models.WAFull
import de.dreier.mytargets.shared.views.TargetViewBase
import java.util.*

class MiniDbTestRule : DbTestRuleBase() {

    override fun addDatabaseContent() {
        SettingsManager.target = Target(WAFull.ID, 0, Dimension(122f, Dimension.Unit.CENTIMETER))
        SettingsManager.distance = Dimension(50f, Dimension.Unit.METER)
        SettingsManager.indoor = false
        SettingsManager.inputMethod = TargetViewBase.EInputMethod.PLOTTING
        SettingsManager.timerEnabled = true
        SettingsManager.shotsPerEnd = 6
        addRandomTraining(578459341)
        addRandomTraining(454459456)
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
        RoundDAO.saveRound(round1)

        round2.ends.add(randomEnd(round2, 6, generator))
        round2.ends.add(randomEnd(round2, 6, generator))
        RoundDAO.saveRound(round2)
    }

}
