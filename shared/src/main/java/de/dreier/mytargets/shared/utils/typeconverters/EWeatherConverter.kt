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

package de.dreier.mytargets.shared.utils.typeconverters;

import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import de.dreier.mytargets.shared.models.EWeather;

public final class EWeatherConverter extends TypeConverter<Integer, EWeather> {

    @Nullable
    @Override
    public Integer getDBValue(@Nullable EWeather model) {
        if (model != null) {
            return model.getValue();
        }

        return null;
    }

    @Nullable
    @Override
    public EWeather getModelValue(@Nullable Integer data) {
        if (data != null) {
            return EWeather.getOfValue(data);
        }

        return null;
    }

}
