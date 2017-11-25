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

import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.utils.typeconverters.DimensionConverter;

import static de.dreier.mytargets.shared.models.Dimension.Unit.METER;

@Parcel
@Table(database = AppDatabase.class)
public class SightMark extends BaseModel implements IIdSettable {

    @Nullable
    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    Long id;

    @Nullable
    @ForeignKey(tableClass = Bow.class, references = {
            @ForeignKeyReference(columnName = "bow", columnType = Long.class, foreignKeyColumnName = "_id")},
            onDelete = ForeignKeyAction.CASCADE)
    public Long bowId;

    @Nullable
    @Column(typeConverter = DimensionConverter.class)
    public Dimension distance = new Dimension(18, METER);

    @Nullable
    @Column
    public String value = "";

    @Nullable
    public Long getId() {
        return id;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof SightMark &&
                getClass().equals(another.getClass()) &&
                id.equals(((SightMark) another).id);
    }
}
