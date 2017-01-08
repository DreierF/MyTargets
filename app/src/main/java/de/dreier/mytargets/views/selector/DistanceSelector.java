/*
 * Copyright (C) 2017 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.views.selector;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;

import de.dreier.mytargets.R;
import de.dreier.mytargets.features.distance.DistanceActivity;
import de.dreier.mytargets.databinding.SelectorItemDistanceBinding;
import de.dreier.mytargets.shared.models.Dimension;

public class DistanceSelector extends SelectorBase<Dimension> {

    public static final int DISTANCE_REQUEST_CODE = 1;

    public DistanceSelector(Context context) {
        this(context, null);
    }

    public DistanceSelector(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.selector_item_distance);
        defaultActivity = DistanceActivity.class;
        requestCode = DISTANCE_REQUEST_CODE;
    }

    @Override
    protected void bindView() {
        SelectorItemDistanceBinding binding = DataBindingUtil.bind(view);
        binding.distanceValue.setText(item.toString());
    }
}
