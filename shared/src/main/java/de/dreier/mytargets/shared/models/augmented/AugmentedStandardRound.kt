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

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import de.dreier.mytargets.shared.R
import de.dreier.mytargets.shared.models.IIdProvider
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.RoundTemplate
import de.dreier.mytargets.shared.models.db.StandardRound
import de.dreier.mytargets.shared.targets.drawable.CombinedSpot

data class AugmentedStandardRound(
        val standardRound: StandardRound,
        var roundTemplates: MutableList<RoundTemplate>
) : Parcelable, IIdProvider {

    override val id: Long
        get() = standardRound.id

    val targetDrawable: Drawable
        get() {
            val targets = roundTemplates.map { it.targetTemplate.drawable }
            return CombinedSpot(targets)
        }

    fun getDescription(context: Context): String {
        var desc = ""
        for (r in roundTemplates) {
            if (!desc.isEmpty()) {
                desc += "\n"
            }
            desc += context.getString(R.string.round_desc, r.distance, r.endCount,
                    r.shotsPerEnd, r.targetTemplate.diameter)
        }
        return desc
    }

    fun createRoundsFromTemplate(): MutableList<Round> {
        return roundTemplates.map { Round(it) }.toMutableList()
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
