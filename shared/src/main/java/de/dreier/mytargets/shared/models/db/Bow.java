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

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
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
import de.dreier.mytargets.shared.models.EBowType;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.models.IImageProvider;
import de.dreier.mytargets.shared.models.IRecursiveModel;
import de.dreier.mytargets.shared.models.Thumbnail;
import de.dreier.mytargets.shared.utils.typeconverters.EBowTypeConverter;
import de.dreier.mytargets.shared.utils.typeconverters.ThumbnailConverter;

@Parcel
@Table(database = AppDatabase.class)
public class Bow extends BaseModel implements IImageProvider, IIdSettable, Comparable<Bow>, IRecursiveModel {

    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    public Long id = -1L;

    @Column
    public String name = "";

    @Column(typeConverter = EBowTypeConverter.class)
    public EBowType type = EBowType.RECURVE_BOW;

    @Column
    public String brand = "";

    @Column
    public String size = "";

    @Column
    public String braceHeight = "";

    @Column
    public String tiller = "";

    @Column
    public String limbs = "";

    @Column
    public String sight = "";

    @Column
    public String drawWeight = "";

    @Column
    public String stabilizer = "";

    @Column
    public String clicker = "";

    @Column
    public String button = "";

    @Column
    public String string = "";

    @Column
    public String nockingPoint = "";

    @Column
    public String letoffWeight = "";

    @Column
    public String arrowRest = "";

    @Column
    public String restHorizontalPosition = "";

    @Column
    public String restVerticalPosition = "";

    @Column
    public String restStiffness = "";

    @Column
    public String camSetting = "";

    @Column
    public String scopeMagnification = "";

    @Column
    public String description = "";

    @Column(typeConverter = ThumbnailConverter.class)
    public Thumbnail thumbnail;

    public List<BowImage> images;

    public List<SightMark> sightMarks;

    public static List<Bow> getAll() {
        return SQLite.select().from(Bow.class).queryList();
    }

    public static Bow get(Long id) {
        return SQLite.select()
                .from(Bow.class)
                .where(Bow_Table._id.eq(id))
                .querySingle();
    }

    @OneToMany(methods = {}, variableName = "sightMarks")
    public List<SightMark> getSightMarks() {
        if (sightMarks == null) {
            sightMarks = Stream.of(SQLite.select()
                    .from(SightMark.class)
                    .where(SightMark_Table.bow.eq(id))
                    .queryList())
                    .sortBy(sightMark -> sightMark.distance)
                    .collect(Collectors.toList());
        }
        return sightMarks;
    }

    @OneToMany(methods = {}, variableName = "images")
    public List<BowImage> getImages() {
        if (images == null) {
            images = SQLite.select()
                    .from(BowImage.class)
                    .where(BowImage_Table.bow.eq(id))
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
        return another instanceof Bow &&
                getClass().equals(another.getClass()) &&
                id.equals(((Bow) another).id);
    }

    public SightMark getSightSetting(Dimension distance) {
        return Stream.of(getSightMarks())
                .filter(s -> s.distance.equals(distance))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void save() {
        super.save();
        if (images != null) {
            SQLite.delete(BowImage.class)
                    .where(BowImage_Table.bow.eq(id))
                    .execute();
            // TODO Replace this super ugly workaround by stubbed Relationship in version 4 of dbFlow
            for (BowImage image : images) {
                image.bowId = id;
                image.save();
            }
        }
        if (sightMarks != null) {
            SQLite.delete(SightMark.class)
                    .where(SightMark_Table.bow.eq(id))
                    .execute();
            // TODO Replace this super ugly workaround by stubbed Relationship in version 4 of dbFlow
            for (SightMark sightMark : sightMarks) {
                sightMark.bowId = id;
                sightMark.save();
            }
        }
    }

    @Override
    public void delete() {
        getSightMarks().forEach(BaseModel::delete);
        getImages().forEach(BaseModel::delete);
        super.delete();
    }

    @Override
    public int compareTo(@NonNull Bow another) {
        final int result = getName().compareTo(another.getName());
        return result == 0 ? (int) (id - another.id) : result;
    }

    public boolean areAllPropertiesSet() {
        return !TextUtils.isEmpty(size) &&
                !TextUtils.isEmpty(drawWeight) &&
                (!type.showLetoffWeight() || !TextUtils.isEmpty(letoffWeight)) &&
                (!type.showArrowRest() || !TextUtils.isEmpty(arrowRest)) &&
                (!type.showArrowRest() || !TextUtils.isEmpty(restVerticalPosition)) &&
                (!type.showArrowRest() || !TextUtils.isEmpty(restHorizontalPosition)) &&
                (!type.showArrowRest() || !TextUtils.isEmpty(restStiffness)) &&
                (!type.showCamSetting() || !TextUtils.isEmpty(camSetting)) &&
                (!type.showTiller() || !TextUtils.isEmpty(tiller)) &&
                (!type.showBraceHeight() || !TextUtils.isEmpty(braceHeight)) &&
                (!type.showLimbs() || !TextUtils.isEmpty(limbs)) &&
                (!type.showSight() || !TextUtils.isEmpty(sight)) &&
                (!type.showScopeMagnification() || !TextUtils.isEmpty(scopeMagnification)) &&
                (!type.showStabilizer() || !TextUtils.isEmpty(stabilizer)) &&
                (!type.showClicker() || !TextUtils.isEmpty(clicker)) &&
                (!type.showNockingPoint() || !TextUtils.isEmpty(nockingPoint)) &&
                !TextUtils.isEmpty(string) &&
                (!type.showButton() || !TextUtils.isEmpty(button)) &&
                !TextUtils.isEmpty(description);
    }

    @Override
    public void saveRecursively() {
        save();
    }
}
