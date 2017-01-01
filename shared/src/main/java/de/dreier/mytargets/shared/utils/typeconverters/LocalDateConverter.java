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

import org.joda.time.LocalDate;

public final class LocalDateConverter extends TypeConverter<Long, LocalDate> {

    @Override
    public Long getDBValue(LocalDate model) {
        if (model != null) {
            return model.toDate().getTime();
        }

        return null;
    }

    @Override
    public LocalDate getModelValue(Long data) {
        if (data != null) {
            return new LocalDate(data);
        }

        return null;
    }

}