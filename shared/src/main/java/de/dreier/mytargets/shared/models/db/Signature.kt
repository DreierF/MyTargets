/*
 * Copyright (C) 2017 Florian Dreier
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

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.BaseModel
import de.dreier.mytargets.shared.AppDatabase
import de.dreier.mytargets.shared.utils.typeconverters.BitmapConverter
import de.dreier.mytargets.shared.utils.typeconverters.readBitmap
import de.dreier.mytargets.shared.utils.typeconverters.writeBitmap

@Table(database = AppDatabase::class)
data class Signature(
        @Column(name = "_id")
        @PrimaryKey(autoincrement = true)
        var _id: Long? = 0,

        @Column
        var name: String = "",

        /** A bitmap of the signature or null if no signature has been set. */
        @Column(typeConverter = BitmapConverter::class)
        @JvmField //Bug dbFlow
        var bitmap: Bitmap? = null
) : BaseModel(), Parcelable {

    val isSigned: Boolean
        get() = bitmap != null

    fun getName(defaultName: String): String {
        return if (name.isEmpty()) defaultName else name
    }

    override fun describeContents() = 1

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(_id!!)
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

        operator fun get(signatureId: Long): Signature? {
            return SQLite.select()
                    .from(Signature::class.java)
                    .where(Signature_Table._id.eq(signatureId))
                    .querySingle()
        }
    }
}
