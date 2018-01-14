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

package de.dreier.mytargets.shared.models.augmented

import android.os.Parcel
import android.os.Parcelable
import de.dreier.mytargets.shared.models.IIdSettable
import de.dreier.mytargets.shared.models.dao.EndDAO
import de.dreier.mytargets.shared.models.db.End
import de.dreier.mytargets.shared.models.db.EndImage
import de.dreier.mytargets.shared.models.db.Shot

data class AugmentedEnd(
        val end: End,
        var shots: MutableList<Shot>,
        var images: MutableList<EndImage>
) : Parcelable, IIdSettable {
    override var id: Long
        get() = end.id
        set(value) {
            end.id = value
        }

    constructor(end: End) : this(end, EndDAO.loadShots(end.id).toMutableList(), EndDAO.loadEndImages(end.id).toMutableList())

    fun save() {
        EndDAO.saveEnd(end, images, shots)
    }

    val isEmpty: Boolean
        get() = shots.any { it.scoringRing == Shot.NOTHING_SELECTED } && images.isEmpty()

    constructor(source: Parcel) : this(
            source.readParcelable<End>(End::class.java.classLoader),
            source.createTypedArrayList(Shot.CREATOR),
            source.createTypedArrayList(EndImage.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(end, 0)
        writeTypedList(shots)
        writeTypedList(images)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<AugmentedEnd> = object : Parcelable.Creator<AugmentedEnd> {
            override fun createFromParcel(source: Parcel): AugmentedEnd = AugmentedEnd(source)
            override fun newArray(size: Int): Array<AugmentedEnd?> = arrayOfNulls(size)
        }
    }
}
