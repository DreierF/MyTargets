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
import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.utils.typeconverters.DimensionConverter;

@Parcel
@Table(database = AppDatabase.class)
public class RoundTemplate extends BaseModel implements IIdSettable {

    @Nullable
    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    Long id;

    @Nullable
    @ForeignKey(tableClass = StandardRound.class, references = {
            @ForeignKeyReference(columnName = "standardRound", columnType = Long.class, foreignKeyColumnName = "_id")},
            onDelete = ForeignKeyAction.CASCADE)
    public Long standardRound;

    @Column
    public int index;

    @Column
    public int shotsPerEnd;

    @Column
    public int endCount;

    @Nullable
    @Column(typeConverter = DimensionConverter.class)
    public Dimension distance;

    @Column
    int targetId;

    @Column
    int targetScoringStyle;

    @Nullable
    @Column(typeConverter = DimensionConverter.class)
    Dimension targetDiameter;

    @Nullable
    public static RoundTemplate get(long sid, int index) {
        return SQLite.select()
                .from(RoundTemplate.class)
                .where(RoundTemplate_Table.standardRound.eq(sid))
                .and(RoundTemplate_Table.index.eq(index))
                .querySingle();
    }

    @Nullable
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof RoundTemplate &&
                getClass().equals(another.getClass()) &&
                id.equals(((RoundTemplate) another).id);
    }

    @Nullable
    public Target getTargetTemplate() {
        return new Target(targetId, targetScoringStyle, targetDiameter);
    }

    public void setTargetTemplate(@NonNull Target targetTemplate) {
        targetId = targetTemplate.id;
        targetScoringStyle = targetTemplate.scoringStyle;
        targetDiameter = targetTemplate.size;
    }
}
