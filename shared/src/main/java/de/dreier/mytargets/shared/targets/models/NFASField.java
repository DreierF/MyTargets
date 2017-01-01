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
import de.dreier.mytargets.shared.targets.TargetOvalBase;
import de.dreier.mytargets.shared.targets.scoringstyle.ArrowAwareScoringStyle;
import de.dreier.mytargets.shared.targets.scoringstyle.ScoringStyle;
import de.dreier.mytargets.shared.targets.zone.CircularZone;
import de.dreier.mytargets.shared.targets.zone.EllipseZone;
import de.dreier.mytargets.shared.targets.zone.ZoneBase;

import static de.dreier.mytargets.shared.utils.Color.BLACK;
import static de.dreier.mytargets.shared.utils.Color.GRAY;
import static de.dreier.mytargets.shared.utils.Color.LIGHTER_GRAY;
import static de.dreier.mytargets.shared.utils.Color.ORANGE;
import static de.dreier.mytargets.shared.utils.Color.TURBO_YELLOW;

public class NFASField extends TargetOvalBase {
    public static final int ID = 22;

    public NFASField() {
        super(ID, R.string.nfas_field);
        zones = new ZoneBase[]{
                new CircularZone(0.162f, TURBO_YELLOW, BLACK, 5),
                new EllipseZone(1f, 0f, 0f, ORANGE, BLACK, 4),
                new CircularZone(1.0f, LIGHTER_GRAY, GRAY, 3)
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
