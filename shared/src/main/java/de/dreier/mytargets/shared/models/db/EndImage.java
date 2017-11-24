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
public class EndImage extends BaseModel implements Image {

    @Nullable
    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    public Long _id = -1L;

    @Nullable
    @Column
    public String fileName = "";

    @Nullable
    @ForeignKey(tableClass = End.class, references = {
            @ForeignKeyReference(columnName = "end", columnType = Long.class, foreignKeyColumnName = "_id", referencedGetterName = "getId", referencedSetterName = "setId")},
            onDelete = ForeignKeyAction.CASCADE)
    public Long endId;

    public EndImage() {
    }

    public EndImage(String imageFile) {
        fileName = imageFile;
    }

    /**
     * @return The name of the image file, which is placed inside the files directory of the app
     */
    @Nullable
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName The name of the image file, which is placed inside the files directory of the app
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
