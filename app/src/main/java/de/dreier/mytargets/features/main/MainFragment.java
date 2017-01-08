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

package de.dreier.mytargets.features.main;

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

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import de.dreier.mytargets.R;
import de.dreier.mytargets.features.settings.SettingsActivity;
import de.dreier.mytargets.features.statistics.StatisticsActivity;
import de.dreier.mytargets.databinding.FragmentMainBinding;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Training;

import static de.dreier.mytargets.utils.ToolbarUtils.setSupportActionBar;

public class MainFragment extends Fragment {

    private boolean showStatistics = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentMainBinding binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_main, container, false);

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
        inflater.inflate(R.menu.statistics_settings, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_statistics).setVisible(showStatistics);
    }

    @Override
    public void onResume() {
        super.onResume();
        showStatistics = !Training.getAll().isEmpty();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_statistics:
                StatisticsActivity
                        .getIntent(Stream.of(Training.getAll())
                                .flatMap((training) -> Stream.of(training.getRounds()))
                                .map(Round::getId)

                                .collect(Collectors.toList())).withContext(this)
                        .start();
                return true;
            case R.id.action_preferences:
                startActivity(new Intent(getContext(), SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
