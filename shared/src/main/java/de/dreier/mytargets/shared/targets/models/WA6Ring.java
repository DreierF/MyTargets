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

public class WA6Ring extends TargetModelBase {

    public static final int ID = 1;

    public WA6Ring() {
        super(ID, R.string.wa_6_ring);
        realSizeFactor = 0.6f;
        zones = new ZoneBase[]{
                new CircularZone(0.084f, LEMON_YELLOW, DARK_GRAY, 3),
                new CircularZone(0.166f, LEMON_YELLOW, DARK_GRAY, 3),
                new CircularZone(0.334f, LEMON_YELLOW, DARK_GRAY, 3),
                new CircularZone(0.5f, FLAMINGO_RED, DARK_GRAY, 3),
                new CircularZone(0.666f, FLAMINGO_RED, DARK_GRAY, 3),
                new CircularZone(0.834f, CERULEAN_BLUE, DARK_GRAY, 3),
                new CircularZone(1.0f, CERULEAN_BLUE, DARK_GRAY, 3)
        };
        scoringStyles = new ScoringStyle[]{
                new ScoringStyle(R.string.recurve_style_x_5, true, 10, 10, 9, 8, 7, 6, 5),
                new ScoringStyle(R.string.recurve_style_10_5, false, 10, 10, 9, 8, 7, 6, 5),
                new ScoringStyle(R.string.compound_style, false, 10, 9, 9, 8, 7, 6, 5),
                new ScoringStyle(false, 11, 10, 9, 8, 7, 6, 5),
                new ScoringStyle(true, 5, 5, 5, 4, 4, 3, 3),
                new ScoringStyle(false, 9, 9, 9, 7, 7, 5, 5)
        };
        diameters = new Dimension[]{
                new Dimension(40, CENTIMETER),
                new Dimension(60, CENTIMETER),
                new Dimension(80, CENTIMETER),
                new Dimension(92, CENTIMETER),
                new Dimension(122, CENTIMETER)
        };
        decorator = new CenterMarkDecorator(DARK_GRAY, 8.333f, 4, false);
    }

    @Override
    public boolean shouldDrawZone(int zone, int scoringStyle) {
        // Do not draw second ring if we have a compound face
        return !(scoringStyle == 1 && zone == 0) &&
                !(scoringStyle == 2 && zone == 1);
    }
}
