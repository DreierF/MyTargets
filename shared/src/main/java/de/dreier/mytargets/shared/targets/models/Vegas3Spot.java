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

public class Vegas3Spot extends WA5Ring {
    private static final int ID = 4;

    public Vegas3Spot() {
        super(ID, R.string.vegas_3_spot);
        faceRadius = 0.48f;
        facePositions = new Coordinate[]{
                new Coordinate(-0.52f, 0.5f),
                new Coordinate(0.0f, -0.5f),
                new Coordinate(0.52f, 0.5f)
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
