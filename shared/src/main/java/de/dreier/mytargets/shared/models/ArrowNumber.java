/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import de.dreier.mytargets.shared.AppDatabase;

@Parcel
@Table(database = AppDatabase.class)
public class ArrowNumber extends BaseModel implements IIdSettable {

    @PrimaryKey(autoincrement = true)
    Long id;

    @ForeignKey(tableClass = Arrow.class)
    public Long arrowId;

    @Column
    public String number;

    @Override
    public String toString() {
        return String.valueOf(number);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof ArrowNumber &&
                getClass().equals(another.getClass()) &&
                id == ((ArrowNumber) another).id;
    }
}
