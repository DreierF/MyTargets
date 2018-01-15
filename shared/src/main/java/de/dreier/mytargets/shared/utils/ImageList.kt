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

package de.dreier.mytargets.shared.utils

import android.annotation.SuppressLint
import android.os.Parcelable
import de.dreier.mytargets.shared.models.db.EndImage
import de.dreier.mytargets.shared.models.Image
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
class ImageList(
        internal var images: MutableList<String> = mutableListOf(),
        private var removed: MutableList<String> = mutableListOf()
) : Parcelable {

    val isEmpty: Boolean
        get() = images.isEmpty()

    val removedImages: List<String>
        get() = removed

    constructor(images: List<Image>) : this(
            images = images.map(Image::fileName).toMutableList()
    )

    fun size(): Int {
        return images.size
    }

    operator fun get(i: Int): Image {
        return EndImage(images[i])
    }

    fun remove(i: Int) {
        removed.add(images.removeAt(i))
    }

    fun addAll(images: List<String>) {
        this.images.addAll(images)
    }

    fun toEndImageList(): MutableList<EndImage> {
        return images.map { EndImage(it) }.toMutableList()
    }
}
