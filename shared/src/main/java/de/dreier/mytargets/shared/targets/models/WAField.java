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

import static android.graphics.Color.WHITE;
import static de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER;
import static de.dreier.mytargets.shared.utils.Color.DARK_GRAY;
import static de.dreier.mytargets.shared.utils.Color.LEMON_YELLOW;

public class WAField extends TargetModelBase {
    public static final int ID = 13;


    public WAField() {
        this(ID, R.string.wa_field);
    }

    WAField(int id, int nameRes) {
        super(id, nameRes);
        zones = new ZoneBase[] {
                new CircularZone(0.1f, LEMON_YELLOW, DARK_GRAY, 4),
                new CircularZone(0.2f, LEMON_YELLOW, DARK_GRAY, 4),
                new CircularZone(0.4f, DARK_GRAY, WHITE, 4),
                new CircularZone(0.6f, DARK_GRAY, WHITE, 4),
                new CircularZone(0.8f, DARK_GRAY, WHITE, 4),
                new CircularZone(1.0f, DARK_GRAY, WHITE, 4)
        };
        scoringStyles = new ScoringStyle[]{
                new ScoringStyle(true, 5, 5, 4, 3, 2, 1),
                new ScoringStyle(false, 6, 5, 4, 3, 2, 1)
        };
        diameters = new Dimension[]{
                new Dimension(20, CENTIMETER),
                new Dimension(40, CENTIMETER),
                new Dimension(60, CENTIMETER),
                new Dimension(80, CENTIMETER)
        };
        decorator = new CenterMarkDecorator(DARK_GRAY, 10.5f, 4, false);
        isFieldTarget = true;
    }
}
