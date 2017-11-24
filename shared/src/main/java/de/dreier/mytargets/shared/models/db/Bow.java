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

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
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

    @Nullable
    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    public Long id = -1L;

    @Nullable
    @Column
    public String name = "";

    @Nullable
    @Column(typeConverter = EBowTypeConverter.class)
    public EBowType type = EBowType.RECURVE_BOW;

    @Nullable
    @Column
    public String brand = "";

    @Nullable
    @Column
    public String size = "";

    @Nullable
    @Column
    public String braceHeight = "";

    @Nullable
    @Column
    public String tiller = "";

    @Nullable
    @Column
    public String limbs = "";

    @Nullable
    @Column
    public String sight = "";

    @Nullable
    @Column
    public String drawWeight = "";

    @Nullable
    @Column
    public String stabilizer = "";

    @Nullable
    @Column
    public String clicker = "";

    @Nullable
    @Column
    public String button = "";

    @Nullable
    @Column
    public String string = "";

    @Nullable
    @Column
    public String nockingPoint = "";

    @Nullable
    @Column
    public String letoffWeight = "";

    @Nullable
    @Column
    public String arrowRest = "";

    @Nullable
    @Column
    public String restHorizontalPosition = "";

    @Nullable
    @Column
    public String restVerticalPosition = "";

    @Nullable
    @Column
    public String restStiffness = "";

    @Nullable
    @Column
    public String camSetting = "";

    @Nullable
    @Column
    public String scopeMagnification = "";

    @Nullable
    @Column
    public String description = "";

    @Nullable
    @Column(typeConverter = ThumbnailConverter.class)
    public Thumbnail thumbnail;

    public List<BowImage> images;

    public List<SightMark> sightMarks;

    public static List<Bow> getAll() {
        return SQLite.select().from(Bow.class).queryList();
    }

    @Nullable
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

    @Nullable
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

    @Nullable
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
        FlowManager.getDatabase(AppDatabase.class).executeTransaction(this::save);
    }

    @Override
    public void save(DatabaseWrapper databaseWrapper) {
        super.save(databaseWrapper);
        if (images != null) {
            SQLite.delete(BowImage.class)
                    .where(BowImage_Table.bow.eq(id))
                    .execute(databaseWrapper);
            // TODO Replace this super ugly workaround by stubbed Relationship in version 4 of dbFlow
            for (BowImage image : images) {
                image.bowId = id;
                image.save(databaseWrapper);
            }
        }
        if (sightMarks != null) {
            SQLite.delete(SightMark.class)
                    .where(SightMark_Table.bow.eq(id))
                    .execute(databaseWrapper);
            // TODO Replace this super ugly workaround by stubbed Relationship in version 4 of dbFlow
            for (SightMark sightMark : sightMarks) {
                sightMark.bowId = id;
                sightMark.save(databaseWrapper);
            }
        }
    }

    @Override
    public void delete() {
        FlowManager.getDatabase(AppDatabase.class).executeTransaction(this::delete);
    }

    @Override
    public void delete(DatabaseWrapper databaseWrapper) {
        for (SightMark sightMark : getSightMarks()) {
            sightMark.delete(databaseWrapper);
        }
        for (BowImage bowImage : getImages()) {
            bowImage.delete(databaseWrapper);
        }
        super.delete(databaseWrapper);
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

    @Override
    public void saveRecursively(DatabaseWrapper databaseWrapper) {
        save(databaseWrapper);
    }
}
