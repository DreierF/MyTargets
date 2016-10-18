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

public class WADanage3Spot extends WA3Ring {
    public static final int ID = 26;

    public WADanage3Spot() {
        super(ID, R.string.wa_danage_3_spot);
        faceRadius = 273;
        facePositions = new Coordinate[]{
                new Coordinate(500, 303.217f),
                new Coordinate(272.78f, 698.783f),
                new Coordinate(727.22f, 698.783f)
        };
        diameters = new Dimension[] {
                new Dimension(40, CENTIMETER),
                new Dimension(60, CENTIMETER)
        };
    }
}
