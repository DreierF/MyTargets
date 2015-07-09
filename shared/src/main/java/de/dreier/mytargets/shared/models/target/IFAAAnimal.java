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

public class IFAAAnimal extends TargetOvalBase {
    public IFAAAnimal(Context context) {
        super(context, 20, R.string.ifaa_animal);
        zones = 2;
        radius = new int[]{0, 500};
        colorFill = new int[]{ORANGE, LIGHTER_GRAY};
        colorStroke = new int[]{BLACK, BLACK, BLACK, GRAY};
        strokeWidth = new int[]{5, 4, 3};
        zonePoints = new int[][]{{20, 18}};
        showAsX = new boolean[]{false};
    }
}
