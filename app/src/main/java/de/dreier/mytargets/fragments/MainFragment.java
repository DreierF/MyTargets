/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.SimpleFragmentActivity;
import de.dreier.mytargets.adapters.MainTabsFragmentPagerAdapter;
import de.dreier.mytargets.utils.FABMenu;

import static de.dreier.mytargets.fragments.EditTrainingFragment.FREE_TRAINING;
import static de.dreier.mytargets.fragments.EditTrainingFragment.TRAINING_TYPE;
import static de.dreier.mytargets.fragments.EditTrainingFragment.TRAINING_WITH_STANDARD_ROUND;

public class MainFragment extends Fragment implements FragmentBase.ContentListener, ViewPager.OnPageChangeListener, FABMenu.Listener {

    private final boolean[] empty = new boolean[3];
    private final static int[] stringRes = new int[3];

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.viewPager)
    ViewPager viewPager;

    @Bind(R.id.slidingTabs)
    TabLayout tabLayout;

    private FABMenu fm;

    static {
        stringRes[0] = R.string.new_training;
        stringRes[1] = R.string.new_bow;
        stringRes[2] = R.string.new_arrow;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        fm = new FABMenu(getContext(), rootView);
        fm.setListener(this);
        fm.setFABItem(1, R.drawable.ic_trending_up_white_24dp, R.string.free_training);
        fm.setFABItem(2, R.drawable.ic_album_24dp, R.string.training_with_standard_round);

        MainTabsFragmentPagerAdapter adapter =
                new MainTabsFragmentPagerAdapter(getContext(), getChildFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        tabLayout.setupWithViewPager(viewPager);

        if (savedInstanceState != null) {
            fm.onRestoreInstanceState(savedInstanceState);
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_preferences) {
            startActivity(new Intent(getContext(), SimpleFragmentActivity.SettingsActivity.class));
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
    public void onDestroyView() {
        super.onDestroyView();
        fm.unbind();
        ButterKnife.unbind(this);
    }

    @Override
    public boolean isFABExpandable() {
        return viewPager.getCurrentItem() == 0;
    }

    @Override
    public void onFabClicked(int index) {
        switch (index) {
            case 0:
                int currentTab = viewPager.getCurrentItem();
                if (currentTab == 1) {
                    startActivityAnimated(SimpleFragmentActivity.EditBowActivity.class);
                } else if (currentTab == 2) {
                    startActivityAnimated(SimpleFragmentActivity.EditArrowActivity.class);
                }
                break;
            case 1:
                startActivityAnimated(SimpleFragmentActivity.EditTrainingActivity.class,
                        TRAINING_TYPE, FREE_TRAINING);
                break;
            case 2:
                startActivityAnimated(SimpleFragmentActivity.EditTrainingActivity.class,
                        TRAINING_TYPE, TRAINING_WITH_STANDARD_ROUND);
                break;
        }
    }

    private void startActivityAnimated(Class<?> activity) {
        Intent i = new Intent(getContext(), activity);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    private void startActivityAnimated(Class<?> activity, String key, int value) {
        Intent i = new Intent(getContext(), activity);
        i.putExtra(key, value);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    public void onContentChanged(boolean empty, int stringRes) {
        for (int i = 0; i < MainFragment.stringRes.length; i++) {
            if (stringRes == MainFragment.stringRes[i]) {
                this.empty[i] = empty;
            }
        }
        fm.notifyContentChanged();
        onPageSelected(viewPager.getCurrentItem());
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
