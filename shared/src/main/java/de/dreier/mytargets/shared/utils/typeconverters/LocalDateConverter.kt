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

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

public final class LocalDateConverter extends TypeConverter<String, LocalDate> {

    @Nullable
    @Override
    public String getDBValue(@Nullable LocalDate model) {
        if (model != null) {
            return model.format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        return null;
    }

    @Nullable
    @Override
    public LocalDate getModelValue(@Nullable String data) {
        if (data != null) {
            return LocalDate.parse(data);
        }
        return null;
    }

}
