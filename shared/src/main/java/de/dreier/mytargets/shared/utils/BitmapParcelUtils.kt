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

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcel

fun Parcel.writeBitmap(bitmap: Bitmap?) {
    if (bitmap == null) {
        writeInt(0.toByte().toInt())
    } else {
        val byteArray = bitmap.toByteArray()
        writeInt(byteArray.size.toByte().toInt())
        writeByteArray(byteArray)
    }
}

fun Parcel.readBitmap(): Bitmap? {
    val size = readInt()
    if (size == 0) {
        return null
    }
    val byteArray = ByteArray(size)
    readByteArray(byteArray)
    return BitmapFactory.decodeByteArray(byteArray, 0, size)
}
