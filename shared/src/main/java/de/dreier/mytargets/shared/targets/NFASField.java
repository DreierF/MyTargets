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
import static de.dreier.mytargets.shared.utils.Color.TURBO_YELLOW;

public class NFASField extends TargetOvalBase {
    public static final int ID = 22;

    public NFASField() {
        super(ID, R.string.nfas_field);
        zones = new Zone[]{
                new Zone(81, TURBO_YELLOW, BLACK, 5),
                new Zone(ELLIPSE, 500, 500, 500, ORANGE, BLACK, 4),
                new Zone(500, LIGHTER_GRAY, GRAY, 3)
        };
        scoringStyles = new ScoringStyle[]{
                new ArrowAwareScoringStyle(false, new int[][]{
                        {24, 20, 16},
                        {14, 14, 10},
                        {8, 8, 4}
                })
        };
        isFieldTarget = true;
    }
}
