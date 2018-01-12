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
import de.dreier.mytargets.shared.utils.RoundedAvatarDrawable
import de.dreier.mytargets.shared.utils.toByteArray
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import java.io.File
import java.util.*

@SuppressLint("ParcelCreator")
@Parcelize
class Thumbnail(val data: ByteArray) : Parcelable {

    @Suppress("PLUGIN_WARNING")
    val roundDrawable: Drawable by lazy {
        val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
        if (bitmap == null) {
            Timber.w("Invalid bitmap data provided: %s", Arrays.asList(data).toString())
            val dummyBitmap = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888)
            return@lazy RoundedAvatarDrawable(dummyBitmap)
        }
        RoundedAvatarDrawable(bitmap)
    }

    companion object {
        /**
         * Constant used to indicate the dimension of micro thumbnail.
         */
        private const val TARGET_SIZE_MICRO_THUMBNAIL = 96

        fun from(bitmap: Bitmap): Thumbnail {
            val thumbnail = ThumbnailUtils.extractThumbnail(bitmap,
                    TARGET_SIZE_MICRO_THUMBNAIL,
                    TARGET_SIZE_MICRO_THUMBNAIL,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT)
            return Thumbnail(thumbnail.toByteArray())
        }

        fun from(imageFile: File): Thumbnail {
            val thumbnail = ThumbnailUtils
                    .extractThumbnail(BitmapFactory.decodeFile(imageFile.path),
                            TARGET_SIZE_MICRO_THUMBNAIL, TARGET_SIZE_MICRO_THUMBNAIL)
                    ?: Bitmap.createBitmap(TARGET_SIZE_MICRO_THUMBNAIL, TARGET_SIZE_MICRO_THUMBNAIL, Bitmap.Config.RGB_565)
            return Thumbnail(thumbnail.toByteArray())
        }

        fun from(context: Context, @DrawableRes resId: Int): Thumbnail {
            return from(BitmapFactory.decodeResource(context.resources, resId))
        }
    }
}
