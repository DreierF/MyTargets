/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
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