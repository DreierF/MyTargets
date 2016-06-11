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
import static de.dreier.mytargets.shared.utils.Color.DARK_GRAY;
import static de.dreier.mytargets.shared.utils.Color.SAPPHIRE_BLUE;
import static de.dreier.mytargets.shared.utils.Color.WHITE;

class NFAAIndoor5Spot extends TargetModelBase {
    public NFAAIndoor5Spot() {
        super(11, R.string.nfaa_indoor_5_spot);
        zones = new Zone[]{
                new Zone(125, WHITE, DARK_GRAY, 2),
                new Zone(250, WHITE, DARK_GRAY, 2),
                new Zone(375, SAPPHIRE_BLUE, WHITE, 2),
                new Zone(500, SAPPHIRE_BLUE, WHITE, 0)
        };
        scoringStyles = new ScoringStyle[]{
                new ScoringStyle(true, 5, 5, 5, 4),
                new ScoringStyle(false, 6, 6, 5, 4),
                new ScoringStyle(false, 7, 6, 5, 4)
        };
        //TODO 6 if inner ring is hit but only 7 if arrow is inside
        diameters = new Dimension[]{
                new Dimension(40, CENTIMETER)
        };
        centerMark = new CenterMark(DARK_GRAY, 25f, 9, true);
        facePositions = new Coordinate[]{
                new Coordinate(200, 200),
                new Coordinate(800, 200),
                new Coordinate(500, 500),
                new Coordinate(200, 800),
                new Coordinate(800, 800)
        };
        faceRadius = 200;
    }
}