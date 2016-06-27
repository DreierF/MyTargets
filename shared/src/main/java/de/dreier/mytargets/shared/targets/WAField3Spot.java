/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.targets;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Coordinate;

class WAField3Spot extends WAField {
    public static final int ID = 25;

    WAField3Spot() {
        super(ID, R.string.wa_field_3_spot);
        faceRadius = 160;
        facePositions = new Coordinate[]{
                new Coordinate(500, 160),
                new Coordinate(500, 500),
                new Coordinate(500, 840)
        };
    }
}
