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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import de.dreier.mytargets.R;
import de.dreier.mytargets.fragments.ArrowListFragment;
import de.dreier.mytargets.fragments.BowListFragment;
import de.dreier.mytargets.fragments.ListFragmentBase;
import de.dreier.mytargets.fragments.TrainingsFragment;

public class MainTabsFragmentPagerAdapter extends FragmentPagerAdapter {
    private final Context context;
    private final ListFragmentBase[] fragments = new ListFragmentBase[3];

    public MainTabsFragmentPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.context = context;
        fragments[0] = new TrainingsFragment();
        fragments[1] = new BowListFragment();
        fragments[2] = new ArrowListFragment();
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
            return context.getString(R.string.training);
        } else if (position == 1) {
            return context.getString(R.string.bow);
        } else {
            return context.getString(R.string.arrow);
        }
    }
}
