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
import de.dreier.mytargets.shared.models.WindDirection;

public class WindDirectionSelector extends ImageSelectorBase<WindDirection> {

    private static final int WIND_DIRECTION_REQUEST_CODE = 3;

    public WindDirectionSelector(Context context) {
        this(context, null);
    }

    public WindDirectionSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        defaultActivity = ItemSelectActivity.WindDirectionActivity.class;
        requestCode = WIND_DIRECTION_REQUEST_CODE;
    }

    public void setItemId(long direction) {
        setItem(WindDirection.getList(getContext()).get((int) direction));
    }

    @Override
    protected void bindView() {
        super.bindView();
        setTitle(R.string.wind_direction);
    }
}
