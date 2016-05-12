/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.targets;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.Dimension;

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
        diameters = new Diameter[]{
                new Diameter(40, Dimension.CENTIMETER),
                new Diameter(60, Dimension.CENTIMETER),
                new Diameter(80, Dimension.CENTIMETER),
                new Diameter(92, Dimension.CENTIMETER),
                new Diameter(122, Dimension.CENTIMETER)
        };
        centerMark = new CenterMark(DARK_GRAY, 16.667f, 4, false);
    }

}
