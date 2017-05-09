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

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.IDetailProvider;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.models.IImageProvider;
import de.dreier.mytargets.shared.targets.drawable.TargetDrawable;
import de.dreier.mytargets.shared.targets.models.CombinedSpot;

@Parcel
@Table(database = AppDatabase.class)
public class StandardRound extends BaseModel implements IIdSettable, IImageProvider, IDetailProvider, Comparable<StandardRound> {

    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    Long id;

    @Column
    public int club;

    @Column
    public String name;

    List<RoundTemplate> rounds;

    public static StandardRound get(Long id) {
        return SQLite.select()
                .from(StandardRound.class)
                .where(StandardRound_Table._id.eq(id))
                .querySingle();
    }

    public static List<StandardRound> getAll() {
        return SQLite.select().from(StandardRound.class).queryList();
    }

    public static List<StandardRound> getAllSearch(String query) {
        query = "%" + query.replace(' ', '%') + "%";
        return SQLite.select()
                .from(StandardRound.class)
                .where(StandardRound_Table.name.like(query))
                .and(StandardRound_Table.club.notEq(512))
                .queryList();
    }

    public void insert(RoundTemplate template) {
        template.index = getRounds().size();
        template.standardRound = id;
        getRounds().add(template);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        if (name != null) {
            return name;
        }
        return "";
    }

    public String getDescription(Context context) {
        String desc = "";
        for (RoundTemplate r : getRounds()) {
            if (!desc.isEmpty()) {
                desc += "\n";
            }
            desc += context.getString(R.string.round_desc, r.distance, r.endCount,
                    r.shotsPerEnd, r.getTargetTemplate().size);
        }
        return desc;
    }

    @OneToMany(methods = {}, variableName = "rounds")
    public List<RoundTemplate> getRounds() {
        if (rounds == null) {
            rounds = SQLite.select()
                    .from(RoundTemplate.class)
                    .where(RoundTemplate_Table.standardRound.eq(id))
                    .queryList();
        }
        return rounds;
    }

    public void setRounds(List<RoundTemplate> rounds) {
        this.rounds = rounds;
    }

    @Override
    public Drawable getDrawable(Context context) {
        return getTargetDrawable();
    }

    public Drawable getTargetDrawable() {
        List<TargetDrawable> targets = new ArrayList<>();
        for (RoundTemplate r : getRounds()) {
            targets.add(r.getTargetTemplate().getDrawable());
        }
        return new CombinedSpot(targets);
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof StandardRound &&
                getClass().equals(another.getClass()) &&
                id.equals(((StandardRound) another).id);
    }

    @Override
    public String getDetails(Context context) {
        return getDescription(context);
    }

    @Override
    public int compareTo(@NonNull StandardRound another) {
        final int result = getName().compareTo(another.getName());
        return result == 0 ? (int) (id - another.id) : result;
    }

    @Override
    public void save() {
        FlowManager.getDatabase(AppDatabase.class).executeTransaction(this::save);
    }

    @Override
    public void save(DatabaseWrapper databaseWrapper) {
        super.save(databaseWrapper);
        if (rounds != null) {
            SQLite.delete(RoundTemplate.class)
                    .where(RoundTemplate_Table.standardRound.eq(id))
                    .execute(databaseWrapper);
            // TODO Replace this super ugly workaround by stubbed Relationship in version 4 of dbFlow
            for (RoundTemplate s : rounds) {
                s.standardRound = id;
                s.save(databaseWrapper);
            }
        }
    }

    @Override
    public void delete() {
        FlowManager.getDatabase(AppDatabase.class).executeTransaction(this::delete);
    }

    @Override
    public void delete(DatabaseWrapper databaseWrapper) {
        for (RoundTemplate roundTemplate : getRounds()) {
            roundTemplate.delete(databaseWrapper);
        }
        super.delete(databaseWrapper);
    }
}
