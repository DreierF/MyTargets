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

package de.dreier.mytargets.shared.models.augmented

import android.annotation.SuppressLint
import android.os.Parcelable
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.StandardRound
import de.dreier.mytargets.shared.models.db.Training
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class AugmentedTraining(
        val training: Training,
        var rounds: MutableList<AugmentedRound>
) : Parcelable {
    constructor(training: Training) : this(training, training.loadRounds()
            .map { AugmentedRound(it) }
            .toMutableList())

    fun toTraining(): Training {
        training.rounds = rounds.map {it.toRound()}.toMutableList()
        return training
    }

    fun initRoundsFromTemplate(standardRound: StandardRound) {
        rounds = mutableListOf()
        for (template in standardRound.loadRounds()) {
            val round = AugmentedRound(Round(template))
            round.round.trainingId = training.id
            round.round.target = template.targetTemplate
            round.round.comment = ""
            round.round.save()
            rounds.add(round)
        }
    }
}
