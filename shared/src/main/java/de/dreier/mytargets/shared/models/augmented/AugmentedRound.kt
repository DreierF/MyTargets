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
import de.dreier.mytargets.shared.models.Score
import de.dreier.mytargets.shared.models.dao.RoundDAO
import de.dreier.mytargets.shared.models.db.End
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.Shot
import de.dreier.mytargets.shared.models.sum

data class AugmentedRound(
        val round: Round,
        var ends: MutableList<AugmentedEnd>
) : Parcelable {
    constructor(round: Round) : this(round, RoundDAO.loadEnds(round.id)
            .map { AugmentedEnd(it) }
            .toMutableList())

    val reachedScore: Score
        get() {
            val target = round.target
            return ends.map { target.getReachedScore(it.shots) }.sum()
        }

    /**
     * Adds a new end to the internal list of ends, but does not yet save it.
     *
     * @return Returns the newly created end
     */
    fun addEnd(): AugmentedEnd {
        val end = End(index = ends.size, roundId = round.id)
        val augmentedEnd = AugmentedEnd(end, (0 until round.shotsPerEnd)
                .map { Shot(it) }.toMutableList(), mutableListOf())
        augmentedEnd.save()
        ends.add(augmentedEnd)
        return augmentedEnd
    }

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
