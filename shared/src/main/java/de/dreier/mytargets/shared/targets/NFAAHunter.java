/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.targets;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.Dimension;

import static de.dreier.mytargets.shared.utils.Color.DARK_GRAY;
import static de.dreier.mytargets.shared.utils.Color.WHITE;

public class NFAAHunter extends TargetModelBase {
    public static final int ID = 9;

    public NFAAHunter() {
        super(ID, R.string.nfaa_hunter);
        zones = new Zone[] {
                new Zone(50, WHITE, DARK_GRAY, 4),
                new Zone(100, WHITE, WHITE, 0),
                new Zone(300, DARK_GRAY, WHITE, 4),
                new Zone(500, DARK_GRAY, DARK_GRAY, 0),
        };
        zonePoints = new int[][]{{5, 5, 4, 3},
                {6, 5, 4, 3}};
        showAsX = new boolean[]{true, false};
        diameters = new Diameter[]{new Diameter(20, Dimension.CENTIMETER),
                new Diameter(35, Dimension.CENTIMETER),
                new Diameter(50, Dimension.CENTIMETER),
                new Diameter(65, Dimension.CENTIMETER)};
        centerMark = new CenterMark(WHITE, 7.307f, 4, true);
        isFieldTarget = true;
    }
}
