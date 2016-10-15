/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.targets;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.models.Dimension;

import static de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER;

public class WADanage6Spot extends WA3Ring {
    public static final int ID = 26;

    public WADanage6Spot() {
        super(ID, R.string.wa_danage_6_spot);
        faceRadius = 190;
        facePositions = new Coordinate[]{
                new Coordinate(500, 500),
                new Coordinate(800, 430),
                new Coordinate(690, 790),
                new Coordinate(310, 790),
                new Coordinate(190, 430),
                new Coordinate(500, 210)
        };
        diameters = new Dimension[]{
                new Dimension(40, CENTIMETER),
                new Dimension(60, CENTIMETER)
        };
    }
}
