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
