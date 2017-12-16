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

import de.dreier.mytargets.shared.models.EBowType;

public final class EBowTypeConverter extends TypeConverter<Integer, EBowType> {

    @Nullable
    @Override
    public Integer getDBValue(@Nullable EBowType model) {
        if (model != null) {
            return model.getId();
        }

        return null;
    }

    @Nullable
    @Override
    public EBowType getModelValue(@Nullable Integer data) {
        if (data != null) {
            return EBowType.fromId(data);
        }

        return null;
    }

}
