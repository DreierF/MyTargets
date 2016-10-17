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

import java.util.Arrays;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.fragments.DistanceGridFragment;
import de.dreier.mytargets.fragments.ListFragmentBase;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Dimension.Unit;

import static de.dreier.mytargets.shared.models.Dimension.Unit.FEET;
import static de.dreier.mytargets.shared.models.Dimension.Unit.METER;
import static de.dreier.mytargets.shared.models.Dimension.Unit.YARDS;

public class DistanceTabsFragmentPagerAdapter extends FragmentPagerAdapter {

    public static final List<Unit> UNITS = Arrays.asList(METER, YARDS, FEET);

    private final Context context;
    private final ListFragmentBase[] fragments = new ListFragmentBase[3];

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
