/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models.target;

import android.content.Context;
import de.dreier.mytargets.shared.R;

public class Vertical3Spot extends SpotBase {
    public static final int ID = 5;

    public Vertical3Spot(Context context) {
        super(context, ID, R.string.vertical_3_spot);
        face = new WA5RingTarget(context, true);
        facePositions = new int[][]{{500, 160},{500,500},{500,840}};
        faceRadius = 160;
    }
}
