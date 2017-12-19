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

package de.dreier.mytargets.shared.models.augmented

import android.annotation.SuppressLint
import android.os.Parcelable
import de.dreier.mytargets.shared.models.db.End
import de.dreier.mytargets.shared.models.db.EndImage
import de.dreier.mytargets.shared.models.db.Shot
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class AugmentedEnd(
        val end: End,
        var shots: MutableList<Shot>,
        var images: MutableList<EndImage>
) : Parcelable {
    constructor(end: End) : this(end, end.loadShots()!!.toMutableList(), end.loadImages()!!.toMutableList())

    fun toEnd(): End {
        end.shots = shots
        end.images = images
        return end
    }

    val isEmpty: Boolean
        get() = shots.any { it.scoringRing == Shot.NOTHING_SELECTED } && images.isEmpty()
}
