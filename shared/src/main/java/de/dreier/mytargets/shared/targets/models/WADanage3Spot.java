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

public class WADanage3Spot extends WA3Ring {
    public static final int ID = 26;

    public WADanage3Spot() {
        super(ID, R.string.wa_danage_3_spot);
        faceRadius = 0.54556f;
        facePositions = new Coordinate[]{
                new Coordinate(0.0f, -0.39356598f),
                new Coordinate(-0.45444f, 0.39756605f),
                new Coordinate(0.45443994f, 0.39756605f)
        };
        diameters = new Dimension[] {
                new Dimension(40, CENTIMETER),
                new Dimension(60, CENTIMETER)
        };
    }
}
