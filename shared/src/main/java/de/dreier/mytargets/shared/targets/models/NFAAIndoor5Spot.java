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

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.targets.decoration.CenterMarkDecorator;
import de.dreier.mytargets.shared.targets.scoringstyle.ScoringStyle;
import de.dreier.mytargets.shared.targets.zone.CircularZone;
import de.dreier.mytargets.shared.targets.zone.ZoneBase;

import static de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER;
import static de.dreier.mytargets.shared.utils.Color.DARK_GRAY;
import static de.dreier.mytargets.shared.utils.Color.SAPPHIRE_BLUE;
import static de.dreier.mytargets.shared.utils.Color.WHITE;

public class NFAAIndoor5Spot extends TargetModelBase {

    public static final int ID = 11;

    public NFAAIndoor5Spot() {
        super(ID, R.string.nfaa_indoor_5_spot);
        zones = new ZoneBase[]{
                new CircularZone(0.25f, WHITE, DARK_GRAY, 2),
                new CircularZone(0.5f, WHITE, DARK_GRAY, 2),
                new CircularZone(0.75f, SAPPHIRE_BLUE, WHITE, 2),
                new CircularZone(1.0f, SAPPHIRE_BLUE, WHITE, 0)
        };
        scoringStyles = new ScoringStyle[]{
                new ScoringStyle(true, 5, 5, 4, 4),
                new ScoringStyle(false, 6, 6, 5, 4),
                new ScoringStyle(false, 7, 6, 5, 4)
        };
        diameters = new Dimension[]{
                new Dimension(40, CENTIMETER)
        };
        decorator = new CenterMarkDecorator(DARK_GRAY, 25f, 9, true);
        facePositions = new PointF[]{
                new PointF(-0.6f, -0.6f),
                new PointF(0.6f, -0.6f),
                new PointF(0.0f, 0.0f),
                new PointF(-0.6f, 0.6f),
                new PointF(0.6f, 0.6f)
        };
        faceRadius = 0.4f;
    }
}
