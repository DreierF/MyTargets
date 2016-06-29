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

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.utils.DimensionConverter;

import static de.dreier.mytargets.shared.models.Dimension.Unit.METER;

@Table(database = AppDatabase.class)
public class SightSetting extends BaseModel implements IIdSettable {
    @PrimaryKey(autoincrement = true)
    Long id;
    @ForeignKey(tableClass = Bow.class)
    public Long bowId;
    @Column(typeConverter = DimensionConverter.class)
    public Dimension distance = new Dimension(18, METER);
    @Column
    public String value = "";

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof SightSetting &&
                getClass().equals(another.getClass()) &&
                id == ((SightSetting) another).id;
    }
}
