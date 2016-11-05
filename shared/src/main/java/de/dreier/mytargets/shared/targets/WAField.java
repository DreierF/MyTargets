/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.targets;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Dimension;

import static android.graphics.Color.WHITE;
import static de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER;
import static de.dreier.mytargets.shared.utils.Color.DARK_GRAY;
import static de.dreier.mytargets.shared.utils.Color.LEMON_YELLOW;

public class WAField extends TargetModelBase {
    public static final int ID = 13;


    WAField() {
        this(ID, R.string.wa_field);
    }

    WAField(int id, int nameRes) {
        super(id, nameRes);
        zones = new Zone[]{
                new Zone(50, LEMON_YELLOW, DARK_GRAY, 4),
                new Zone(100, LEMON_YELLOW, DARK_GRAY, 4),
                new Zone(200, DARK_GRAY, WHITE, 4),
                new Zone(300, DARK_GRAY, WHITE, 4),
                new Zone(400, DARK_GRAY, WHITE, 4),
                new Zone(500, DARK_GRAY, WHITE, 4)
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
        centerMark = new CenterMark(DARK_GRAY, 10.5f, 4, false);
        isFieldTarget = true;
    }
}
