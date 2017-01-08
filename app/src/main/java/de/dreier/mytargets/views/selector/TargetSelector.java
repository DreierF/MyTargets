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
import de.dreier.mytargets.features.training.target.TargetActivity;
import de.dreier.mytargets.features.training.target.TargetListFragment;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.utils.IntentWrapper;

public class TargetSelector extends ImageSelectorBase<Target> {

    public static final int TARGET_REQUEST_CODE = 12;
    private TargetListFragment.EFixedType fixedType = TargetListFragment.EFixedType.NONE;

    public TargetSelector(Context context) {
        this(context, null);
    }

    public TargetSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        defaultActivity = TargetActivity.class;
        requestCode = TARGET_REQUEST_CODE;
    }

    public void setFixedType(TargetListFragment.EFixedType fixedType) {
        this.fixedType = fixedType;
    }

    @Override
    protected IntentWrapper getDefaultIntent() {
        IntentWrapper i = super.getDefaultIntent();
        i.with(TargetListFragment.FIXED_TYPE, fixedType.name());
        return i;
    }

    @Override
    protected void bindView() {
        super.bindView();
        setTitle(R.string.target_face);
    }
}