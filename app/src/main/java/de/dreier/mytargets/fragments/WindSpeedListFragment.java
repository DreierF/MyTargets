/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.dreier.mytargets.shared.models.WindSpeed;
import de.dreier.mytargets.utils.ToolbarUtils;

public class WindSpeedListFragment extends SelectPureListItemFragmentBase<WindSpeed> {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mAdapter.setList(WindSpeed.getList(getContext()));
        ToolbarUtils.showUpAsX(this);
        return binding.getRoot();
    }
}
