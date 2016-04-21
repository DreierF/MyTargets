/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.targets;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Diameter;

import static android.graphics.Color.WHITE;
import static de.dreier.mytargets.shared.models.Dimension.CENTIMETER;
import static de.dreier.mytargets.shared.utils.Color.DARK_GRAY;
import static de.dreier.mytargets.shared.utils.Color.LEMON_YELLOW;

public class WAField extends TargetModelBase {
    public static final int ID = 13;

    public WAField() {
        super(ID, R.string.wa_field);
        zones = new Zone[] {
                new Zone(50, LEMON_YELLOW, DARK_GRAY, 4),
                new Zone(100, LEMON_YELLOW, DARK_GRAY, 4),
                new Zone(200, DARK_GRAY, WHITE, 4),
                new Zone(300, DARK_GRAY, WHITE, 4),
                new Zone(400, DARK_GRAY, WHITE, 4),
                new Zone(500, DARK_GRAY, WHITE, 4)
        };
        zonePoints = new int[][]{{5, 5, 4, 3, 2, 1},
                {6, 5, 4, 3, 2, 1}};
        showAsX = new boolean[]{true, false};
        diameters = new Diameter[]{new Diameter(20, CENTIMETER), new Diameter(40, CENTIMETER),
                new Diameter(60, CENTIMETER), new Diameter(80, CENTIMETER)};
        centerMark = new CenterMark(DARK_GRAY, 10.5f, 4, false);
        isFieldTarget = true;
    }
}
