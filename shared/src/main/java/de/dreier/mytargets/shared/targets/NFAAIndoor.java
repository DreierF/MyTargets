/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.targets;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Diameter;

import static de.dreier.mytargets.shared.models.Dimension.CENTIMETER;
import static de.dreier.mytargets.shared.utils.Color.DARK_GRAY;
import static de.dreier.mytargets.shared.utils.Color.SAPPHIRE_BLUE;
import static de.dreier.mytargets.shared.utils.Color.WHITE;

public class NFAAIndoor extends TargetModelBase {

    public static final int ID = 10;

    public NFAAIndoor() {
        super(ID, R.string.nfaa_indoor);
        zones = new Zone[] {
                new Zone(50, WHITE, DARK_GRAY, 4),
                new Zone(100, WHITE, DARK_GRAY, 0),
                new Zone(200, SAPPHIRE_BLUE, WHITE, 4),
                new Zone(300, SAPPHIRE_BLUE, WHITE, 4),
                new Zone(400, SAPPHIRE_BLUE, WHITE, 4),
                new Zone(500, SAPPHIRE_BLUE, WHITE, 4)
        };
        zonePoints = new int[][]{{5, 5, 4, 3, 2, 1},
                {6, 5, 4, 3, 2, 1},
                {7, 5, 4, 3, 2, 1}}; //TODO 6 if inner ring is hit but only 7 if arrow is inside
        showAsX = new boolean[]{true, false, false};
        diameters = new Diameter[]{new Diameter(40, CENTIMETER)};
        centerMark = new CenterMark(DARK_GRAY, 23.783f, 8, true);
    }

}
