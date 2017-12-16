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

import static de.dreier.mytargets.shared.models.Dimension.Unit.INCH;
import static de.dreier.mytargets.shared.utils.Color.DARK_GRAY;
import static de.dreier.mytargets.shared.utils.Color.WHITE;

public class Worcester extends TargetModelBase {
    public static final int ID = 12;

    public Worcester() {
        super(ID, R.string.worcester_face);
        zones = new ZoneBase[]{
                new CircularZone(0.2f, WHITE, WHITE, 4),
                new CircularZone(0.4f, DARK_GRAY, WHITE, 4),
                new CircularZone(0.6f, DARK_GRAY, WHITE, 4),
                new CircularZone(0.8f, DARK_GRAY, WHITE, 4),
                new CircularZone(1.0f, DARK_GRAY, WHITE, 0)
        };
        scoringStyles = new ScoringStyle[]{
                new ScoringStyle(false, 5, 4, 3, 2, 1)
        };
        diameters = new Dimension[]{
                new Dimension(16, INCH)
        };
        decorator = new CenterMarkDecorator(DARK_GRAY, 10.5f, 4, false);
    }
}
