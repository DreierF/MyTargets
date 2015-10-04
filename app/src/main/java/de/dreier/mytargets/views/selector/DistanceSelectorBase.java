/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views.selector;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.shared.models.Distance;

public class DistanceSelectorBase extends SelectorBase<Distance> {

    public DistanceSelectorBase(Context context, AttributeSet attrs, int resId) {
        super(context, attrs, resId);
        setOnClickActivity(ItemSelectActivity.DistanceActivity.class);
    }

    @Override
    protected void bindView() {
        TextView name = (TextView) mView.findViewById(android.R.id.text1);
        name.setText(item.toString(getContext()));
    }
}
