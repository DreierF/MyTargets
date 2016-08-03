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

class Beursault extends TargetModelBase {

    public Beursault() {
        super(23, R.string.beursault);
        zones = new Zone[] {
                new Zone(31.089f, WHITE, DARK_GRAY, 27, false),
                new Zone(69.673f, WHITE, DARK_GRAY, 6, false),
                new Zone(98.578f, WHITE, DARK_GRAY, 6, false),
                new Zone(141.358f, WHITE, DARK_GRAY, 27, false),
                new Zone(231.017f, WHITE, DARK_GRAY, 6, false),
                new Zone(320.679f, WHITE, DARK_GRAY, 6, false),
                new Zone(410.339f, WHITE, DARK_GRAY, 6, false),
                new Zone(500, WHITE, DARK_GRAY, 27, false)
        };
        scoringStyles = new ScoringStyle[]{
                new ScoringStyle(false, 4, 4, 3, 3, 2, 2, 1, 1)
        };
        diameters = new Dimension[]{
                new Dimension(48, CENTIMETER)
        };
        decoration = TargetDecoration.BEURSAULT;
        centerMark = new CenterMark(DARK_GRAY, 500f, 6, false);
    }
}
