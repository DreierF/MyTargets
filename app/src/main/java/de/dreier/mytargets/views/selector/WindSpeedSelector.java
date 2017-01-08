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
import android.util.AttributeSet;

import de.dreier.mytargets.R;
import de.dreier.mytargets.features.training.environment.WindSpeedActivity;
import de.dreier.mytargets.shared.models.WindSpeed;

public class WindSpeedSelector extends ImageSelectorBase<WindSpeed> {

    private static final int WIND_SPEED_REQUEST_CODE = 4;

    public WindSpeedSelector(Context context) {
        this(context, null);
    }

    public WindSpeedSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        defaultActivity = WindSpeedActivity.class;
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
