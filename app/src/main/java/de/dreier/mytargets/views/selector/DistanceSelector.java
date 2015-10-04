/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views.selector;

import android.content.Context;
import android.util.AttributeSet;

import de.dreier.mytargets.R;

public class DistanceSelector extends DistanceSelectorBase {

    public DistanceSelector(Context context) {
        this(context, null);
    }

    public DistanceSelector(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.item_distance);
    }
}
