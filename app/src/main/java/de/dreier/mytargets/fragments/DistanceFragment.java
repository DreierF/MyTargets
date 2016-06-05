/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import junit.framework.Assert;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.DistanceTabsFragmentPagerAdapter;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.utils.DistanceInputDialog;

import static de.dreier.mytargets.activities.ItemSelectActivity.ITEM;

public class DistanceFragment extends Fragment implements DistanceInputDialog.OnClickListener {

    @Bind(R.id.viewPager)
    ViewPager viewPager;

    @Bind(R.id.slidingTabs)
    TabLayout tabLayout;

    private SelectItemFragment.OnItemSelectedListener listener;
    private Dimension distance;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_distance, container, false);
        ButterKnife.bind(this, rootView);

        distance = Parcels.unwrap(getArguments().getParcelable(ITEM));

        viewPager.setAdapter(new DistanceTabsFragmentPagerAdapter(getActivity(), distance));
        selectUnit(distance.unit);
        tabLayout.setupWithViewPager(viewPager);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if (activity instanceof SelectItemFragment.OnItemSelectedListener) {
            this.listener = (SelectItemFragment.OnItemSelectedListener) activity;
        }
        Assert.assertNotNull(listener);
    }

    @OnClick(R.id.fab)
    public void onClick() {
        new DistanceInputDialog.Builder(getContext())
                .setUnit(getSelectedUnit().toString())
                .setOnClickListener(this)
                .show();
    }

    private Dimension.Unit getSelectedUnit() {
        int pos = viewPager.getCurrentItem();
        return DistanceTabsFragmentPagerAdapter.UNITS.get(pos);
    }

    private void selectUnit(Dimension.Unit unit) {
        int item = DistanceTabsFragmentPagerAdapter.UNITS.indexOf(unit);
        viewPager.setCurrentItem(item, false);
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
