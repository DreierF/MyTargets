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
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.fragments.EditableListFragmentBase;
import de.dreier.mytargets.databinding.ActivityMainBinding;
import de.dreier.mytargets.features.arrows.EditArrowListFragment;
import de.dreier.mytargets.features.bows.EditBowListFragment;
import de.dreier.mytargets.features.settings.ESettingsScreens;
import de.dreier.mytargets.features.settings.SettingsActivity;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.features.timer.TimerFragment;
import de.dreier.mytargets.features.training.overview.TrainingsFragment;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.Utils;

import static de.dreier.mytargets.features.settings.ESettingsScreens.SCOREBOARD;

/**
 * Shows the apps main screen, which contains a bottom navigation for switching between trainings,
 * bows and arrows, as well as an navigation drawer for hosting settings and the timer quick access.
 */
public class MainActivity extends AppCompatActivity {

    /*
     * TODO:
     * - Statistics does not refresh if all trainings are deleted
     * - FAB does not return after snackbar gets shown
     * - Help and feedback page
     * */

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    ActivityMainBinding binding;
    private ActionBarDrawerToggle drawerToggle;
    private IntentWrapper onDrawerClosePendingIntent = null;
    CountingIdlingResource espressoTestIdlingResource = new CountingIdlingResource("delayed_activity");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_CustomToolbar);
        super.onCreate(savedInstanceState);
        if (SettingsManager.shouldShowIntroActivity()) {
            SettingsManager.setShouldShowIntroActivity(false);
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(binding.toolbar);

        setupBottomNavigation();
        setupNavigationDrawer();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content_frame, new TrainingsFragment())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupNavigationHeaderView();
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            EditableListFragmentBase fragment = null;
            switch (item.getItemId()) {
                case R.id.action_arrows:
                    fragment = new EditArrowListFragment();
                    break;
                case R.id.action_bows:
                    fragment = new EditBowListFragment();
                    break;
                case R.id.action_trainings:
                    fragment = new TrainingsFragment();
                    break;
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
            return true;
        });
    }

    private void setupNavigationDrawer() {
        if (Utils.isLollipop()) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        binding.navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.nav_timer:
                            closeDrawerAndStart(TimerFragment.getIntent(false));
                            break;
                        case R.id.nav_settings:
                            closeDrawerAndStart(SettingsActivity.getIntent(ESettingsScreens.MAIN));
                            break;
                        case R.id.nav_help_and_feedback:
                            //TODO
                            break;
                    }
                    return false;
                });

        drawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (onDrawerClosePendingIntent != null) {
                    onDrawerClosePendingIntent.withContext(MainActivity.this).start();
                    onDrawerClosePendingIntent = null;
                    espressoTestIdlingResource.decrement();
                }
            }
        };
        binding.drawerLayout.addDrawerListener(drawerToggle);
    }

    private void setupNavigationHeaderView() {
        View headerLayout = binding.navigationView.getHeaderView(0);
        TextView userName = headerLayout.findViewById(R.id.username);
        TextView userDetails = headerLayout.findViewById(R.id.user_details);
        String profileFullName = SettingsManager.getProfileFullName();
        if (profileFullName.trim().isEmpty()) {
            userName.setText(R.string.click_to_enter_profile);
        } else {
            userName.setText(profileFullName);
        }
        userDetails.setText(SettingsManager.getProfileClub());
        headerLayout.setOnClickListener(view -> {
            closeDrawerAndStart(SettingsActivity.getIntent(SCOREBOARD));
        });
    }

    private void closeDrawerAndStart(IntentWrapper intent) {
        onDrawerClosePendingIntent = intent;
        espressoTestIdlingResource.increment();
        binding.drawerLayout.closeDrawers();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                binding.drawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * @return MainActvity's idling resource for Espresso testing
     */
    public CountingIdlingResource getEspressoIdlingResourceForMainActivity() {
        return espressoTestIdlingResource;
    }
}
