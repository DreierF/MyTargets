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
import androidx.room.PrimaryKey
import android.os.Parcelable
import de.dreier.mytargets.shared.models.IIdSettable
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class StandardRound(
    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0,
    var club: Int = 0,
    var name: String = ""
) : IIdSettable, Parcelable
