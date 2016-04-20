/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.targets;

import de.dreier.mytargets.shared.R;

import static de.dreier.mytargets.shared.targets.ZoneType.CIRCLE;
import static de.dreier.mytargets.shared.targets.ZoneType.HEART;
import static de.dreier.mytargets.shared.utils.Color.BLACK;
import static de.dreier.mytargets.shared.utils.Color.BROWN;
import static de.dreier.mytargets.shared.utils.Color.CERULEAN_BLUE;
import static de.dreier.mytargets.shared.utils.Color.GRAY;
import static de.dreier.mytargets.shared.utils.Color.LIGHT_GRAY;
import static de.dreier.mytargets.shared.utils.Color.RED;

public class IBO3D extends Target3DBase {
    public IBO3D() {
        super(17, R.string.ibo_3d);
        zones = new Zone[]{
                new Zone(CIRCLE, 26.698f, 563.447f, 576.566f, RED, BLACK, 3),
                new Zone(CIRCLE, 62, 563.447f, 576.566f, RED, BLACK, 3),
                new Zone(CIRCLE, 208.938f, 564.375f, 577.813f, CERULEAN_BLUE, BLACK, 4),
                new Zone(HEART, 500, 500, 500, LIGHT_GRAY, BLACK, 3),
                new Zone(CIRCLE, 500, 500, 500, BROWN, GRAY, 5)
        };
        zonePoints = new int[][]{{11, 11, 10, 8, 5}};
        showAsX = new boolean[]{false};
    }
}
