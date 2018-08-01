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

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import android.os.Parcelable
import de.dreier.mytargets.shared.models.Image
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Bow::class,
            parentColumns = ["id"],
            childColumns = ["bowId"],
            onDelete = CASCADE
        )
    ],
    indices = [
        Index(value = ["bowId"])
    ]
)
data class BowImage(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    override var fileName: String = "",

    var bowId: Long? = null
) : Image, Parcelable
