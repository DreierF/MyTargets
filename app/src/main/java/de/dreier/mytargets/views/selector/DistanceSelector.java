/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views.selector;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.databinding.SelectorItemDistanceBinding;
import de.dreier.mytargets.shared.models.Dimension;

public class DistanceSelector extends SelectorBase<Dimension> {

    public static final int DISTANCE_REQUEST_CODE = 1;
    private SelectorItemDistanceBinding binding;

    public DistanceSelector(Context context) {
        this(context, null);
    }

    public DistanceSelector(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.selector_item_distance);
        defaultActivity = ItemSelectActivity.DistanceActivity.class;
        requestCode = DISTANCE_REQUEST_CODE;
        binding = DataBindingUtil.bind(mView);
    }

    @Override
    protected void bindView() {
        binding.distance.setText(item.toString());
    }
}
