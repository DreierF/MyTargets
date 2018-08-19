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

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE
import android.os.Parcel
import android.os.Parcelable
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.IIdSettable
import de.dreier.mytargets.shared.models.Target

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = StandardRound::class,
            parentColumns = ["id"],
            childColumns = ["standardRoundId"],
            onDelete = CASCADE
        )
    ],
    indices = [
        Index(value = ["standardRoundId"])
    ]
)
data class RoundTemplate(
    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0,

    var standardRoundId: Long? = null,

    var index: Int = 0,

    var shotsPerEnd: Int = 0,

    var endCount: Int = 0,

    var distance: Dimension = Dimension.UNKNOWN,

    @Embedded
    var targetTemplate: Target = Target()
) : IIdSettable, Parcelable {
    constructor(source: Parcel) : this(
        source.readLong(),
        source.readValue(Long::class.java.classLoader) as Long?,
        source.readInt(),
        source.readInt(),
        source.readInt(),
        source.readParcelable<Dimension>(Dimension::class.java.classLoader)!!,
        source.readParcelable<Target>(Target::class.java.classLoader)!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(id)
        writeValue(standardRoundId)
        writeInt(index)
        writeInt(shotsPerEnd)
        writeInt(endCount)
        writeParcelable(distance, 0)
        writeParcelable(targetTemplate, 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<RoundTemplate> =
            object : Parcelable.Creator<RoundTemplate> {
                override fun createFromParcel(source: Parcel): RoundTemplate = RoundTemplate(source)
                override fun newArray(size: Int): Array<RoundTemplate?> = arrayOfNulls(size)
            }
    }
}
