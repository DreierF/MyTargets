/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;

import de.dreier.mytargets.R;
import de.dreier.mytargets.fragments.DistanceGridFragment;
import de.dreier.mytargets.fragments.FragmentBase;
import de.dreier.mytargets.shared.models.Distance;

public class DistanceTabsFragmentPagerAdapter extends FragmentPagerAdapter {
    private final Context context;
    private final FragmentBase[] fragments = new FragmentBase[2];

    public DistanceTabsFragmentPagerAdapter(FragmentActivity context, Distance distance) {
        super(context.getSupportFragmentManager());
        this.context = context;
        fragments[0] = DistanceGridFragment.newInstance(distance, Distance.METER);
        fragments[1] = DistanceGridFragment.newInstance(distance, Distance.YARDS);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return context.getString(R.string.metric);
        } else {
            return context.getString(R.string.imperial);
        }
    }
}
