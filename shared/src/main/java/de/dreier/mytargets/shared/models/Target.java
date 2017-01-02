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

package de.dreier.mytargets.shared.models;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import org.parceler.Parcel;

import java.util.List;
import java.util.Set;

import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.targets.TargetFactory;
import de.dreier.mytargets.shared.targets.drawable.TargetDrawable;
import de.dreier.mytargets.shared.targets.drawable.TargetImpactAggregationDrawable;
import de.dreier.mytargets.shared.targets.models.TargetModelBase;
import de.dreier.mytargets.shared.targets.scoringstyle.ScoringStyle;

@Parcel
public class Target implements IIdProvider, IImageProvider, IDetailProvider, Comparable<Target> {
    public int id;
    public int scoringStyle;
    public Dimension size;
    private transient TargetModelBase model;
    private transient TargetDrawable drawable;
    private transient TargetImpactAggregationDrawable targetImpactAggregationDrawable;

    public Target() {
    }

    public Target(int target, int scoringStyle) {
        this(target, scoringStyle, null);
        this.size = getModel().getDiameters()[0];
    }

    public Target(int target, int scoringStyle, Dimension diameter) {
        this.id = target;
        this.model = TargetFactory.getTarget(target);
        this.scoringStyle = scoringStyle;
        this.size = diameter;
    }

    public Long getId() {
        return (long) id;
    }

    public TargetDrawable getDrawable() {
        if (drawable == null) {
            drawable = new TargetDrawable(this);
        }
        return drawable;
    }

    public TargetImpactAggregationDrawable getImpactAggregationDrawable() {
        if (targetImpactAggregationDrawable == null) {
            targetImpactAggregationDrawable = new TargetImpactAggregationDrawable(this);
        }
        return targetImpactAggregationDrawable;
    }

    public String zoneToString(int zone, int arrow) {
        return getScoringStyle().zoneToString(zone, arrow);
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Target &&
                getClass().equals(another.getClass()) &&
                id == ((Target) another).id;
    }

    public TargetModelBase getModel() {
        if (model == null) {
            model = TargetFactory.getTarget(id);
        }
        return model;
    }

    public int getScoreByZone(int zone, int arrow) {
        return getScoringStyle().getScoreByScoringRing(zone, arrow);
    }

    @Override
    public Drawable getDrawable(Context context) {
        return getDrawable();
    }

    @Override
    public String getName() {
        return String.format("%s (%s)", toString(), size.toString());
    }

    @Override
    public String getDetails(Context context) {
        return getModel().getScoringStyles().get(scoringStyle);
    }

    public List<SelectableZone> getSelectableZoneList(int arrow) {
        return getModel().getSelectableZoneList(scoringStyle, arrow);
    }

    private ScoringStyle getScoringStyle() {
        return getModel().getScoringStyle(scoringStyle);
    }

    public Score getReachedScore(End end) {
        return getScoringStyle().getReachedScore(end);
    }

    @Override
    public String toString() {
        return getModel().toString();
    }

    public Set<SelectableZone> getAllPossibleSelectableZones() {
        return getModel().getAllPossibleSelectableZones(scoringStyle);
    }
    
    @Override
    public int compareTo(@NonNull Target target) {
        return id - target.id;
    }
}