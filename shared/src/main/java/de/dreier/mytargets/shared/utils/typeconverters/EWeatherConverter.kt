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

import com.raizlabs.android.dbflow.converter.TypeConverter

import de.dreier.mytargets.shared.models.EWeather

class EWeatherConverter : TypeConverter<Int, EWeather>() {

    override fun getDBValue(model: EWeather?): Int? {
        return model?.ordinal //TODO migrate to save name instead of ordinal
    }

    override fun getModelValue(data: Int?): EWeather? {
        return if (data != null) EWeather.getOfValue(data) else null
    }

}
