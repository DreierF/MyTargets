/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.targets.CombinedSpot;
import de.dreier.mytargets.shared.targets.TargetDrawable;

@Table(database = AppDatabase.class)
public class StandardRound extends BaseModel implements IIdSettable, IImageProvider, IDetailProvider {

    @PrimaryKey(autoincrement = true)
    Long id;
    @Column
    public int club;
    @Column
    public String name;
    @Column
    public boolean indoor;

    public List<RoundTemplate> rounds = new ArrayList<>();

    @Column
    public int usages;

    public void insert(RoundTemplate template) {
        template.index = rounds.size();
        template.standardRound = id;
        rounds.add(template);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
            desc += context.getString(R.string.round_desc, r.distance, r.passes,
                    r.arrowsPerPasse, r.target.size);
        }
        return desc;
    }

    @Override
    public Drawable getDrawable(Context context) {
        return getTargetDrawable();
    }

    public Drawable getTargetDrawable() {
        List<TargetDrawable> targets = new ArrayList<>();
        for(RoundTemplate r: rounds) {
            targets.add(r.target.getDrawable());
        }
        return new CombinedSpot(targets);
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof StandardRound &&
                getClass().equals(another.getClass()) &&
                id == ((StandardRound) another).id;
    }

    @Override
    public String getDetails(Context context) {
        return getDescription(context);
    }
}
