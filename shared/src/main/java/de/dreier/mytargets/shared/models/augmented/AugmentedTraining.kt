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

package de.dreier.mytargets.shared.models.augmented

import android.os.Parcel
import android.os.Parcelable
import de.dreier.mytargets.shared.models.dao.TrainingDAO
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.Training

data class AugmentedTraining(
        val training: Training,
        var rounds: MutableList<AugmentedRound>
) : Parcelable {
    constructor(training: Training) : this(training, TrainingDAO.loadRounds(training.id)
            .map { AugmentedRound(it) }
            .toMutableList())

    fun initRoundsFromTemplate(standardRound: AugmentedStandardRound) {
        rounds = mutableListOf()
        for (template in standardRound.roundTemplates) {
            val round = AugmentedRound(Round(template))
            round.round.trainingId = training.id
            round.round.target = template.targetTemplate
            round.round.comment = ""
            rounds.add(round)
        }
    }

    constructor(source: Parcel) : this(
            source.readParcelable<Training>(Training::class.java.classLoader),
            source.createTypedArrayList(AugmentedRound.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(training, 0)
        writeTypedList(rounds)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<AugmentedTraining> = object : Parcelable.Creator<AugmentedTraining> {
            override fun createFromParcel(source: Parcel): AugmentedTraining = AugmentedTraining(source)
            override fun newArray(size: Int): Array<AugmentedTraining?> = arrayOfNulls(size)
        }
    }
}
