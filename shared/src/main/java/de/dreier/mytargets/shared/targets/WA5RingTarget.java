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
import static de.dreier.mytargets.shared.utils.Color.CERULEAN_BLUE;
import static de.dreier.mytargets.shared.utils.Color.DARK_GRAY;
import static de.dreier.mytargets.shared.utils.Color.FLAMINGO_RED;
import static de.dreier.mytargets.shared.utils.Color.LEMON_YELLOW;

public class WA5RingTarget extends TargetModelBase {
    public static final int ID = 2;

    public WA5RingTarget() {
        this(ID, R.string.wa_5_ring);
    }

    protected WA5RingTarget(int id, int nameRes) {
        super(id, nameRes);
        zones = new Zone[] {
                new Zone(50, LEMON_YELLOW, DARK_GRAY, 4),
                new Zone(100, LEMON_YELLOW, DARK_GRAY, 4),
                new Zone(200, LEMON_YELLOW, DARK_GRAY, 4),
                new Zone(300, FLAMINGO_RED, DARK_GRAY, 4),
                new Zone(400, FLAMINGO_RED, DARK_GRAY, 4),
                new Zone(500, CERULEAN_BLUE, DARK_GRAY, 4)
        };
        zonePoints = new int[][]{{10, 10, 9, 8, 7, 6},
                {10, 9, 9, 8, 7, 6},
                {11, 10, 9, 8, 7, 6},
                {5, 5, 5, 4, 4, 3},
                {9, 9, 9, 7, 7, 5}};
        showAsX = new boolean[]{true, false, false, true, false};
        diameters = new Diameter[]{new Diameter(40, CENTIMETER),
                new Diameter(60, CENTIMETER),
                new Diameter(80, CENTIMETER),
                new Diameter(92, CENTIMETER),
                new Diameter(122, CENTIMETER)};
        centerMark = new CenterMark(DARK_GRAY, 10f, 4, false);
    }

}
