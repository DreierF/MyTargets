/*
 * Copyright (C) 2016 Florian Dreier
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
import static de.dreier.mytargets.shared.utils.Color.DARK_GRAY;
import static de.dreier.mytargets.shared.utils.Color.SAPPHIRE_BLUE;
import static de.dreier.mytargets.shared.utils.Color.WHITE;

public class NFAAIndoor extends TargetModelBase {

    public static final int ID = 10;

    public NFAAIndoor() {
        super(ID, R.string.nfaa_indoor);
        zones = new ZoneBase[] {
                new CircularZone(0.1f, WHITE, DARK_GRAY, 4),
                new CircularZone(0.2f, WHITE, DARK_GRAY, 0),
                new CircularZone(0.4f, SAPPHIRE_BLUE, WHITE, 4),
                new CircularZone(0.6f, SAPPHIRE_BLUE, WHITE, 4),
                new CircularZone(0.8f, SAPPHIRE_BLUE, WHITE, 4),
                new CircularZone(1.0f, SAPPHIRE_BLUE, WHITE, 4)
        };
        scoringStyles = new ScoringStyle[]{
                new ScoringStyle(true, 5, 5, 4, 3, 2, 1),
                new ScoringStyle(false, 6, 5, 4, 3, 2, 1),
                new ScoringStyle(false, 7, 5, 4, 3, 2, 1)
        };
        diameters = new Dimension[]{
                new Dimension(40, CENTIMETER)
        };
        decorator = new CenterMarkDecorator(DARK_GRAY, 23.783f, 8, true);
    }

}
