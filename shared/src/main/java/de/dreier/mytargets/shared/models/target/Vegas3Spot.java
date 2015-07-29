/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models.target;

import android.content.Context;

import de.dreier.mytargets.shared.R;

public class Vegas3Spot extends SpotBase {

    public static final int ID = 4;

    public Vegas3Spot(Context context) {
        super(context, ID, R.string.vegas_3_spot);
        face = new WA5RingTarget(context);
        facePositions = new int[][]{{240, 750}, {500, 250}, {760, 750}};
        faceRadius = 240;
    }
}
