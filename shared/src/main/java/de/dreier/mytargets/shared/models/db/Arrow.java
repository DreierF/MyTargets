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
import android.text.TextUtils;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.util.List;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.models.IImageProvider;
import de.dreier.mytargets.shared.models.IRecursiveModel;
import de.dreier.mytargets.shared.models.Thumbnail;
import de.dreier.mytargets.shared.utils.typeconverters.DimensionConverter;
import de.dreier.mytargets.shared.utils.typeconverters.ThumbnailConverter;

@Parcel
@Table(database = AppDatabase.class)
public class Arrow extends BaseModel implements IImageProvider, IIdSettable, Comparable<Arrow>, IRecursiveModel {

    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    public Long id = -1L;

    @Column
    public String name = "";

    @Column
    public String length = "";

    @Column
    public String material = "";

    @Column
    public String spine = "";

    @Column
    public String weight = "";

    @Column
    public String tipWeight = "";

    @Column
    public String vanes = "";

    @Column
    public String nock = "";

    @Column
    public String comment = "";

    @Column(typeConverter = DimensionConverter.class)
    public Dimension diameter = new Dimension(5, Dimension.Unit.MILLIMETER);

    @Column(typeConverter = ThumbnailConverter.class)
    public Thumbnail thumbnail;

    public List<ArrowImage> images = null;

    public static List<Arrow> getAll() {
        return SQLite.select().from(Arrow.class).queryList();
    }

    public static Arrow get(Long id) {
        return SQLite.select()
                .from(Arrow.class)
                .where(Arrow_Table._id.eq(id))
                .querySingle();
    }

    @OneToMany(methods = {}, variableName = "images")
    public List<ArrowImage> getImages() {
        if (images == null) {
            images = SQLite.select()
                    .from(ArrowImage.class)
                    .where(ArrowImage_Table.arrow.eq(id))
                    .queryList();
        }
        return images;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Drawable getDrawable() {
        return thumbnail.getRoundDrawable();
    }

    @Override
    public Drawable getDrawable(Context context) {
        return getDrawable();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Arrow &&
                getClass().equals(another.getClass()) &&
                id.equals(((Arrow) another).id);
    }

    @Override
    public void save() {
        super.save();
        if (images != null) {
            SQLite.delete(ArrowImage.class)
                    .where(ArrowImage_Table.arrow.eq(id))
                    .execute();
            // TODO Replace this super ugly workaround by stubbed Relationship in version 4 of dbFlow
            for (ArrowImage image : images) {
                image.arrowId = id;
                image.save();
            }
        }
    }

    @Override
    public void delete() {
        getImages().forEach(ArrowImage::delete);
        super.delete();
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
}
