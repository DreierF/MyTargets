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
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.wearable.activity.WearableActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.parceler.Parcels;

import de.dreier.mytargets.databinding.ActivityTimerBinding;
import de.dreier.mytargets.databinding.FragmentTimerBinding;
import de.dreier.mytargets.shared.base.fragment.ETimerState;
import de.dreier.mytargets.shared.base.fragment.TimerFragmentBase;
import de.dreier.mytargets.shared.models.TimerSettings;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Demonstrates use of Navigation and Action Drawers on Android Wear.
 */
public class TimerActivity extends WearableActivity implements MenuItem.OnMenuItemClickListener {

    public static final String EXTRA_TIMER_SETTINGS = "timer_settings";

    private TimerFragment timerFragment;
    private ActivityTimerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timer);
        setAmbientEnabled();

        TimerSettings settings = Parcels
                .unwrap(getIntent().getParcelableExtra(EXTRA_TIMER_SETTINGS));
        timerFragment = TimerFragment.getInstance(settings);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, timerFragment).commit();

        binding.primaryActionPeek
                .setOnClickListener(v -> binding.bottomActionDrawer.getController().openDrawer());
        binding.bottomActionDrawer.setOnMenuItemClickListener(this);
        binding.bottomActionDrawer.getMenu().findItem(R.id.menu_vibrate)
                .setIcon(settings.vibrate
                        ? R.drawable.ic_vibration_white_24dp
                        : R.drawable.ic_vibration_off_white_24dp);
        binding.bottomActionDrawer.getMenu().findItem(R.id.menu_sound)
                .setIcon(settings.sound
                        ? R.drawable.ic_volume_up_white_24dp
                        : R.drawable.ic_volume_off_white_24dp);
        binding.bottomActionDrawer.getController().peekDrawer();
    }

    @Override
    public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_stop:
                finish();
                return true;
            case R.id.menu_vibrate:
                timerFragment.settings.vibrate = !timerFragment.settings.vibrate;
                menuItem.setIcon(timerFragment.settings.vibrate
                        ? R.drawable.ic_vibration_white_24dp
                        : R.drawable.ic_vibration_off_white_24dp);
                ApplicationInstance.wearableClient
                        .sendTimerSettingsFromLocal(timerFragment.settings);
                return true;
            case R.id.menu_sound:
                timerFragment.settings.sound = !timerFragment.settings.sound;
                menuItem.setIcon(timerFragment.settings.sound
                        ? R.drawable.ic_volume_up_white_24dp
                        : R.drawable.ic_volume_off_white_24dp);
                ApplicationInstance.wearableClient
                        .sendTimerSettingsFromLocal(timerFragment.settings);
                return true;
            default:
                return false;
        }
    }

    public void applyStatus(ETimerState status) {
        binding.primaryActionPeek.setImageResource(status == ETimerState.WAIT_FOR_START
                ? R.drawable.ic_more_vert_white_24dp
                : R.drawable.ic_stop_white_24dp);
        binding.primaryActionPeek.setOnClickListener(status == ETimerState.WAIT_FOR_START
                ? null : view -> finish());
    }

    /**
     * Fragment that appears in the "content_frame".
     */
    public static class TimerFragment extends TimerFragmentBase {

        private FragmentTimerBinding binding;

        @NonNull
        public static TimerFragment getInstance(TimerSettings settings) {
            TimerFragment timer = new TimerFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(ARG_TIMER_SETTINGS, Parcels.wrap(settings));
            timer.setArguments(bundle);
            return timer;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer, container, false);
            binding.startTimer.setOnClickListener(this);
            return binding.getRoot();
        }

        @Override
        public void applyTime(String text) {
            binding.timerTime.setText(text);
        }

        @Override
        protected void applyStatus(@NonNull ETimerState status) {
            if (getActivity() != null) {
                ((TimerActivity) getActivity()).applyStatus(status);
            }
            binding.startTimer.setVisibility(status == ETimerState.WAIT_FOR_START ? VISIBLE : GONE);
            binding.timerTime.setVisibility(status != ETimerState.WAIT_FOR_START ? VISIBLE : GONE);
            binding.getRoot().setBackgroundResource(status.color);
        }
    }
}
