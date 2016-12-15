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
import de.dreier.mytargets.shared.targets.scoringstyle.ScoringStyle;
import de.dreier.mytargets.shared.targets.zone.CircularZone;
import de.dreier.mytargets.shared.targets.zone.ZoneBase;

import static de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER;
import static de.dreier.mytargets.shared.utils.Color.DARK_GRAY;
import static de.dreier.mytargets.shared.utils.Color.DBSC_BLUE;
import static de.dreier.mytargets.shared.utils.Color.DBSC_RED;
import static de.dreier.mytargets.shared.utils.Color.DBSC_YELLOW;

public class DBSCBlowpipe extends TargetModelBase {

    public static final int ID = 28;

    public DBSCBlowpipe() {
        super(ID, R.string.dbsc_blowpipe);
        zones = new ZoneBase[]{
                new CircularZone(0.3333f, DBSC_YELLOW, DARK_GRAY, 8),
                new CircularZone(0.6666f, DBSC_RED, DARK_GRAY, 8),
                new CircularZone(1f, DBSC_BLUE, DARK_GRAY, 8)
        };
        scoringStyles = new ScoringStyle[]{
                new ScoringStyle(false, 7, 5, 3)
        };
        diameters = new Dimension[]{
                new Dimension(18, CENTIMETER)
        };
    }
}
