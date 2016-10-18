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

public class WADanage6Spot extends WA3Ring {
    public static final int ID = 27;

    public WADanage6Spot() {
        super(ID, R.string.wa_danage_6_spot);
        faceRadius = 0.38692f;
        facePositions = new Coordinate[]{
                new Coordinate(0.0f, -0.61308f),
                new Coordinate(-0.61308f, -0.16767126f),
                new Coordinate(0.6130799f, -0.16767126f),
                new Coordinate(0.0f, 0.03152883f),
                new Coordinate(-0.378922f, 0.5530689f),
                new Coordinate(0.378922f, 0.5530689f)
        };
        diameters = new Dimension[]{
                new Dimension(40, CENTIMETER),
                new Dimension(60, CENTIMETER)
        };
    }
}
