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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.IIdSettable
import de.dreier.mytargets.shared.models.Thumbnail
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Arrow(
    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0,

    var name: String = "",
    var maxArrowNumber: Int = 12,
    var length: String? = "",
    var material: String? = "",
    var spine: String? = "",
    var weight: String? = "",
    var tipWeight: String? = "",
    var vanes: String? = "",
    var nock: String? = "",
    var comment: String? = "",
    var diameter: Dimension = Dimension(5f, Dimension.Unit.MILLIMETER),

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var thumbnail: Thumbnail? = null
) : IIdSettable, Parcelable
