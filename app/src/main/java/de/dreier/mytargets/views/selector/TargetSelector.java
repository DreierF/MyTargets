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
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.shared.models.Target;

public class TargetSelector extends ImageSelectorBase<Target> {

    public static final int TARGET_REQUEST_CODE = 12;

    public TargetSelector(Context context) {
        this(context, null);
    }

    public TargetSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTitle(R.string.target_face);
        defaultActivity = ItemSelectActivity.TargetActivity.class;
        requestCode = TARGET_REQUEST_CODE;
    }

}