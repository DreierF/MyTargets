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

package de.dreier.mytargets.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.parceler.Parcels;

import de.dreier.mytargets.shared.models.WindDirection;

import static de.dreier.mytargets.activities.ItemSelectActivity.ITEM;

public class WindDirectionListFragment extends SelectPureListItemFragmentBase<WindDirection> {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mAdapter.setList(WindDirection.getList(getContext()));
        WindDirection windDirection = Parcels.unwrap(getArguments().getParcelable(ITEM));
        selectItem(binding.recyclerView, windDirection);
        return binding.getRoot();
    }
}
