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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.converter.TypeConverter;
import com.raizlabs.android.dbflow.data.Blob;

import de.dreier.mytargets.shared.utils.BitmapUtils;

public final class BitmapConverter extends TypeConverter<Blob, Bitmap> {

    @Nullable
    @Override
    public Blob getDBValue(@Nullable Bitmap model) {
        if (model != null) {
            return new Blob(BitmapUtils.getBitmapAsByteArray(model));
        }

        return null;
    }

    @Nullable
    @Override
    public Bitmap getModelValue(@Nullable Blob data) {
        if (data != null) {
            return BitmapFactory.decodeByteArray(data.getBlob(), 0, data.getBlob().length);
        }

        return null;
    }

}
