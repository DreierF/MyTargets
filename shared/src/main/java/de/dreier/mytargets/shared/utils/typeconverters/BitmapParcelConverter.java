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
import android.os.Parcel;

import org.parceler.ParcelConverter;

import de.dreier.mytargets.shared.utils.BitmapUtils;

public class BitmapParcelConverter implements ParcelConverter<Bitmap> {
    @Override
    public void toParcel(Bitmap input, Parcel parcel) {
        if (input == null) {
            parcel.writeInt((byte) 0);
        } else {
            byte[] byteArray = BitmapUtils.getBitmapAsByteArray(input);
            parcel.writeInt((byte) byteArray.length);
            parcel.writeByteArray(byteArray);
        }
    }

    @Override
    public Bitmap fromParcel(Parcel parcel) {
        int size = parcel.readInt();
        if (size == 0) {
            return null;
        }
        byte[] byteArray = new byte[size];
        parcel.readByteArray(byteArray);
        return BitmapFactory.decodeByteArray(byteArray, 0, size);
    }
}
