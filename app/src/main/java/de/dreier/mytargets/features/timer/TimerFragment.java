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

package de.dreier.mytargets.features.timer;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import org.parceler.Parcels;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.FragmentTimerBinding;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.shared.base.fragment.ETimerState;
import de.dreier.mytargets.shared.base.fragment.TimerFragmentBase;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.Utils;

import static de.dreier.mytargets.shared.base.fragment.ETimerState.FINISHED;

/**
 * Shows the archery timer
 */
public class TimerFragment extends TimerFragmentBase {

    private FragmentTimerBinding binding;

    @NonNull
    public static IntentWrapper getIntent(boolean exitAfterStop) {
        return new IntentWrapper(TimerActivity.class)
                .with(TimerFragmentBase.ARG_EXIT_AFTER_STOP, exitAfterStop)
                .with(TimerFragmentBase.ARG_TIMER_SETTINGS,
                        Parcels.wrap(SettingsManager.INSTANCE.getTimerSettings()));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbar);
        ToolbarUtils.showHomeAsUp(activity);
    }

    @Override
    public void applyTime(String text) {
        binding.timerTime.setText(text);
    }

    @Override
    protected void applyStatus(@NonNull ETimerState status) {
        binding.getRoot().setBackgroundResource(status.color);
        if (Utils.isLollipop() && getActivity() != null) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(status.color));
        }
        binding.timerStatus.setText(getStatusText(status));

        if (status == FINISHED) {
            binding.timerStatus.setText("");
        }
    }

    private int getStatusText(@NonNull ETimerState state) {
        switch (state) {
            case WAIT_FOR_START:
                return R.string.touch_to_start;
            case PREPARATION:
                return R.string.preparation;
            case SHOOTING:
                /* intended fallthrough */
            case COUNTDOWN:
                return R.string.shooting;
            case FINISHED:
                /* intended fallthrough */
            case EXIT:
                /* intended fallthrough */
            default:
                return R.string.stop;
        }
    }
}
