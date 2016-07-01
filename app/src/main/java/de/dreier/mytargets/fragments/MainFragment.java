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
import de.dreier.mytargets.activities.StatisticsActivity;
import de.dreier.mytargets.adapters.MainTabsFragmentPagerAdapter;
import de.dreier.mytargets.databinding.FragmentMainBinding;
import de.dreier.mytargets.models.ETrainingType;
import de.dreier.mytargets.shared.models.db.Arrow;
import de.dreier.mytargets.managers.dao.TrainingDataSource;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.utils.FABMenu;

import static de.dreier.mytargets.utils.ToolbarUtils.setSupportActionBar;

public class MainFragment extends Fragment {

    private boolean showStatistics = false;

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
        showStatistics = !new TrainingDataSource().getAll().isEmpty();
    }

    @Override
    public boolean isFABExpandable() {
        return binding.viewPager.getCurrentItem() == 0;
    }

    @Override
    public void onFabClicked(int index) {
        switch (index) {
            case 0:
                int currentTab = binding.viewPager.getCurrentItem();
                if (currentTab == 1) {
                    startActivityAnimated(getActivity(),
                            SimpleFragmentActivityBase.EditBowActivity.class);
                } else if (currentTab == 2) {
                    startActivityAnimated(getActivity(),
                            SimpleFragmentActivityBase.EditArrowActivity.class);
                }
                break;
            case 1:
                startActivityAnimated(getActivity(),
                        SimpleFragmentActivityBase.EditTrainingActivity.class,
                        TRAINING_TYPE, ETrainingType.FREE_TRAINING.toString());
                break;
            case 2:
        getActivity().invalidateOptionsMenu();
                        SimpleFragmentActivityBase.EditTrainingActivity.class,
                        TRAINING_TYPE, ETrainingType.TRAINING_WITH_STANDARD_ROUND.toString());
                break;
        }
    }

    @Override
    public void onContentChanged(boolean empty, int stringRes) {
        for (int i = 0; i < MainFragment.stringRes.length; i++) {
            if (stringRes == MainFragment.stringRes[i]) {
                this.empty[i] = empty;
            }
        }
        fm.notifyContentChanged();
        onPageSelected(binding.viewPager.getCurrentItem());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_statistics:
                startActivity(new Intent(getContext(), StatisticsActivity.class));
                return true;
            case R.id.action_preferences:
                startActivity(new Intent(getContext(), SimpleFragmentActivityBase.SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
