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

    public Vegas3Spot(Context context) {
        super(context, 4, R.string.vegas_3_spot);
        face = new WA5RingTarget(context);
        facePositions = new int[][]{{240, 750}, {500, 250}, {760, 750}};
        faceRadius = 240;
    }
}
