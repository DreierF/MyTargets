/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.targets;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.utils.Color;

import static de.dreier.mytargets.shared.targets.ZoneType.CIRCLE;
import static de.dreier.mytargets.shared.targets.ZoneType.HEART;
import static de.dreier.mytargets.shared.utils.Color.BROWN;
import static de.dreier.mytargets.shared.utils.Color.CERULEAN_BLUE;
import static de.dreier.mytargets.shared.utils.Color.GRAY;
import static de.dreier.mytargets.shared.utils.Color.GREEN;
import static de.dreier.mytargets.shared.utils.Color.LIGHT_GRAY;
import static de.dreier.mytargets.shared.utils.Color.RED;
import static de.dreier.mytargets.shared.utils.Color.TURBO_YELLOW;

public class DAIR3D extends Target3DBase {
    public static final int ID = 19;

    public DAIR3D() {
        super(ID, R.string.dair_3d);
        zones = new Zone[]{
                new Zone(CIRCLE, 26.698f, 184.448f, 250.568f, GREEN, Color.BLACK, 3),
                new Zone(CIRCLE, 62, 184.448f, 250.568f, GREEN, Color.BLACK, 3),
                new Zone(CIRCLE, 26.698f, 637.198f, 455.401f, TURBO_YELLOW, Color.BLACK, 3),
                new Zone(CIRCLE, 26.698f, 491.448f, 699.901f, TURBO_YELLOW, Color.BLACK, 3),
                new Zone(CIRCLE, 26.698f, 563.447f, 576.566f, RED, Color.BLACK, 3),
                new Zone(CIRCLE, 62, 563.447f, 576.566f, RED, Color.BLACK, 3),
                new Zone(CIRCLE, 62, 637.198f, 455.401f, TURBO_YELLOW, Color.BLACK, 3),
                new Zone(CIRCLE, 62, 491.448f, 699.901f, TURBO_YELLOW, Color.BLACK, 3),
                new Zone(CIRCLE, 208.938f, 564.375f, 577.813f, CERULEAN_BLUE, Color.BLACK, 4),
                new Zone(HEART, 500, 500, 500, LIGHT_GRAY, Color.BLACK, 3),
                new Zone(CIRCLE, 500, 500, 500, BROWN, GRAY, 5)
        };
        zonePoints = new int[][]{
                {8, 8, 12, 12, 12, 12, 11, 11, 10, 8, 0},
                {14, 14, 12, 12, 12, 12, 11, 11, 10, 8, 0},
                {8, 8, 12, 12, 12, 12, 10, 10, 10, 8, 0},
                {14, 14, 12, 12, 12, 12, 10, 10, 10, 8, 0}};
        showAsX = new boolean[]{false, false, false, false};
    }
}
