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
package de.dreier.mytargets.shared.targets.models;

import android.graphics.PointF;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Dimension;

import static de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER;

public class WADanage3Spot extends WA3Ring {
    public static final int ID = 26;

    public WADanage3Spot() {
        super(ID, R.string.wa_danage_3_spot);
        faceRadius = 0.54556f;
        facePositions = new PointF[]{
                new PointF(0.0f, -0.39356598f),
                new PointF(-0.45444f, 0.39756605f),
                new PointF(0.45443994f, 0.39756605f)
        };
        diameters = new Dimension[]{
                new Dimension(40, CENTIMETER),
                new Dimension(60, CENTIMETER)
        };
    }
}