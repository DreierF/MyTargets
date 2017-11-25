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

import de.dreier.mytargets.shared.AppDatabase;

@Table(database = AppDatabase.class)
public class ArrowImage extends BaseModel implements Image {

    @Nullable
    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    public Long id = -1L;

    @Nullable
    @Column
    public String fileName = "";

    @Nullable
    @ForeignKey(tableClass = Arrow.class, references = {
            @ForeignKeyReference(columnName = "arrow", columnType = Long.class, foreignKeyColumnName = "_id", referencedGetterName = "getId", referencedSetterName = "setId")},
            onDelete = ForeignKeyAction.CASCADE)
    public Long arrowId;

    public ArrowImage() {
    }

    public ArrowImage(@Nullable String imageFilePath) {
        fileName = imageFilePath;
    }

    @Nullable
    public String getFileName() {
        return fileName;
    }

    public void setFileName(@Nullable String fileName) {
        this.fileName = fileName;
    }
}
