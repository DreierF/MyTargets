/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models.target;

import android.content.Context;

import de.dreier.mytargets.shared.R;

public class WA3Ring3Spot extends SpotBase {
    public static final int ID = 6;

    public WA3Ring3Spot(Context context) {
        super(context, ID, R.string.wa_3_ring_3_spot);
        face = new WA3RingTarget(context);
        facePositions = new int[][]{{240, 750}, {500, 250}, {760, 750}};
        faceRadius = 240;
    }
}
