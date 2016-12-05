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

package de.dreier.mytargets.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Arrays;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.fragments.DistanceGridFragment;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Dimension.Unit;

import static de.dreier.mytargets.shared.models.Dimension.Unit.FEET;
import static de.dreier.mytargets.shared.models.Dimension.Unit.METER;
import static de.dreier.mytargets.shared.models.Dimension.Unit.YARDS;

public class DistanceTabsFragmentPagerAdapter extends FragmentPagerAdapter {

    public static final List<Unit> UNITS = Arrays.asList(METER, YARDS, FEET);

    private final Context context;
    private final DistanceGridFragment[] fragments = new DistanceGridFragment[3];

    public DistanceTabsFragmentPagerAdapter(FragmentActivity context, Dimension distance) {
        super(context.getSupportFragmentManager());
        this.context = context;
        fragments[0] = DistanceGridFragment.newInstance(distance, UNITS.get(0));
        fragments[1] = DistanceGridFragment.newInstance(distance, UNITS.get(1));
        fragments[2] = DistanceGridFragment.newInstance(distance, UNITS.get(2));
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
        switch (position) {
            case 0:
                return context.getString(R.string.metric);
            case 1:
                return context.getString(R.string.imperial);
            default:
                return context.getString(R.string.us);
        }
    }
}
