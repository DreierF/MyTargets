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
import de.dreier.mytargets.shared.targets.decoration.BeursaultDecorator;
import de.dreier.mytargets.shared.targets.scoringstyle.ScoringStyle;
import de.dreier.mytargets.shared.targets.zone.CircularZone;
import de.dreier.mytargets.shared.targets.zone.ZoneBase;

import static android.graphics.Color.WHITE;
import static de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER;
import static de.dreier.mytargets.shared.utils.Color.DARK_GRAY;

public class Beursault extends TargetModelBase {

    public Beursault() {
        super(23, R.string.beursault);
        zones = new ZoneBase[]{
                new CircularZone(0.062178f, WHITE, DARK_GRAY, 27, false),
                new CircularZone(0.13934599f, WHITE, DARK_GRAY, 6, false),
                new CircularZone(0.19715601f, WHITE, DARK_GRAY, 6, false),
                new CircularZone(0.282716f, WHITE, DARK_GRAY, 27, false),
                new CircularZone(0.462034f, WHITE, DARK_GRAY, 6, false),
                new CircularZone(0.64135796f, WHITE, DARK_GRAY, 6, false),
                new CircularZone(0.820678f, WHITE, DARK_GRAY, 6, false),
                new CircularZone(1.0f, WHITE, DARK_GRAY, 27, false)
        };
        scoringStyles = new ScoringStyle[]{
                new ScoringStyle(false, 4, 4, 3, 3, 2, 2, 1, 1)
        };
        diameters = new Dimension[]{
                new Dimension(48, CENTIMETER)
        };
        decorator = new BeursaultDecorator(this);
    }
}
