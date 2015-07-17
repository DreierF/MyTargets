/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models.target;

import android.content.Context;

import de.dreier.mytargets.shared.R;

import static android.graphics.Color.BLACK;

public class NFAAAnimal extends TargetOvalBase {
    public NFAAAnimal(Context context) {
        super(context, 21, R.string.nfaa_animal);
        zones = 3;
        radius = new float[]{81, 0, 500};
        colorFill = new int[]{TURBO_YELLOW, ORANGE, LIGHTER_GRAY};
        colorStroke = new int[]{BLACK, BLACK, GRAY};
        strokeWidth = new int[]{5, 4, 3};
        zonePoints = new int[][]{{21, 20, 18}};
        showAsX = new boolean[]{false};
    }
}
