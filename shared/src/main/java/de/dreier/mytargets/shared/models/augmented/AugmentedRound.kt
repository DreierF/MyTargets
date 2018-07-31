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
import de.dreier.mytargets.shared.models.db.Round

data class AugmentedRound(
        val round: Round,
        var ends: MutableList<AugmentedEnd>
) : Parcelable {

    constructor(source: Parcel) : this(
            source.readParcelable<Round>(Round::class.java.classLoader),
            source.createTypedArrayList(AugmentedEnd.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(round, 0)
        writeTypedList(ends)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<AugmentedRound> = object : Parcelable.Creator<AugmentedRound> {
            override fun createFromParcel(source: Parcel): AugmentedRound = AugmentedRound(source)
            override fun newArray(size: Int): Array<AugmentedRound?> = arrayOfNulls(size)
        }
    }
}
