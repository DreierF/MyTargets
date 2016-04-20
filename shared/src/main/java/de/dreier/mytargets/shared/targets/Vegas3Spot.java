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
import de.dreier.mytargets.shared.models.Dimension;

import static de.dreier.mytargets.shared.models.Dimension.CENTIMETER;

public class Vegas3Spot extends SpotBase {

    public static final int ID = 4;

    public Vegas3Spot() {
        super(ID, R.string.vegas_3_spot);
        face = new WA5RingTarget();
        faceRadius = 240;
        facePositions = new Coordinate[]{
                new Coordinate(240, 750),
                new Coordinate(500, 250),
                new Coordinate(760, 750)
        };
        diameters = new Diameter[]{new Diameter(40, CENTIMETER),
                new Diameter(60, CENTIMETER)};
    }
}
