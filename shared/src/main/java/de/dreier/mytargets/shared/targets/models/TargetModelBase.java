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

package de.dreier.mytargets.shared.targets.models;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.SharedApplicationInstance;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.ETargetType;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.shared.models.SelectableZone;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.streamwrapper.Stream;
import de.dreier.mytargets.shared.targets.decoration.TargetDecorator;
import de.dreier.mytargets.shared.targets.scoringstyle.ScoringStyle;
import de.dreier.mytargets.shared.targets.zone.CircularZone;
import de.dreier.mytargets.shared.targets.zone.ZoneBase;
import de.dreier.mytargets.shared.utils.Color;

import static de.dreier.mytargets.shared.utils.Color.BLACK;

public class TargetModelBase implements IIdProvider {
    private long id;
    private final int nameRes;
    public float faceRadius;
    public PointF[] facePositions;
    protected ZoneBase[] zones;
    @NonNull
    protected Dimension[] diameters = new Dimension[0];
    protected ScoringStyle[] scoringStyles;
    protected TargetDecorator decorator;
    @NonNull
    protected ETargetType type = ETargetType.TARGET;

    /**
     * Factor that needs to be applied to the target's diameter to get the real target size.
     * e.g. 5 Ring 40cm is half the size of a full 40cm, therefore the target is 20cm in reality,
     * hence the factor is 0.5f.
     */
    protected float realSizeFactor = 1f;

    protected TargetModelBase(long id, @StringRes int nameRes) {
        this.id = id;
        this.nameRes = nameRes;
        this.faceRadius = 1f;
        this.facePositions = new PointF[]{new PointF(0f, 0f)};
    }

    @Override
    public Long getId() {
        return id;
    }

    @NonNull
    public ETargetType getType() {
        return type;
    }

    @NonNull
    @Override
    public String toString() {
        return SharedApplicationInstance.Companion.getStr(nameRes);
    }

    public ZoneBase getZone(int zone) {
        if (isOutOfRange(zone)) {
            return new CircularZone(0f, BLACK, BLACK, 0);
        }
        return zones[zone];
    }

    @NonNull
    public Dimension[] getDiameters() {
        return diameters;
    }

    @NonNull
    public Dimension getRealSize(@NonNull Dimension diameter) {
        return new Dimension(realSizeFactor * diameter.getValue(), diameter.getUnit());
    }

    public int getZoneCount() {
        return zones.length;
    }

    public List<String> getScoringStyles() {
        return Stream.of(scoringStyles)
                .map(ScoringStyle::toString)
                .toList();
    }

    public ScoringStyle getScoringStyle(int scoringStyle) {
        return scoringStyles[scoringStyle];
    }

    public boolean dependsOnArrowIndex() {
        return false;
    }

    public int getFaceCount() {
        return facePositions.length;
    }

    public TargetDecorator getDecorator() {
        return decorator;
    }

    public boolean shouldDrawZone(int zone, int scoringStyle) {
        return true;
    }

    public int getContrastColor(int zone) {
        // Handle Miss-shots
        if (isOutOfRange(zone)) {
            return BLACK;
        }
        return Color.getContrast(getZone(zone).getFillColor());
    }

    private boolean isOutOfRange(int zone) {
        return zone < 0 || zone >= getZoneCount();
    }

    public int getZoneFromPoint(float ax, float ay, float arrowRadius) {
        for (int i = 0; i < zones.length; i++) {
            if (getZone(i).isInZone(ax, ay, arrowRadius)) {
                return i;
            }
        }
        return Shot.Companion.getMISS();
    }

    /**
     * Lists all zones that can be selected for the given scoringStyleIndex and arrow index.
     * Consecutive zones with the same text are excluded.
     *
     * @param scoringStyleIndex Index of the scoring style for this target.
     * @param arrow             Shot index, describes whether it is the first arrow(0), the second one, ...
     *                          This has an impact on the yielded score for some animal target faces.
     */
    @NonNull
    public List<SelectableZone> getSelectableZoneList(int scoringStyleIndex, int arrow) {
        ScoringStyle scoringStyle = getScoringStyle(scoringStyleIndex);
        List<SelectableZone> list = new ArrayList<>();
        String last = "";
        for (int i = 0; i <= zones.length; i++) {
            String zoneText = scoringStyle.zoneToString(i, arrow);
            if (!last.equals(zoneText)) {
                final int index = i == zones.length ? -1 : i;
                final int score = scoringStyle.getScoreByScoringRing(i, arrow);
                list.add(new SelectableZone(index, getZone(i), zoneText, score));
                last = zoneText;
            }
        }
        return list;
    }

    public long getSingleSpotTargetId() {
        return id;
    }
}
