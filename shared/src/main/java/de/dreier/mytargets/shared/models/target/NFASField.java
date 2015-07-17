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

public class NFASField extends TargetOvalBase {
    public NFASField(Context context) {
        super(context, 22, R.string.nfas_field);
        zones = 3;
        radius = new float[]{81, 0, 500};
        colorFill = new int[]{TURBO_YELLOW, ORANGE, LIGHTER_GRAY};
        colorStroke = new int[]{BLACK, BLACK, GRAY};
        strokeWidth = new int[]{5, 4, 3};
        zonePoints = new int[][]{{24, 20, 16}};
        showAsX = new boolean[]{false};
    }
}
