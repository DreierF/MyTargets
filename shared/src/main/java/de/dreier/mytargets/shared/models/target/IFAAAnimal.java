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

public class IFAAAnimal extends TargetOvalBase {
    public static final int ID = 20;

    public IFAAAnimal() {
        super(ID, R.string.ifaa_animal);
        zones = 2;
        radius = new float[]{0, 500};
        colorFill = new int[]{Color.ORANGE, Color.LIGHTER_GRAY};
        colorStroke = new int[]{BLACK, BLACK, BLACK, Color.GRAY};
        strokeWidth = new int[]{5, 4, 3};
        zonePoints = new int[][]{{20, 18}, {16, 14}, {12, 10}};
        showAsX = new boolean[]{false};
    }
}
