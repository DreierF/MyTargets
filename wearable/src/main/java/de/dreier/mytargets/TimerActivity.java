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
package de.dreier.mytargets;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.drawer.WearableActionDrawer;
import android.support.wearable.view.drawer.WearableDrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.dreier.mytargets.shared.base.fragment.ETimerState;
import de.dreier.mytargets.shared.base.fragment.TimerFragmentBase;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Demonstrates use of Navigation and Action Drawers on Android Wear.
 */
public class TimerActivity extends WearableActivity implements
        WearableActionDrawer.OnMenuItemClickListener {

    private WearableDrawerLayout wearableDrawerLayout;
    private WearableActionDrawer wearableActionDrawer;
    private ImageView primaryActionPeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timer);
        setAmbientEnabled();

        // Initialize content to first planet.
        TimerFragment timerFragment = new TimerFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, timerFragment).commit();

        // Main Wearable Drawer Layout that wraps all content
        wearableDrawerLayout = (WearableDrawerLayout) findViewById(R.id.drawer_layout);

        // Bottom Action Drawer
        wearableActionDrawer = (WearableActionDrawer) findViewById(R.id.bottom_action_drawer);
        primaryActionPeek = (ImageView) findViewById(R.id.primaryActionPeek);

        wearableActionDrawer.setOnMenuItemClickListener(this);

        // Peeks action drawer on the bottom.
        wearableDrawerLayout.peekDrawer(Gravity.BOTTOM);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        final int itemId = menuItem.getItemId();

        switch (itemId) {
            case R.id.menu_stop:
                //TODO
                break;
            case R.id.menu_vibrate:
                //TODO
                return true;
            case R.id.menu_sound:
                //TODO
                return true;
            default:
                return false;
        }

        wearableDrawerLayout.closeDrawer(wearableActionDrawer);
        return true;
    }

    private void applyStatus(ETimerState status) {
        primaryActionPeek.setImageResource(status == ETimerState.WAIT_FOR_START
                ? R.drawable.ic_more_vert_white_24dp
                : R.drawable.ic_stop_white_24dp);
        primaryActionPeek.setOnClickListener(status == ETimerState.WAIT_FOR_START
                ? null : view -> finish());
    }

    /**
     * Fragment that appears in the "content_frame".
     */
    public static class TimerFragment extends TimerFragmentBase {

        private TextView time;
        private View startTimer;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_timer, container, false);
            startTimer = rootView.findViewById(R.id.start_timer);
            startTimer.setOnClickListener(this);
            time = (TextView) rootView.findViewById(R.id.timer_time);
            return rootView;
        }

        @Override
        public void getSettings() {
            soundEnabled = true;
            vibrate = true;
            timerWaitTime = 10;
            timerShootTime = 30;
            timerWarnTime = 15;
        }

        @Override
        public void applyTime(String text) {
            time.setText(text);
        }

        @Override
        protected void applyStatus(ETimerState status) {
            ((TimerActivity) getActivity()).applyStatus(status);
            startTimer.setVisibility(status == ETimerState.WAIT_FOR_START ? VISIBLE : GONE);
            time.setVisibility(status != ETimerState.WAIT_FOR_START ? VISIBLE : GONE);
            getView().setBackgroundResource(status.color);
        }
    }
}