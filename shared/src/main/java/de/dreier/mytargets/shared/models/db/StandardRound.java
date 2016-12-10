/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models.db;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

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

    List<RoundTemplate> rounds = new ArrayList<>();

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
                .where(StandardRound_Table.name.like(query)).and(StandardRound_Table.club.notEq(512))
                .queryList();
    }

    public void insert(RoundTemplate template) {
        template.index = rounds.size();
        template.standardRound = id;
        rounds.add(template);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        for (RoundTemplate r : rounds) {
            r.standardRound = id;
        }
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
        for (RoundTemplate r : rounds) {
            if (!desc.isEmpty()) {
                desc += "\n";
            }
            desc += context.getString(R.string.round_desc, r.distance, r.endCount,
                    r.shotsPerEnd, r.getTargetTemplate().size);
        }
        return desc;
    }


    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "rounds")
    public List<RoundTemplate> getRounds() {
        if (rounds == null || rounds.isEmpty()) {
            rounds = SQLite.select()
                    .from(RoundTemplate.class)
                     .where(RoundTemplate_Table.standardRound.eq(id))
                    .queryList();
        }
        return rounds;
    }

    @Override
    public Drawable getDrawable(Context context) {
        return getTargetDrawable();
    }

    public Drawable getTargetDrawable() {
        List<TargetDrawable> targets = new ArrayList<>();
        for (RoundTemplate r : rounds) {
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

    public void setRounds(List<RoundTemplate> rounds) {
        this.rounds = rounds;
    }
}