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

package de.dreier.mytargets.shared.models.db;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;
import org.parceler.ParcelPropertyConverter;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.utils.typeconverters.BitmapConverter;
import de.dreier.mytargets.shared.utils.typeconverters.BitmapParcelConverter;

@Parcel
@Table(database = AppDatabase.class)
public class Signature extends BaseModel {

    @Nullable
    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    public Long _id = -1L;

    @NonNull
    @Column
    public String name = "";

    @Nullable
    @Column(typeConverter = BitmapConverter.class)
    @ParcelPropertyConverter(BitmapParcelConverter.class)
    public Bitmap bitmap;

    @ParcelConstructor
    public Signature() {
    }

    public Signature(@Nullable Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    /**
     * Returns a bitmap of the signature or null if no signature has been set.
     */
    @Nullable
    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(@Nullable Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isSigned() {
        return bitmap != null;
    }

    public String getName(String defaultName) {
        return name.isEmpty() ? defaultName : name;
    }

    @Nullable
    public static Signature get(long signatureId) {
        return SQLite.select()
                .from(Signature.class)
                .where(Signature_Table._id.eq(signatureId))
                .querySingle();
    }
}
