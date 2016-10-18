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
import de.dreier.mytargets.shared.models.WindSpeed;

public class WindSpeedSelector extends ImageSelectorBase<WindSpeed> {

    private static final int WIND_SPEED_REQUEST_CODE = 4;

    public WindSpeedSelector(Context context) {
        this(context, null);
    }

    public WindSpeedSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        defaultActivity = ItemSelectActivity.WindSpeedActivity.class;
        requestCode = WIND_SPEED_REQUEST_CODE;
    }

    public void setItemId(long speed) {
        setItem(WindSpeed.getList(getContext()).get((int) speed));
    }

    @Override
    protected void bindView() {
        super.bindView();
        setTitle(R.string.wind_speed);
    }
}
