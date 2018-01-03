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
import de.dreier.mytargets.shared.models.db.RoundTemplate
import de.dreier.mytargets.shared.models.db.StandardRound

data class AugmentedStandardRound(
        val standardRound: StandardRound,
        var roundTemplates: MutableList<RoundTemplate>
) : Parcelable {

    constructor(standardRound: StandardRound) : this(standardRound, standardRound.loadRounds())

    fun toStandardRound(): StandardRound {
        standardRound.rounds = roundTemplates
        return standardRound
    }

    constructor(source: Parcel) : this(
            source.readParcelable<StandardRound>(StandardRound::class.java.classLoader),
            source.createTypedArrayList(RoundTemplate.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(standardRound, 0)
        writeTypedList(roundTemplates)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<AugmentedStandardRound> = object : Parcelable.Creator<AugmentedStandardRound> {
            override fun createFromParcel(source: Parcel): AugmentedStandardRound = AugmentedStandardRound(source)
            override fun newArray(size: Int): Array<AugmentedStandardRound?> = arrayOfNulls(size)
        }
    }
}
