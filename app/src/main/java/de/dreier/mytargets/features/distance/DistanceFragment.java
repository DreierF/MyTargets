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

package de.dreier.mytargets.features.distance;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.parceler.Parcels;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.FragmentDistanceBinding;
import de.dreier.mytargets.shared.models.Dimension;

import static de.dreier.mytargets.base.activities.ItemSelectActivity.ITEM;

public class DistanceFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentDistanceBinding binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_distance, container, false);
        Dimension distance = Parcels.unwrap(getArguments().getParcelable(ITEM));
        binding.viewPager.setAdapter(new DistanceTabsFragmentPagerAdapter(getActivity(), distance));
        binding.slidingTabs.setupWithViewPager(binding.viewPager);

        // Select current unit
        int item = DistanceTabsFragmentPagerAdapter.UNITS.indexOf(distance.getUnit());
        binding.viewPager.setCurrentItem(item, false);
        return binding.getRoot();
    }
}
