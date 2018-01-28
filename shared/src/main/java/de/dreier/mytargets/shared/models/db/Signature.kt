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

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import de.dreier.mytargets.shared.utils.readBitmap
import de.dreier.mytargets.shared.utils.writeBitmap

@Entity
data class Signature(
        @ColumnInfo(name = "_id")
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,

        var name: String = "",

        /** A bitmap of the signature or null if no signature has been set. */
        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
        var bitmap: Bitmap? = null
) : Parcelable {

    val isSigned: Boolean
        get() = bitmap != null

    fun getName(defaultName: String): String {
        return if (name.isEmpty()) defaultName else name
    }

    override fun describeContents() = 1

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(name)
        dest.writeBitmap(bitmap)
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Signature> {
            override fun createFromParcel(source: Parcel): Signature {
                val id = source.readLong()
                val name = source.readString()
                val bitmap = source.readBitmap()
                return Signature(id, name, bitmap)
            }

            override fun newArray(size: Int) = arrayOfNulls<Signature>(size)
        }
    }
}
