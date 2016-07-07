/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.parceler.Parcels;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.DistanceTabsFragmentPagerAdapter;
import de.dreier.mytargets.databinding.FragmentDistanceBinding;
import de.dreier.mytargets.shared.models.Dimension;

import static de.dreier.mytargets.activities.ItemSelectActivity.ITEM;

public class DistanceFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentDistanceBinding binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_distance, container, false);
        Dimension distance = Parcels.unwrap(getArguments().getParcelable(ITEM));
        binding.viewPager.setAdapter(new DistanceTabsFragmentPagerAdapter(getActivity(), distance));
        binding.slidingTabs.setupWithViewPager(binding.viewPager);

        // Select current unit
        int item = DistanceTabsFragmentPagerAdapter.UNITS.indexOf(distance.unit);
        binding.viewPager.setCurrentItem(item, false);
        return binding.getRoot();
    }

}
