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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import org.parceler.Parcel;

import java.util.List;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.models.IImageProvider;
import de.dreier.mytargets.shared.models.IRecursiveModel;
import de.dreier.mytargets.shared.models.Thumbnail;
import de.dreier.mytargets.shared.utils.SharedUtils;
import de.dreier.mytargets.shared.utils.typeconverters.DimensionConverter;
import de.dreier.mytargets.shared.utils.typeconverters.ThumbnailConverter;

@Parcel
@Table(database = AppDatabase.class)
public class Arrow extends BaseModel implements IImageProvider, IIdSettable, Comparable<Arrow>, IRecursiveModel {

    @Nullable
    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    Long id = null;

    @NonNull
    @Column
    public String name = "";

    @Column
    public int maxArrowNumber = 12;

    @Nullable
    @Column
    public String length = "";

    @Nullable
    @Column
    public String material = "";

    @Nullable
    @Column
    public String spine = "";

    @Nullable
    @Column
    public String weight = "";

    @Nullable
    @Column
    public String tipWeight = "";

    @Nullable
    @Column
    public String vanes = "";

    @Nullable
    @Column
    public String nock = "";

    @Nullable
    @Column
    public String comment = "";

    @NonNull
    @Column(typeConverter = DimensionConverter.class)
    public Dimension diameter = new Dimension(5, Dimension.Unit.MILLIMETER);

    @Nullable
    @Column(typeConverter = ThumbnailConverter.class)
    public Thumbnail thumbnail;

    @Nullable
    public List<ArrowImage> images = null;

    public static List<Arrow> getAll() {
        return SQLite.select().from(Arrow.class).queryList();
    }

    @Nullable
    public static Arrow get(Long id) {
        return SQLite.select()
                .from(Arrow.class)
                .where(Arrow_Table._id.eq(id))
                .querySingle();
    }

    @Nullable
    @OneToMany(methods = {}, variableName = "images")
    public List<ArrowImage> loadImages() {
        if (images == null) {
            images = SQLite.select()
                    .from(ArrowImage.class)
                    .where(ArrowImage_Table.arrow.eq(id))
                    .queryList();
        }
        return images;
    }

    @Nullable
    public Long getId() {
        return id;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
    }

    public Drawable getDrawable() {
        return thumbnail.getRoundDrawable();
    }

    @Override
    public Drawable getDrawable(Context context) {
        return getDrawable();
    }

    @NonNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Arrow &&
                getClass().equals(another.getClass()) &&
                SharedUtils.equals(id, ((Arrow) another).id);
    }

    @Override
    public void save() {
        FlowManager.getDatabase(AppDatabase.class).executeTransaction(this::save);
    }

    @Override
    public void save(DatabaseWrapper databaseWrapper) {
        super.save(databaseWrapper);
        if (images != null) {
            SQLite.delete(ArrowImage.class)
                    .where(ArrowImage_Table.arrow.eq(id))
                    .execute(databaseWrapper);
            // TODO Replace this super ugly workaround by stubbed Relationship in version 4 of dbFlow
            for (ArrowImage image : images) {
                image.arrowId = id;
                image.save(databaseWrapper);
            }
        }
    }

    @Override
    public void delete() {
        FlowManager.getDatabase(AppDatabase.class).executeTransaction(this::delete);
    }

    @Override
    public void delete(DatabaseWrapper databaseWrapper) {
        for (ArrowImage arrowImage : loadImages()) {
            arrowImage.delete(databaseWrapper);
        }
        super.delete(databaseWrapper);
    }

    @Override
    public int compareTo(@NonNull Arrow another) {
        final int result = getName().compareTo(another.getName());
        return result == 0 ? (int) (id - another.id) : result;
    }

    public boolean areAllPropertiesSet() {
        return !TextUtils.isEmpty(length) &&
                !TextUtils.isEmpty(material) &&
                !TextUtils.isEmpty(spine) &&
                !TextUtils.isEmpty(weight) &&
                !TextUtils.isEmpty(tipWeight) &&
                !TextUtils.isEmpty(vanes) &&
                !TextUtils.isEmpty(nock) &&
                !TextUtils.isEmpty(comment);
    }

    @Override
    public void saveRecursively() {
        save();
    }

    @Override
    public void saveRecursively(DatabaseWrapper databaseWrapper) {
        save(databaseWrapper);
    }
}
