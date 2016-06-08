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
import de.dreier.mytargets.shared.models.Dimension;

public class SimpleDistanceSelector extends SelectorBase<Dimension> {

    public static final int SIMPLE_DISTANCE_REQUEST_CODE = 2;

    @Bind(android.R.id.text1)
    TextView distance;

    public SimpleDistanceSelector(Context context) {
        this(context, null);
    }

    public SimpleDistanceSelector(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.selector_item_simple_text);
        defaultActivity = ItemSelectActivity.DistanceActivity.class;
        requestCode = SIMPLE_DISTANCE_REQUEST_CODE;
    }

    @Override
    protected void bindView() {
        distance.setText(item.toString());
    }
}
