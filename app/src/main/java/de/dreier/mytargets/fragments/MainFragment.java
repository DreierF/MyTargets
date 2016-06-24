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
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.parceler.Parcels;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase;
import de.dreier.mytargets.adapters.MainTabsFragmentPagerAdapter;
import de.dreier.mytargets.databinding.FragmentMainBinding;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.shared.models.Bow;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.utils.FABMenu;

import static de.dreier.mytargets.fragments.EditArrowFragment.ARROW_ID;
import static de.dreier.mytargets.fragments.EditBowFragment.BOW_ID;
import static de.dreier.mytargets.fragments.EditTrainingFragment.FREE_TRAINING;
import static de.dreier.mytargets.fragments.EditTrainingFragment.TRAINING_TYPE;
import static de.dreier.mytargets.fragments.EditTrainingFragment.TRAINING_WITH_STANDARD_ROUND;
import static de.dreier.mytargets.fragments.FragmentBase.ITEM_ID;
import static de.dreier.mytargets.utils.ActivityUtils.startActivityAnimated;
import static de.dreier.mytargets.utils.ToolbarUtils.setSupportActionBar;

public class MainFragment extends Fragment implements FragmentBase.ContentListener, ViewPager.OnPageChangeListener, FABMenu.Listener, FragmentBase.OnItemSelectedListener {

    private final static int[] stringRes = new int[3];

    static {
        stringRes[0] = R.string.new_training;
        stringRes[1] = R.string.new_bow;
        stringRes[2] = R.string.new_arrow;
    }

    private final boolean[] empty = new boolean[3];

    private FABMenu fm;
    private FragmentMainBinding binding;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);

        setSupportActionBar(this, binding.toolbar);
        setHasOptionsMenu(true);

        fm = new FABMenu(getContext(), binding.fabLayout, binding.overlayView);
        fm.setListener(this);
        fm.setFABItem(1, R.drawable.ic_trending_up_white_24dp, R.string.free_training);
        fm.setFABItem(2, R.drawable.ic_album_24dp, R.string.training_with_standard_round);

        MainTabsFragmentPagerAdapter adapter =
                new MainTabsFragmentPagerAdapter(getContext(), getChildFragmentManager());
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.addOnPageChangeListener(this);
        binding.slidingTabs.setupWithViewPager(binding.viewPager);

        if (savedInstanceState != null) {
            fm.onRestoreInstanceState(savedInstanceState);
        }

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_preferences) {
            startActivity(new Intent(getContext(), SimpleFragmentActivityBase.SettingsActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fm.reset();
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
                    startActivityAnimated(getActivity(), SimpleFragmentActivityBase.EditBowActivity.class);
                } else if (currentTab == 2) {
                    startActivityAnimated(getActivity(), SimpleFragmentActivityBase.EditArrowActivity.class);
                }
                break;
            case 1:
                startActivityAnimated(getActivity(), SimpleFragmentActivityBase.EditTrainingActivity.class,
                        TRAINING_TYPE, FREE_TRAINING);
                break;
            case 2:
                startActivityAnimated(getActivity(), SimpleFragmentActivityBase.EditTrainingActivity.class,
                        TRAINING_TYPE, TRAINING_WITH_STANDARD_ROUND);
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
    public void onItemSelected(Parcelable passedItem) {
        IIdProvider item = Parcels.unwrap(passedItem);
        if (item instanceof Arrow) {
            startActivityAnimated(getActivity(), SimpleFragmentActivityBase.EditArrowActivity.class, ARROW_ID,
                    item.getId());
        } else if (item instanceof Bow) {
            startActivityAnimated(getActivity(), SimpleFragmentActivityBase.EditBowActivity.class, BOW_ID,
                    item.getId());
        } else {
            startActivityAnimated(getActivity(), SimpleFragmentActivityBase.EditTrainingActivity.class, ITEM_ID,
                    item.getId());
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        fm.setFABHelperTitle(empty[position] ? stringRes[position] : 0);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        fm.onSaveInstanceState(outState);
    }
}
