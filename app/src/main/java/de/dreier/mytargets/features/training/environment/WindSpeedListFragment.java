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

package de.dreier.mytargets.features.training.environment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.parceler.Parcels;

import de.dreier.mytargets.base.fragments.SelectPureListItemFragmentBase;
import de.dreier.mytargets.shared.models.WindSpeed;

import static de.dreier.mytargets.base.activities.ItemSelectActivity.ITEM;

public class WindSpeedListFragment extends SelectPureListItemFragmentBase<WindSpeed> {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        adapter.setList(WindSpeed.Companion.getList(getContext()));
        WindSpeed windSpeed = Parcels.unwrap(getArguments().getParcelable(ITEM));
        selectItem(binding.recyclerView, windSpeed);
        return binding.getRoot();
    }
}
