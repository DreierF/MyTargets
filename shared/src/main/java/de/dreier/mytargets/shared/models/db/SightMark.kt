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

import android.annotation.SuppressLint
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.Dimension.Unit.METER
import de.dreier.mytargets.shared.models.IIdSettable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
@Entity(foreignKeys = [(ForeignKey(entity = Bow::class,
                parentColumns = ["id"],
                childColumns = ["bowId"],
                onDelete = CASCADE))])
data class SightMark(
        @PrimaryKey(autoGenerate = true)
        override var id: Long = 0,

        var bowId: Long? = null,

        var distance: Dimension = Dimension(18f, METER),

        var value: String? = ""
) : IIdSettable, Parcelable
