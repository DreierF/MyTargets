/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.targets;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.Dimension;

import static android.graphics.Color.WHITE;
import static de.dreier.mytargets.shared.utils.Color.DARK_GRAY;

public class Beursault extends TargetModelBase {

    public Beursault() {
        super(23, R.string.beursault);
        zones = new Zone[] {
                new Zone(31.089f, WHITE, DARK_GRAY, 27),
                new Zone(69.673f, WHITE, DARK_GRAY, 6),
                new Zone(98.578f, WHITE, DARK_GRAY, 6),
                new Zone(141.358f, WHITE, DARK_GRAY, 27),
                new Zone(231.017f, WHITE, DARK_GRAY, 6),
                new Zone(320.679f, WHITE, DARK_GRAY, 6),
                new Zone(410.339f, WHITE, DARK_GRAY, 6),
                new Zone(500, WHITE, DARK_GRAY, 27)
        };
        zonePoints = new int[][]{{4, 4, 3, 3, 2, 2, 1, 1}};
        //TODO inside out, see http://scores-sca.org/public/scores_rules.php?R=25&Shoot=108
        showAsX = new boolean[]{false};
        diameters = new Diameter[]{new Diameter(48, Dimension.CENTIMETER)};
        decoration = TargetDecoration.BEURSAULT;
        centerMark = new CenterMark(DARK_GRAY, 500f, 6, false);
    }
}
