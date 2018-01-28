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

package de.dreier.mytargets.shared.utils.typeconverters

import android.arch.persistence.room.TypeConverter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import de.dreier.mytargets.shared.utils.toByteArray

class BitmapConverters {

    @TypeConverter
    fun getDBValue(model: Bitmap?): ByteArray? {
        return model?.toByteArray()
    }

    @TypeConverter
    fun getModelValue(data: ByteArray?): Bitmap? {
        return if (data != null) {
            BitmapFactory.decodeByteArray(data, 0, data.size)
        } else null
    }

}
