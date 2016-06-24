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
import de.dreier.mytargets.models.WindDirection;

public class WindDirectionSelector extends SelectorBase<WindDirection> {

    private static final int WIND_DIRECTION_REQUEST_CODE = 3;

    public WindDirectionSelector(Context context) {
        this(context, null);
    }

    public WindDirectionSelector(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.selector_item_simple_text);
        defaultActivity = ItemSelectActivity.WindDirectionActivity.class;
        requestCode = WIND_DIRECTION_REQUEST_CODE;
    }

    @Override
    protected void bindView() {
        SelectorItemSimpleTextBinding binding = DataBindingUtil.bind(mView);
        binding.text.setText(item.name);
    }

    public void setItemId(long direction) {
        setItem(WindDirection.getList(getContext()).get((int) direction));
    }
}
