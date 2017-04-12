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

import com.raizlabs.android.dbflow.converter.TypeConverter;

import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;

public final class LocalTimeConverter extends TypeConverter<String, LocalTime> {

    @Override
    public String getDBValue(LocalTime model) {
        if (model != null) {
            return model.format(DateTimeFormatter.ISO_LOCAL_TIME);
        }
        return null;
    }

    @Override
    public LocalTime getModelValue(String data) {
        if (data != null) {
            return LocalTime.parse(data);
        }
        return null;
    }

}
