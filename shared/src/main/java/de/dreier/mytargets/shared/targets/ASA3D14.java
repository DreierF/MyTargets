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
import static de.dreier.mytargets.shared.utils.Color.GREEN;
import static de.dreier.mytargets.shared.utils.Color.LIGHT_GRAY;
import static de.dreier.mytargets.shared.utils.Color.TURBO_YELLOW;

public class ASA3D14 extends Target3DBase {
    public ASA3D14() {
        super(16, R.string.asa_3d_14);
        zones = new Zone[]{
                new Zone(CIRCLE, 26.698f, 184.448f, 250.568f, GREEN, BLACK, 3),
                new Zone(CIRCLE, 62, 184.448f, 250.568f, GREEN, BLACK, 3),
                new Zone(CIRCLE, 26.698f, 637.198f, 455.401f, TURBO_YELLOW, BLACK, 3),
                new Zone(CIRCLE, 26.698f, 491.448f, 699.901f, TURBO_YELLOW, BLACK, 3),
                new Zone(CIRCLE, 62, 637.198f, 455.401f, TURBO_YELLOW, BLACK, 3),
                new Zone(CIRCLE, 62, 491.448f, 699.901f, TURBO_YELLOW, BLACK, 3),
                new Zone(CIRCLE, 208.938f, 564.375f, 577.813f, CERULEAN_BLUE, BLACK, 4),
                new Zone(HEART, 500, 500, 500, LIGHT_GRAY, BLACK, 3),
                new Zone(CIRCLE, 500, 500, 500, BROWN, GRAY, 5)
        };
        scoringStyles = new ScoringStyle[]{
                new ScoringStyle(false, 14, 14, 12, 12, 12, 12, 10, 8, 5)
        };
    }
}
