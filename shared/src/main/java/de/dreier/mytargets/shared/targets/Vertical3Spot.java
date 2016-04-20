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

public class Vertical3Spot extends SpotBase {
    public static final int ID = 5;

    public Vertical3Spot() {
        super(ID, R.string.vertical_3_spot);
        face = new WA5RingTarget(true);
        faceRadius = 160;
        facePositions = new Coordinate[]{
                new Coordinate(500, 160),
                new Coordinate(500, 500),
                new Coordinate(500, 840)
        };
        diameters = new Diameter[]{new Diameter(40, CENTIMETER),
                new Diameter(60, CENTIMETER)};
    }
}
