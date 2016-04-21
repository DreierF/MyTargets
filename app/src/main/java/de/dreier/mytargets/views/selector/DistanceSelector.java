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

import butterknife.Bind;
import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.shared.models.Distance;

public class DistanceSelector extends SelectorBase<Distance> {

    @Bind(android.R.id.text1)
    TextView distance;

    public DistanceSelector(Context context) {
        this(context, null);
    }

    public DistanceSelector(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.selector_item_distance);
        setOnClickActivity(ItemSelectActivity.DistanceActivity.class);
    }

    @Override
    protected void bindView() {
        distance.setText(item.toString(getContext()));
    }
}
