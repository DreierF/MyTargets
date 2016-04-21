/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.targets;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Diameter;

import static de.dreier.mytargets.shared.models.Dimension.INCH;
import static de.dreier.mytargets.shared.utils.Color.DARK_GRAY;
import static de.dreier.mytargets.shared.utils.Color.WHITE;

public class Worcester extends TargetModelBase {
    public static final int ID = 12;

    public Worcester() {
        super(ID, R.string.worcester_face);
        zones = new Zone[]{
                new Zone(100, WHITE, WHITE, 4),
                new Zone(200, DARK_GRAY, WHITE, 4),
                new Zone(300, DARK_GRAY, WHITE, 4),
                new Zone(400, DARK_GRAY, WHITE, 4),
                new Zone(500, DARK_GRAY, WHITE, 0)
        };
        zonePoints = new int[][]{{5, 4, 3, 2, 1}};
        showAsX = new boolean[]{false};
        diameters = new Diameter[]{new Diameter(16, INCH)};
        centerMark = new CenterMark(DARK_GRAY, 10.5f, 4, false);
    }
}
