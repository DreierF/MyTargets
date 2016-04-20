/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.targets;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.models.Diameter;

import static de.dreier.mytargets.shared.models.Dimension.CENTIMETER;

public class Vertical3Spot extends WA5RingTarget {
    public static final int ID = 5;

    public Vertical3Spot() {
        super(ID, R.string.vertical_3_spot);
        faceRadius = 160;
        facePositions = new Coordinate[]{
                new Coordinate(500, 160),
                new Coordinate(500, 500),
                new Coordinate(500, 840)
        };
        diameters = new Diameter[]{new Diameter(40, CENTIMETER),
                new Diameter(60, CENTIMETER)};
    }

    @Override
    public boolean shouldDrawZone(int zone, int scoringStyle) {
        // Do not draw second ring if we have a 3 Spot for compound
        return !(scoringStyle == 1 && zone == 1);
    }
}
