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
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

class LocalTimeConverters {

    @TypeConverter
    fun getDBValue(model: LocalTime?): String? {
        return model?.format(DateTimeFormatter.ISO_LOCAL_TIME)
    }

    @TypeConverter
    fun getModelValue(data: String?): LocalTime? {
        return if (data != null) LocalTime.parse(data) else null
    }

}
