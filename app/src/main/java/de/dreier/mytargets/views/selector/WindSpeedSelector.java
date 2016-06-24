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
import de.dreier.mytargets.databinding.SelectorItemSimpleTextBinding;
import de.dreier.mytargets.models.WindSpeed;

public class WindSpeedSelector extends SelectorBase<WindSpeed> {

    private static final int WIND_SPEED_REQUEST_CODE = 4;

    public WindSpeedSelector(Context context) {
        this(context, null);
    }

    public WindSpeedSelector(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.selector_item_simple_text);
        defaultActivity = ItemSelectActivity.WindSpeedActivity.class;
        requestCode = WIND_SPEED_REQUEST_CODE;
    }

    @Override
    protected void bindView() {
        SelectorItemSimpleTextBinding binding = DataBindingUtil.bind(mView);
        binding.text.setText(item.name);
    }

    public void setItemId(long speed) {
        setItem(WindSpeed.getList(getContext()).get((int) speed));
    }
}
