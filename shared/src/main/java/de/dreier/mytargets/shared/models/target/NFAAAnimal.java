/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models.target;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.utils.Color;

import static android.graphics.Color.BLACK;

public class NFAAAnimal extends TargetOvalBase {
    public static final int ID = 21;

    public NFAAAnimal() {
        super(ID, R.string.nfaa_animal);
        zones = 3;
        radius = new float[]{81, 0, 500};
        colorFill = new int[]{Color.TURBO_YELLOW, Color.ORANGE, Color.LIGHTER_GRAY};
        colorStroke = new int[]{BLACK, BLACK, Color.GRAY};
        strokeWidth = new int[]{5, 4, 3};
        zonePoints = new int[][]{{21, 20, 18}, {17, 16, 14}, {13, 12, 10}};
        showAsX = new boolean[]{false};
    }
}
