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
import static de.dreier.mytargets.shared.utils.Color.RED_MISS;
import static de.dreier.mytargets.shared.utils.Color.YELLOW;

public class HitOrMiss extends TargetModelBase {

    public HitOrMiss() {
        super(14, R.string.hit_or_miss);
        zones = new Zone[] {
                new Zone(125, YELLOW, DARK_GRAY, 3),
                new Zone(500, RED_MISS, DARK_GRAY, 3)
        };
        zonePoints = new int[][]{{1, 0}};
        showAsX = new boolean[]{false};
        diameters = new Diameter[]{new Diameter(30, CENTIMETER), new Diameter(96, CENTIMETER)};
        centerMark = new CenterMark(DARK_GRAY, 4.188f, 4, false);
    }
}
