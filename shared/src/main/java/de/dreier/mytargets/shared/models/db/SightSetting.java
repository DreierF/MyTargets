/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.utils.DimensionConverter;

import static de.dreier.mytargets.shared.models.Dimension.Unit.METER;

@Parcel
@Table(database = AppDatabase.class, name = "VISIER")
public class SightSetting extends BaseModel implements IIdSettable {

    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    Long id;

    @ForeignKey(tableClass = Bow.class, references = {
            @ForeignKeyReference(columnName = "bow", columnType = Long.class, foreignKeyColumnName = "_id")})
    public Long bowId;

    @Column(typeConverter = DimensionConverter.class, name = "distance")
    public Dimension distance = new Dimension(18, METER);

    @Column(name = "setting")
    public String value = "";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof SightSetting &&
                getClass().equals(another.getClass()) &&
                id.equals(((SightSetting) another).id);
    }
}
