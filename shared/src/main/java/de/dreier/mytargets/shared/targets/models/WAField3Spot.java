/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.targets.models;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Coordinate;

public class WAField3Spot extends WAField {
    public static final int ID = 25;

    public WAField3Spot() {
        super(ID, R.string.wa_field_3_spot);
        faceRadius = 0.32f;
        facePositions = new Coordinate[]{
                new Coordinate(0.0f, -0.68f),
                new Coordinate(0.0f, 0.0f),
                new Coordinate(0.0f, 0.68f)
        };
    }
}
