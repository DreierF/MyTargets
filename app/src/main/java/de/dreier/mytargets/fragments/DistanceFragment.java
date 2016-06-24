/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import junit.framework.Assert;

import org.parceler.Parcels;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.DistanceTabsFragmentPagerAdapter;
import de.dreier.mytargets.databinding.FragmentDistanceBinding;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.utils.DistanceInputDialog;

import static de.dreier.mytargets.activities.ItemSelectActivity.ITEM;

public class DistanceFragment extends Fragment implements DistanceInputDialog.OnClickListener, View.OnClickListener {

    private SelectItemFragment.OnItemSelectedListener listener;
    private Dimension distance;
    private FragmentDistanceBinding binding;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_distance,  container, false);

        distance = Parcels.unwrap(getArguments().getParcelable(ITEM));

        binding.viewPager.setAdapter(new DistanceTabsFragmentPagerAdapter(getActivity(), distance));
        selectUnit(distance.unit);
        binding.slidingTabs.setupWithViewPager(binding.viewPager);
        binding.fabLayout.fab.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if (activity instanceof SelectItemFragment.OnItemSelectedListener) {
            this.listener = (SelectItemFragment.OnItemSelectedListener) activity;
        }
        Assert.assertNotNull(listener);
    }

    @Override
    public void onClick(View view) {
        new DistanceInputDialog.Builder(getContext())
                .setUnit(getSelectedUnit().toString())
                .setOnClickListener(this)
                .show();
    }

    private Dimension.Unit getSelectedUnit() {
        int pos = binding.viewPager.getCurrentItem();
        return DistanceTabsFragmentPagerAdapter.UNITS.get(pos);
    }

    private void selectUnit(Dimension.Unit unit) {
        int item = DistanceTabsFragmentPagerAdapter.UNITS.indexOf(unit);
        binding.viewPager.setCurrentItem(item, false);
    }

    @Override
    public void onOkClickListener(String input) {
        Dimension distance = this.distance;
        try {
            int distanceVal = Integer.parseInt(input.replaceAll("[^0-9]", ""));
            distance = new Dimension(distanceVal, getSelectedUnit());
        } catch (NumberFormatException e) {
            // leave distance as it is
        }
        listener.onItemSelected(Parcels.wrap(distance));
    }
}
