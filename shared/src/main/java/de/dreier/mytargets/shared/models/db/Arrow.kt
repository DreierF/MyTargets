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
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import de.dreier.mytargets.shared.AppDatabase
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.IIdSettable
import de.dreier.mytargets.shared.models.IImageProvider
import de.dreier.mytargets.shared.models.Thumbnail
import de.dreier.mytargets.shared.utils.typeconverters.DimensionConverter
import de.dreier.mytargets.shared.utils.typeconverters.ThumbnailConverter
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
@Table(database = AppDatabase::class)
data class Arrow(
        @Column(name = "_id")
        @PrimaryKey(autoincrement = true)
        override var id: Long = 0,

        @Column
        override var name: String = "",

        @Column
        var maxArrowNumber: Int = 12,

        @Column
        var length: String? = "",

        @Column
        var material: String? = "",

        @Column
        var spine: String? = "",

        @Column
        var weight: String? = "",

        @Column
        var tipWeight: String? = "",

        @Column
        var vanes: String? = "",

        @Column
        var nock: String? = "",

        @Column
        var comment: String? = "",

        @Column(typeConverter = DimensionConverter::class)
        var diameter: Dimension = Dimension(5f, Dimension.Unit.MILLIMETER),

        @Column(typeConverter = ThumbnailConverter::class)
        var thumbnail: Thumbnail? = null) : IImageProvider, IIdSettable, Parcelable {

    val drawable: Drawable
        get() = thumbnail!!.roundDrawable

    override fun getDrawable(context: Context): Drawable {
        return drawable
    }
}
