/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import de.dreier.mytargets.R;
import de.dreier.mytargets.fragments.ArrowListFragment;
import de.dreier.mytargets.fragments.BowListFragment;
import de.dreier.mytargets.fragments.EditableListFragmentBase;
import de.dreier.mytargets.fragments.TrainingsFragment;

public class MainTabsFragmentPagerAdapter extends FragmentPagerAdapter {
    private final Context context;
    private final EditableListFragmentBase[] fragments = new EditableListFragmentBase[3];

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
