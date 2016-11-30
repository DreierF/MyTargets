/*
 * Copyright (C) 2016 Florian Dreier
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
package de.dreier.mytargets.shared.models;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.targets.models.CombinedSpot;
import de.dreier.mytargets.shared.targets.drawable.TargetDrawable;

public class StandardRound implements IIdSettable, IImageProvider, IDetailProvider, Comparable<StandardRound> {

    public long id;
    public int club;
    public String name;
    public boolean indoor;
    public List<RoundTemplate> rounds = new ArrayList<>();
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
            desc += context.getString(R.string.round_desc, r.distance, r.endCount,
                    r.arrowsPerEnd, r.target.size);
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

    @Override
    public int compareTo(@NonNull StandardRound another) {
        final int result = getName().compareTo(another.getName());
        return result == 0 ? (int) (id - another.id) : result;
    }
}
