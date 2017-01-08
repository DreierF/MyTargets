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
package de.dreier.mytargets.activities;

import android.content.Intent;
import android.os.Parcelable;

import de.dreier.mytargets.fragments.ListFragmentBase;

public abstract class ItemSelectActivity extends SimpleFragmentActivityBase
        implements ListFragmentBase.OnItemSelectedListener {

    public static final String ITEM = "item";
    public static final String INTENT = "intent";

    @Override
    public void onItemSelected(Parcelable item) {
        Intent data = new Intent();
        data.putExtra(ITEM, item);
        data.putExtra(INTENT, getIntent() != null ? getIntent().getExtras() : null);
        setResult(RESULT_OK, data);
    }

}
