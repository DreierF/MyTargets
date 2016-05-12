/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.targets;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Diameter;

import static de.dreier.mytargets.shared.models.Dimension.CENTIMETER;
import static de.dreier.mytargets.shared.utils.Color.DARK_GRAY;
import static de.dreier.mytargets.shared.utils.Color.WHITE;

public class NFAAExpertField extends TargetModelBase {

    public static final int ID = 8;

    public NFAAExpertField() {
        super(ID, R.string.nfaa_expert_field);
        zones = new Zone[] {
                new Zone(50, DARK_GRAY, WHITE, 4),
                new Zone(100, DARK_GRAY, DARK_GRAY, 0),
                new Zone(200, WHITE, DARK_GRAY, 4),
                new Zone(300, WHITE, WHITE, 0),
                new Zone(400, DARK_GRAY, WHITE, 4),
                new Zone(500, DARK_GRAY, DARK_GRAY, 0),
        };
        scoringStyles = new ScoringStyle[]{
                new ScoringStyle(true, 6, 5, 4, 3, 2, 1)
        };
        diameters = new Diameter[]{
                new Diameter(20, CENTIMETER),
                new Diameter(35, CENTIMETER),
                new Diameter(50, CENTIMETER),
                new Diameter(65, CENTIMETER)
        };
        centerMark = new CenterMark(WHITE, 7.307f, 4, true);
        isFieldTarget = true;
    }
}
