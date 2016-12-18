/*
 * Copyright (C) 2016 Florian Dreier
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
import com.raizlabs.android.dbflow.data.Blob;

import de.dreier.mytargets.shared.models.Thumbnail;

public final class ThumbnailConverter extends TypeConverter<Blob, Thumbnail> {

    @Override
    public Blob getDBValue(Thumbnail model) {
        if (model != null) {
            return model.getBlob();
        }

        return null;
    }

    @Override
    public Thumbnail getModelValue(Blob data) {
        if (data != null) {
            return new Thumbnail(data);
        }

        return null;
    }

}