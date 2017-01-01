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

import de.dreier.mytargets.shared.models.Dimension;

public final class DimensionConverter extends TypeConverter<String, Dimension> {

    @Override
    public String getDBValue(Dimension model) {
        if (model != null) {
            return model.value + " " + model.unit;
        }

        return null;
    }

    @Override
    public Dimension getModelValue(String data) {
        if (data != null) {
            int index = data.indexOf(' ');
            final String value = data.substring(0, index);
            final String unit = data.substring(index + 1);
            return new Dimension(Float.parseFloat(value), Dimension.Unit.from(unit));
        }

        return null;
    }

}