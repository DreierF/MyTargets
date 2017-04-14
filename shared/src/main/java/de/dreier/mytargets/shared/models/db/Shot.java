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

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.IIdSettable;

@Parcel
@Table(database = AppDatabase.class)
public class Shot extends BaseModel implements IIdSettable, Comparable<Shot> {
    public static final int NOTHING_SELECTED = -2;
    public static final int MISS = -1;

    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    Long id;

    // The index of the shot in the containing end
    @Column
    public int index;

    @ForeignKey(tableClass = End.class, references = {
            @ForeignKeyReference(columnName = "end", columnType = Long.class, foreignKeyColumnName = "_id", referencedGetterName = "getId", referencedSetterName = "setId")},
            onDelete = ForeignKeyAction.CASCADE)
    public Long endId;

    @Column
    public float x;

    @Column
    public float y;

    @Column
    public int scoringRing = NOTHING_SELECTED;

    // Is the actual number of the arrow not its index, arrow id or something else
    @Column
    public String arrowNumber = null;

    public Shot() {
    }

    public Shot(int i) {
        this.index = i;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int compareTo(@NonNull Shot another) {
        if (another.scoringRing == scoringRing) {
            return 0;
        } else if (another.scoringRing >= 0 && scoringRing >= 0) {
            return scoringRing - another.scoringRing;
        } else {
            return another.scoringRing - scoringRing;
        }
    }

    @Override
    public boolean equals(Object o) {
        return this == o || !(o == null || getClass() != o.getClass())
                && (id != null ? id.equals(((Shot) o).id) : ((Shot) o).id == null);
    }
}
