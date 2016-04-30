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

public class SimpleDistanceSelector extends SelectorBase<Distance> {

    @Bind(android.R.id.text1)
    TextView distance;

    public SimpleDistanceSelector(Context context) {
        this(context, null);
    }

    public SimpleDistanceSelector(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.selector_item_simple_text);
        setOnClickActivity(ItemSelectActivity.DistanceActivity.class);
    }

    @Override
    protected void bindView() {
        distance.setText(item.toString(getContext()));
    }
}
