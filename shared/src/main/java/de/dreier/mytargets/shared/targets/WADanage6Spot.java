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
    public static final int ID = 27;

    public WADanage6Spot() {
        super(ID, R.string.wa_danage_6_spot);
        faceRadius = 193;
        facePositions = new Coordinate[]{
                new Coordinate(500, 515.7644f),
                new Coordinate(500, 193.46f),
                new Coordinate(806.54f, 416.16437f),
                new Coordinate(193.46f, 416.16437f),
                new Coordinate(689.461f, 776.5344f),
                new Coordinate(310.539f, 776.5344f)
        };
        diameters = new Dimension[]{
                new Dimension(40, CENTIMETER),
                new Dimension(60, CENTIMETER)
        };
    }
}
