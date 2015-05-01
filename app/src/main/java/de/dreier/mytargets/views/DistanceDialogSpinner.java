/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views;

import android.content.Context;
import android.util.AttributeSet;

import de.dreier.mytargets.adapters.DistanceItemAdapter;


public class DistanceDialogSpinner extends DialogSpinner {

    public DistanceDialogSpinner(Context context) {
        super(context);
        init();
    }

    public DistanceDialogSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setAdapter(new DistanceItemAdapter(getContext()));
    }

    @Override
    public void setItemId(long id) {
        setAdapter(new DistanceItemAdapter(getContext(), (int) id));
        super.setItemId(id);
    }
}
