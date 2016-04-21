/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.targets;

import de.dreier.mytargets.shared.R;

import static de.dreier.mytargets.shared.targets.ZoneType.ELLIPSE;
import static de.dreier.mytargets.shared.utils.Color.BLACK;
import static de.dreier.mytargets.shared.utils.Color.GRAY;
import static de.dreier.mytargets.shared.utils.Color.LIGHTER_GRAY;
import static de.dreier.mytargets.shared.utils.Color.ORANGE;

public class IFAAAnimal extends TargetOvalBase {
    public static final int ID = 20;

    public IFAAAnimal() {
        super(ID, R.string.ifaa_animal);
        zones = new Zone[]{
                new Zone(ELLIPSE, 500, 500, 500, ORANGE, BLACK, 4),
                new Zone(500, LIGHTER_GRAY, GRAY, 3)
        };
        zonePoints = new int[][]{{20, 18}, {16, 14}, {12, 10}};
        showAsX = new boolean[]{false};
    }
}
