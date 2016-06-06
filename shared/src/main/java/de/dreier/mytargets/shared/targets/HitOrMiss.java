/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.targets;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Dimension;

import static de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER;
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
        scoringStyles = new ScoringStyle[]{
                new ScoringStyle(false, 1, 0)
        };
        diameters = new Dimension[]{
                new Dimension(30, CENTIMETER),
                new Dimension(96, CENTIMETER)
        };
        centerMark = new CenterMark(DARK_GRAY, 4.188f, 4, false);
    }
}
