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

public class WAVegas3Spot extends WA5Ring {
    private static final int ID = 4;

    public WAVegas3Spot() {
        super(ID, R.string.vegas_3_spot);
        faceRadius = 0.48f;
        facePositions = new PointF[]{
                new PointF(-0.52f, 0.5f),
                new PointF(0.0f, -0.5f),
                new PointF(0.52f, 0.5f)
        };
        diameters = new Dimension[]{new Dimension(40, CENTIMETER),
                new Dimension(60, CENTIMETER)};
    }

    @Override
    public long getSingleSpotTargetId() {
        return WA5Ring.ID;
    }
}
