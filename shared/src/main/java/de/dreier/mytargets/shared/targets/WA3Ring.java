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
import static de.dreier.mytargets.shared.utils.Color.FLAMINGO_RED;
import static de.dreier.mytargets.shared.utils.Color.LEMON_YELLOW;

public class WA3Ring extends TargetModelBase {

    public static final int ID = 3;

    public WA3Ring() {
        this(ID, R.string.wa_3_ring);
    }

    protected WA3Ring(int id, int nameRes) {
        super(id, nameRes);
        zones = new Zone[] {
                new Zone(83, LEMON_YELLOW, DARK_GRAY, 4),
                new Zone(167, LEMON_YELLOW, DARK_GRAY, 4),
                new Zone(333, LEMON_YELLOW, DARK_GRAY, 4),
                new Zone(500, FLAMINGO_RED, DARK_GRAY, 4)
        };
        scoringStyles = new ScoringStyle[]{
                new ScoringStyle(true, 10, 10, 9, 8),
                new ScoringStyle(false, 10, 9, 9, 8),
                new ScoringStyle(false, 11, 10, 9, 8),
                new ScoringStyle(true, 5, 5, 5, 4),
                new ScoringStyle(false, 9, 9, 9, 7)
        };
        diameters = new Dimension[]{
                new Dimension(40, CENTIMETER),
                new Dimension(60, CENTIMETER),
                new Dimension(80, CENTIMETER),
                new Dimension(92, CENTIMETER),
                new Dimension(122, CENTIMETER)
        };
        centerMark = new CenterMark(DARK_GRAY, 16.667f, 4, false);
    }

}
