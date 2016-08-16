/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.targets.models;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.models.Dimension;

import static de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER;

public class Vertical3Spot extends WA5Ring {
    public static final int ID = 5;

    public Vertical3Spot() {
        super(ID, R.string.vertical_3_spot);
        faceRadius = 0.32f;
        facePositions = new Coordinate[]{
                new Coordinate(0.0f, -0.68f),
                new Coordinate(0.0f, 0.0f),
                new Coordinate(0.0f, 0.68f)
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
