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

package de.dreier.mytargets.shared.models.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import de.dreier.mytargets.shared.models.IIdSettable

@Entity(foreignKeys = [(ForeignKey(entity = End::class,
                parentColumns = ["id"],
                childColumns = ["endId"],
                onDelete = CASCADE))])
data class Shot(
        @PrimaryKey(autoGenerate = true)
        override var id: Long = 0,

        // The index of the shot in the containing end
        var index: Int = 0,

        var endId: Long? = null,

        var x: Float = 0f,

        var y: Float = 0f,

        var scoringRing: Int = NOTHING_SELECTED,

        // Is the actual number of the arrow not its index, arrow id or something else
        var arrowNumber: String? = null
) : IIdSettable, Comparable<Shot>, Parcelable {
    constructor(i: Int) : this(index = i)

    override fun compareTo(other: Shot): Int {
        return if (other.scoringRing == scoringRing) {
            0
        } else if (other.scoringRing >= 0 && scoringRing >= 0) {
            scoringRing - other.scoringRing
        } else {
            other.scoringRing - scoringRing
        }
    }

    constructor(source: Parcel) : this(
            source.readLong(),
            source.readInt(),
            source.readValue(Long::class.java.classLoader) as Long?,
            source.readFloat(),
            source.readFloat(),
            source.readInt(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(id)
        writeInt(index)
        writeValue(endId)
        writeFloat(x)
        writeFloat(y)
        writeInt(scoringRing)
        writeString(arrowNumber)
    }

    companion object {
        const val NOTHING_SELECTED = -2
        const val MISS = -1

        @JvmField
        val CREATOR: Parcelable.Creator<Shot> = object : Parcelable.Creator<Shot> {
            override fun createFromParcel(source: Parcel): Shot = Shot(source)
            override fun newArray(size: Int): Array<Shot?> = arrayOfNulls(size)
        }
    }
}
