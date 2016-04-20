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

public class WA3Ring3Spot extends WA3RingTarget {
    public static final int ID = 6;

    public WA3Ring3Spot() {
        super(ID, R.string.wa_3_ring_3_spot);
        faceRadius = 240;
        facePositions = new Coordinate[]{
                new Coordinate(240, 750),
                new Coordinate(500, 250),
                new Coordinate(760, 750)
        };
        diameters = new Diameter[]{new Diameter(40, Dimension.CENTIMETER),
                new Diameter(60, Dimension.CENTIMETER)};
    }
}
