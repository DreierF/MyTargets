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
import de.dreier.mytargets.shared.models.ETargetType;
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

public class IFAAAnimal extends TargetOvalBase {
    public static final int ID = 20;

    public IFAAAnimal() {
        super(ID, R.string.ifaa_animal);
        zones = new ZoneBase[]{
                new EllipseZone(1.0f, 0.0f, 0.0f, ORANGE, BLACK, 4),
                new CircularZone(1.0f, LIGHTER_GRAY, GRAY, 3)
        };
        scoringStyles = new ScoringStyle[]{
                new ArrowAwareScoringStyle(false, new int[][]{
                        {20, 18},
                        {16, 14},
                        {12, 10}
                }),
                new ArrowAwareScoringStyle(false, new int[][]{
                        {20, 15},
                        {15, 10}
                })
        };
        type = ETargetType.THREE_D;
    }
}
