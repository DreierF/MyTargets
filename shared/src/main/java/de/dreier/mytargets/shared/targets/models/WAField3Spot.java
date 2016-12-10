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

public class WAField3Spot extends WAField {
    public static final int ID = 25;

    public WAField3Spot() {
        super(ID, R.string.wa_field_3_spot);
        faceRadius = 0.32f;
        facePositions = new PointF[]{
                new PointF(0.0f, -0.68f),
                new PointF(0.0f, 0.0f),
                new PointF(0.0f, 0.68f)
        };
    }
}
