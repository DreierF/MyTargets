/*
 * Copyright (C) 2017 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package de.dreier.mytargets.shared.targets.models;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.targets.decoration.CenterMarkDecorator;
import de.dreier.mytargets.shared.targets.scoringstyle.ScoringStyle;
import de.dreier.mytargets.shared.targets.zone.CircularZone;
import de.dreier.mytargets.shared.targets.zone.ZoneBase;

import static de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER;
import static de.dreier.mytargets.shared.utils.Color.CERULEAN_BLUE;
import static de.dreier.mytargets.shared.utils.Color.DARK_GRAY;
import static de.dreier.mytargets.shared.utils.Color.FLAMINGO_RED;
import static de.dreier.mytargets.shared.utils.Color.LEMON_YELLOW;

public class WA5Ring extends TargetModelBase {
    public static final int ID = 2;

    public WA5Ring() {
        this(ID, R.string.wa_5_ring);
    }

    WA5Ring(int id, int nameRes) {
        super(id, nameRes);
        realSizeFactor = 0.5f;
        zones = new ZoneBase[]{
                new CircularZone(0.1f, LEMON_YELLOW, DARK_GRAY, 4),
                new CircularZone(0.2f, LEMON_YELLOW, DARK_GRAY, 4),
                new CircularZone(0.4f, LEMON_YELLOW, DARK_GRAY, 4),
                new CircularZone(0.6f, FLAMINGO_RED, DARK_GRAY, 4),
                new CircularZone(0.8f, FLAMINGO_RED, DARK_GRAY, 4),
                new CircularZone(1.0f, CERULEAN_BLUE, DARK_GRAY, 4)
        };
        scoringStyles = new ScoringStyle[]{
                new ScoringStyle(R.string.recurve_style_x_6, true, 10, 10, 9, 8, 7, 6),
                new ScoringStyle(R.string.recurve_style_10_6, false, 10, 10, 9, 8, 7, 6),
                new ScoringStyle(R.string.compound_style, false, 10, 9, 9, 8, 7, 6),
                new ScoringStyle(false, 11, 10, 9, 8, 7, 6),
                new ScoringStyle(true, 5, 5, 5, 4, 4, 3),
                new ScoringStyle(false, 9, 9, 9, 7, 7, 5)
        };
        diameters = new Dimension[]{
                new Dimension(40, CENTIMETER),
                new Dimension(60, CENTIMETER),
                new Dimension(80, CENTIMETER),
                new Dimension(92, CENTIMETER),
                new Dimension(122, CENTIMETER)
        };
        decorator = new CenterMarkDecorator(DARK_GRAY, 10f, 4, false);
    }

    @Override
    public boolean shouldDrawZone(int zone, int scoringStyle) {
        // Do not draw second ring if we have a compound face
        return !(scoringStyle == 1 && zone == 0) &&
                !(scoringStyle == 2 && zone == 1);
    }
}
