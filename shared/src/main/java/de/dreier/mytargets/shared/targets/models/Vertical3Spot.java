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

import static de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER;

public class Vertical3Spot extends WA5Ring {
    public static final int ID = 5;

    public Vertical3Spot() {
        super(ID, R.string.vertical_3_spot);
        faceRadius = 0.32f;
        facePositions = new PointF[]{
                new PointF(0.0f, -0.68f),
                new PointF(0.0f, 0.0f),
                new PointF(0.0f, 0.68f)
        };
        diameters = new Dimension[]{new Dimension(40, CENTIMETER),
                new Dimension(60, CENTIMETER)};
    }

    @Override
    public boolean shouldDrawZone(int zone, int scoringStyle) {
        // Do not draw second ring if we have a 3 Spot for compound
        return !(scoringStyle == 1 && zone == 1);
    }
}
