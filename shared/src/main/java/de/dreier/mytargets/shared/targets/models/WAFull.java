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
import de.dreier.mytargets.shared.targets.scoringstyle.ColorScoringStyle;
import de.dreier.mytargets.shared.targets.scoringstyle.ScoringStyle;
import de.dreier.mytargets.shared.targets.zone.CircularZone;
import de.dreier.mytargets.shared.targets.zone.ZoneBase;

import static de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER;
import static de.dreier.mytargets.shared.utils.Color.BLACK;
import static de.dreier.mytargets.shared.utils.Color.CERULEAN_BLUE;
import static de.dreier.mytargets.shared.utils.Color.DARK_GRAY;
import static de.dreier.mytargets.shared.utils.Color.FLAMINGO_RED;
import static de.dreier.mytargets.shared.utils.Color.LEMON_YELLOW;
import static de.dreier.mytargets.shared.utils.Color.WHITE;

public class WAFull extends TargetModelBase {

    public static final int ID = 0;

    public WAFull() {
        super(ID, R.string.wa_full);
        zones = new ZoneBase[]{
                new CircularZone(0.05f, LEMON_YELLOW, DARK_GRAY, 2),
                new CircularZone(0.1f, LEMON_YELLOW, DARK_GRAY, 2),
                new CircularZone(0.2f, LEMON_YELLOW, DARK_GRAY, 2),
                new CircularZone(0.3f, FLAMINGO_RED, DARK_GRAY, 2),
                new CircularZone(0.4f, FLAMINGO_RED, DARK_GRAY, 2),
                new CircularZone(0.5f, CERULEAN_BLUE, DARK_GRAY, 2),
                new CircularZone(0.6f, CERULEAN_BLUE, DARK_GRAY, 2),
                new CircularZone(0.7f, BLACK, DARK_GRAY, 2),
                new CircularZone(0.8f, BLACK, DARK_GRAY, 2),
                new CircularZone(0.9f, WHITE, DARK_GRAY, 2),
                new CircularZone(1.0f, WHITE, DARK_GRAY, 2)
        };
        scoringStyles = new ScoringStyle[]{
                new ScoringStyle(true, 10, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1),
                new ScoringStyle(false, 10, 9, 9, 8, 7, 6, 5, 4, 3, 2, 1),
                new ScoringStyle(false, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1),
                new ScoringStyle(true, 5, 5, 5, 4, 4, 3, 3, 2, 2, 1, 1),
                new ScoringStyle(false, 9, 9, 9, 7, 7, 5, 5, 3, 3, 1, 1),
                new ColorScoringStyle("FCFS Color (Reversed 10-1)", 31, 1, 1, 2, 4, 4, 6, 6, 8, 8, 10, 10)
        };
        diameters = new Dimension[]{
                new Dimension(40, CENTIMETER),
                new Dimension(60, CENTIMETER),
                new Dimension(80, CENTIMETER),
                new Dimension(92, CENTIMETER),
                new Dimension(122, CENTIMETER)
        };
        decorator = new CenterMarkDecorator(DARK_GRAY, 5f, 4, false);
    }
}
