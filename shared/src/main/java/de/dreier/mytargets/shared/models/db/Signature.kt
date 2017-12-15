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

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Parcelable
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.BaseModel
import de.dreier.mytargets.shared.AppDatabase
import de.dreier.mytargets.shared.utils.typeconverters.BitmapConverter
import de.dreier.mytargets.shared.utils.typeconverters.BitmapParcelConverter
import kotlinx.android.parcel.Parcelize
import org.parceler.ParcelPropertyConverter

@SuppressLint("ParcelCreator")
@Parcelize
@Table(database = AppDatabase::class)
data class Signature(
        @Column(name = "_id")
        @PrimaryKey(autoincrement = true)
        var _id: Long? = 0,

        @Column
        var name: String = "",

        /** A bitmap of the signature or null if no signature has been set. */
        @Column(typeConverter = BitmapConverter::class)
        @ParcelPropertyConverter(BitmapParcelConverter::class)
        var bitmap: Bitmap? = null
) : BaseModel(), Parcelable {

    val isSigned: Boolean
        get() = bitmap != null

    fun getName(defaultName: String): String {
        return if (name.isEmpty()) defaultName else name
    }

    companion object {
        operator fun get(signatureId: Long): Signature? {
            return SQLite.select()
                    .from(Signature::class.java)
                    .where(Signature_Table._id.eq(signatureId))
                    .querySingle()
        }
    }
}
