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

package de.dreier.mytargets.base.db.typeconverters

import androidx.room.TypeConverter
import de.dreier.mytargets.shared.models.Thumbnail

class ThumbnailConverters {

    @TypeConverter
    fun getDBValue(model: Thumbnail?): ByteArray? {
        return model?.data
    }

    @TypeConverter
    fun getModelValue(data: ByteArray?): Thumbnail? {
        return if (data != null) Thumbnail(data) else null
    }

}
