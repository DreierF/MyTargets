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
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import de.dreier.mytargets.shared.models.IIdSettable
import de.dreier.mytargets.shared.models.Score
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalTime

@SuppressLint("ParcelCreator")
@Parcelize
@Entity(foreignKeys = [
    ForeignKey(entity = Round::class,
            parentColumns = ["id"],
            childColumns = ["roundId"],
            onDelete = CASCADE)
])
data class End(

        @PrimaryKey(autoGenerate = true)
        override var id: Long = 0,

        var index: Int = 0,

        var roundId: Long? = null,

        var exact: Boolean = false,

        var saveTime: LocalTime? = null,

        var comment: String = "",

        @Embedded
        var score: Score = Score()
) : IIdSettable, Parcelable
