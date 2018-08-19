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
package de.dreier.mytargets.shared.models

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import de.dreier.mytargets.shared.R
import de.dreier.mytargets.shared.models.augmented.AugmentedRound
import de.dreier.mytargets.shared.models.augmented.AugmentedTraining

data class TrainingInfo(
        var title: String? = null,
        var roundCount: Int = 0,
        var round: AugmentedRound) : Parcelable {

    constructor(training: AugmentedTraining, round: AugmentedRound) : this(
            title = training.training.title,
            roundCount = training.rounds.size,
            round = round
    )

    constructor(source: Parcel) : this(
            source.readString(),
            source.readInt(),
            source.readParcelable<AugmentedRound>(AugmentedRound::class.java.classLoader)!!
    )

    fun getRoundDetails(context: Context): String {
        return if (round.ends.isEmpty()) {
            context.resources
                    .getQuantityString(R.plurals.rounds, roundCount, roundCount)
        } else {
            round.round.score.toString()
        }
    }

    fun getEndDetails(context: Context): String {
        val (_, _, _, shotsPerEnd, maxEndCount) = round.round
        val ends = round.ends
        return if (ends.isEmpty()) {
            if (maxEndCount == null) {
                context.resources
                        .getQuantityString(R.plurals.arrows_per_end, shotsPerEnd, shotsPerEnd)
            } else {
                context.resources
                        .getQuantityString(R.plurals.ends_arrow, shotsPerEnd, maxEndCount, shotsPerEnd)
            }
        } else {
            context.resources
                    .getQuantityString(R.plurals.ends_arrow, shotsPerEnd, ends
                            .size, shotsPerEnd)
        }
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(title)
        writeInt(roundCount)
        writeParcelable(round, 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TrainingInfo> = object : Parcelable.Creator<TrainingInfo> {
            override fun createFromParcel(source: Parcel): TrainingInfo = TrainingInfo(source)
            override fun newArray(size: Int): Array<TrainingInfo?> = arrayOfNulls(size)
        }
    }
}
