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
import static de.dreier.mytargets.shared.utils.Color.WHITE;

public class NFAAExpertField extends TargetModelBase {

    public static final int ID = 8;

    public NFAAExpertField() {
        super(ID, R.string.nfaa_expert_field);
        zones = new ZoneBase[] {
                new CircularZone(0.1f, DARK_GRAY, WHITE, 4),
                new CircularZone(0.2f, DARK_GRAY, DARK_GRAY, 0),
                new CircularZone(0.4f, WHITE, DARK_GRAY, 4),
                new CircularZone(0.6f, WHITE, WHITE, 0),
                new CircularZone(0.8f, DARK_GRAY, WHITE, 4),
                new CircularZone(1.0f, DARK_GRAY, DARK_GRAY, 0),
        };
        scoringStyles = new ScoringStyle[]{
                new ScoringStyle(true, 6, 5, 4, 3, 2, 1)
        };
        diameters = new Dimension[]{
                new Dimension(20, CENTIMETER),
                new Dimension(35, CENTIMETER),
                new Dimension(50, CENTIMETER),
                new Dimension(65, CENTIMETER)
        };
        decorator = new CenterMarkDecorator(WHITE, 7.307f, 4, true);
        isFieldTarget = true;
    }
}
