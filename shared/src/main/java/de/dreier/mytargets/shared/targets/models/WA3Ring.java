/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
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
import static de.dreier.mytargets.shared.utils.Color.FLAMINGO_RED;
import static de.dreier.mytargets.shared.utils.Color.LEMON_YELLOW;

public class WA3Ring extends TargetModelBase {

    public static final int ID = 3;

    public WA3Ring() {
        this(ID, R.string.wa_3_ring);
    }

    WA3Ring(int id, int nameRes) {
        super(id, nameRes);
        realSizeFactor = 0.3f;
        zones = new ZoneBase[] {
                new CircularZone(0.166f, LEMON_YELLOW, DARK_GRAY, 4),
                new CircularZone(0.334f, LEMON_YELLOW, DARK_GRAY, 4),
                new CircularZone(0.666f, LEMON_YELLOW, DARK_GRAY, 4),
                new CircularZone(1.0f, FLAMINGO_RED, DARK_GRAY, 4)
        };
        scoringStyles = new ScoringStyle[]{
                new ScoringStyle(true, 10, 10, 9, 8),
                new ScoringStyle(false, 10, 9, 9, 8),
                new ScoringStyle(false, 11, 10, 9, 8),
                new ScoringStyle(true, 5, 5, 5, 4),
                new ScoringStyle(false, 9, 9, 9, 7)
        };
        diameters = new Dimension[]{
                new Dimension(40, CENTIMETER),
                new Dimension(60, CENTIMETER),
                new Dimension(80, CENTIMETER),
                new Dimension(92, CENTIMETER),
                new Dimension(122, CENTIMETER)
        };
        decorator = new CenterMarkDecorator(DARK_GRAY, 16.667f, 4, false);
    }

}
