/*
 * Copyright (C) 2016 Florian Dreier
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

package de.dreier.mytargets.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import de.dreier.mytargets.R;
import de.dreier.mytargets.utils.Utils;
import icepick.Icepick;

/**
 * Generic fragment class used as base for most fragments.
 * Has Icepick build in to save state on orientation change
 * and animates activity when #finish gets called.
 */
public abstract class FragmentBase extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    protected void finish() {
        if (Utils.isLollipop()) {
            getActivity().finishAfterTransition();
        } else {
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
    }
}
