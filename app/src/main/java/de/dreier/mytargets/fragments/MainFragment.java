/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase;
import de.dreier.mytargets.adapters.MainTabsFragmentPagerAdapter;
import de.dreier.mytargets.databinding.FragmentMainBinding;

import static de.dreier.mytargets.utils.ToolbarUtils.setSupportActionBar;

public class MainFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentMainBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);

        setSupportActionBar(this, binding.toolbar);
        setHasOptionsMenu(true);

        MainTabsFragmentPagerAdapter adapter =
                new MainTabsFragmentPagerAdapter(getContext(), getChildFragmentManager());
        binding.viewPager.setAdapter(adapter);
        binding.slidingTabs.setupWithViewPager(binding.viewPager);

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_preferences) {
            startActivity(
                    new Intent(getContext(), SimpleFragmentActivityBase.SettingsActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
