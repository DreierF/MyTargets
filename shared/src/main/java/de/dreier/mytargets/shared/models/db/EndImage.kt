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
import de.dreier.mytargets.shared.models.Image

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = End::class,
            parentColumns = ["id"],
            childColumns = ["endId"],
            onDelete = CASCADE
        )
    ]
)
data class EndImage(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    override var fileName: String = "",

    var endId: Long? = null
) : Image, Parcelable {
    constructor(imageFile: String) : this(fileName = imageFile)

    constructor(source: Parcel) : this(
        source.readLong(),
        source.readString(),
        source.readValue(Long::class.java.classLoader) as Long?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(id)
        writeString(fileName)
        writeValue(endId)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<EndImage> = object : Parcelable.Creator<EndImage> {
            override fun createFromParcel(source: Parcel): EndImage = EndImage(source)
            override fun newArray(size: Int): Array<EndImage?> = arrayOfNulls(size)
        }
    }
}
