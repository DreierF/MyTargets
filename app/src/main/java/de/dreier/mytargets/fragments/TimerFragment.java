/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.FragmentTimerBinding;
import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.utils.ToolbarUtils;

/**
 * Shows all passes of one round
 */
public class TimerFragment extends Fragment implements View.OnClickListener {

    public static final String SHOOTING_TIME = "shooting_time";
    private static final int WAIT_FOR_START = 0;
    private static final int PREPARATION = 1;
    private static final int SHOOTING = 2;
    private static final int FINISHED = 3;

    private int mWaitingTime;
    private int mShootingTime;
    private int mWarnTime;
    private int mCurStatus = WAIT_FOR_START;
    private CountDownTimer countdown;
    private MediaPlayer horn;
    private boolean mSound, mVibrate;
    private PowerManager.WakeLock wakeLock;
    private FragmentTimerBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer, container, false);
        binding.getRoot().setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ToolbarUtils.setSupportActionBar(this, binding.toolbar);
        ToolbarUtils.showHomeAsUp(this);
    }

    private void loadPreferenceValues() {
        mWaitingTime = SettingsManager.getTimerWaitTime();
        mShootingTime = SettingsManager.getTimerShootTime();
        mWarnTime = SettingsManager.getTimerWarnTime();
        mSound = SettingsManager.getTimerSoundEnabled();
        mVibrate = SettingsManager.getTimerVibrate();
    }

    @Override
    public void onResume() {
        super.onResume();
        PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        //noinspection deprecation
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "WakeLock");
        wakeLock.acquire();
        horn = MediaPlayer.create(getActivity(), R.raw.horn);

        loadPreferenceValues();
        changeStatus(mCurStatus);
    }

    @Override
    public void onPause() {
        super.onPause();
        wakeLock.release();
        horn.release();
        horn = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (countdown != null) {
            countdown.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        changeStatus(mCurStatus + 1);
    }

    private void changeStatus(int status) {
        if (countdown != null) {
            countdown.cancel();
        }
        mCurStatus = status;
        switch (status) {
            case WAIT_FOR_START:
                binding.getRoot().setBackgroundResource(R.color.timer_red);
                binding.timerStatus.setText(R.string.touch_to_start);
                binding.timerTime.setText("");
                break;
            case PREPARATION:
                playSignal(2);
                binding.timerStatus.setText(R.string.preparation);
                countdown = new CountDownTimer(mWaitingTime * 1000, 100) {
                    public void onTick(long millisUntilFinished) {
                        binding.timerTime.setText(String.valueOf(millisUntilFinished / 1000));
                    }

                    public void onFinish() {
                        changeStatus(SHOOTING);
                    }
                }.start();
                break;
            case SHOOTING:
                playSignal(1);
                binding.getRoot().setBackgroundResource(R.color.timer_green);
                binding.timerStatus.setText(R.string.shooting);
                countdown = new CountDownTimer(mShootingTime * 1000, 100) {
                    public void onTick(long millisUntilFinished) {
                        binding.timerTime.setText(String.valueOf(millisUntilFinished / 1000));
                        if (millisUntilFinished <= mWarnTime * 1000) {
                            binding.getRoot().setBackgroundResource(R.color.timer_orange);
                        }
                    }

                    public void onFinish() {
                        changeStatus(FINISHED);
                    }
                }.start();
                break;
            case FINISHED:
                playSignal(3);
                binding.getRoot().setBackgroundResource(R.color.timer_red);
                binding.timerTime.setText(R.string.stop);
                countdown = new CountDownTimer(6000, 100) {
                    public void onTick(long millisUntilFinished) {
                        binding.timerStatus.setText(String.valueOf(millisUntilFinished / 1000));
                    }

                    public void onFinish() {
                        getActivity().finish();
                        getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
                    }
                }.start();
                break;
            case FINISHED + 1:
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
                break;
        }
    }

    private void playSignal(final int n) {
        if (mSound) {
            playHorn(n);
        }
        if (mVibrate) {
            long[] pattern = new long[1 + n * 2];
            Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            pattern[0] = 150;
            for (int i = 0; i < n; i++) {
                pattern[i * 2 + 1] = 550;
                pattern[i * 2 + 2] = 800;
            }
            v.vibrate(pattern, -1);
        }
    }

    private void playHorn(final int n) {
        horn.start();
        horn.setOnCompletionListener(mp -> {
            if (n > 1) {
                playHorn(n - 1);
            }
        });
    }
}
