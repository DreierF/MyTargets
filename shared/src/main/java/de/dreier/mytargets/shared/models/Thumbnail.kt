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

package de.dreier.mytargets.shared.models

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.ThumbnailUtils
import android.os.Parcelable
import android.support.annotation.DrawableRes
import com.raizlabs.android.dbflow.data.Blob
import de.dreier.mytargets.shared.utils.BitmapUtils
import de.dreier.mytargets.shared.utils.RoundedAvatarDrawable
import kotlinx.android.parcel.Parcelize
import java.io.File

@SuppressLint("ParcelCreator")
@Parcelize
class Thumbnail(internal var data: ByteArray) : Parcelable {

    @Suppress("PLUGIN_WARNING")
    val roundDrawable: Drawable by lazy {
        val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
        RoundedAvatarDrawable(bitmap)
    }

    val blob: Blob
        get() = Blob(data)


    constructor() : this(ByteArray(0))

    constructor(data: Blob) : this(data.blob)

    companion object {
        /**
         * Constant used to indicate the dimension of micro thumbnail.
         */
        private val TARGET_SIZE_MICRO_THUMBNAIL = 96

        fun from(bitmap: Bitmap): Thumbnail {
            val thumbnail = ThumbnailUtils.extractThumbnail(bitmap,
                    TARGET_SIZE_MICRO_THUMBNAIL,
                    TARGET_SIZE_MICRO_THUMBNAIL,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT)
            return Thumbnail(BitmapUtils.getBitmapAsByteArray(thumbnail))
        }

        fun from(imageFile: File): Thumbnail {
            val thumbnail = ThumbnailUtils
                    .extractThumbnail(BitmapFactory.decodeFile(imageFile.path),
                            TARGET_SIZE_MICRO_THUMBNAIL, TARGET_SIZE_MICRO_THUMBNAIL) ?:
                    Bitmap.createBitmap(TARGET_SIZE_MICRO_THUMBNAIL, TARGET_SIZE_MICRO_THUMBNAIL, Bitmap.Config.RGB_565)
            return Thumbnail(BitmapUtils.getBitmapAsByteArray(thumbnail))
        }

        fun from(context: Context, @DrawableRes resId: Int) :Thumbnail {
            return from(BitmapFactory.decodeResource(context.resources, resId))
        }
    }
}
