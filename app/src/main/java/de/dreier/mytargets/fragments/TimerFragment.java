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
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.FragmentTimerBinding;
import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.utils.ToolbarUtils;

import static de.dreier.mytargets.fragments.TimerFragment.TimerState.EXIT;
import static de.dreier.mytargets.fragments.TimerFragment.TimerState.FINISHED;
import static de.dreier.mytargets.fragments.TimerFragment.TimerState.WAIT_FOR_START;

/**
 * Shows all passes of one round
 */
public class TimerFragment extends Fragment implements View.OnClickListener {

    public static final String SHOOTING_TIME = "shooting_time";
    private TimerState mCurStatus = WAIT_FOR_START;
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

        mSound = SettingsManager.getTimerSoundEnabled();
        mVibrate = SettingsManager.getTimerVibrate();
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
        changeStatus(mCurStatus.getNext());
    }

    private void changeStatus(TimerState status) {
        if (countdown != null) {
            countdown.cancel();
        }
        if (status == EXIT) {
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
            return;
        }
        mCurStatus = status;
        binding.getRoot().setBackgroundResource(status.color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getContext().getResources().getColor(status.color));
        }
        binding.timerStatus.setText(status.text);
        playSignal(status.signalCount);

        if (status == FINISHED) {
            binding.timerStatus.setText("");
            binding.timerTime.setText(R.string.stop);
            countdown = new CountDownTimer(6000, 100) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    changeStatus(status.getNext());
                }
            }.start();
        } else if (status.timeOffsetCallback == null) {
            binding.timerTime.setText("");
        } else {
            final int value = status.valueCallback.getValue();
            final int offset = status.timeOffsetCallback.getValue();
            countdown = new CountDownTimer((value - offset) * 1000, 100) {
                public void onTick(long millisUntilFinished) {
                    binding.timerTime.setText(
                            String.valueOf((millisUntilFinished / 1000) + offset));
                }

                public void onFinish() {
                    changeStatus(status.getNext());
                }
            }.start();
        }
    }

    private void playSignal(final int n) {
        if (n > 0) {
            if (mSound) {
                playHorn(n);
            }
            if (mVibrate) {
                long[] pattern = new long[1 + n * 2];
                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                pattern[0] = 150;
                for (int i = 0; i < n; i++) {
                    pattern[i * 2 + 1] = 400;
                    pattern[i * 2 + 2] = 750;
                }
                v.vibrate(pattern, -1);
            }
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

    enum TimerState {
        WAIT_FOR_START(R.color.timer_red, R.string.touch_to_start, 0, null, null),
        PREPARATION(R.color.timer_red, R.string.preparation, 2, SettingsManager::getTimerWaitTime,
                () -> 0),
        SHOOTING(R.color.timer_green, R.string.shooting, 1, SettingsManager::getTimerShootTime,
                SettingsManager::getTimerWarnTime),
        COUNTDOWN(R.color.timer_orange, R.string.shooting, 0, SettingsManager::getTimerWarnTime,
                () -> 0),
        FINISHED(R.color.timer_red, R.string.stop, 3, null, null),
        EXIT(R.color.timer_red, R.string.stop, 0, null, null);

        public int color;
        public int text;
        public int signalCount;
        public ValueCallback valueCallback;
        public ValueCallback timeOffsetCallback;

        TimerState(@ColorRes int color, @StringRes int text, int signalCount, ValueCallback valueCallback, ValueCallback timeOffsetCallback) {
            this.color = color;
            this.text = text;
            this.signalCount = signalCount;
            this.valueCallback = valueCallback;
            this.timeOffsetCallback = timeOffsetCallback;
        }

        public TimerState getNext() {
            switch (this) {
                case WAIT_FOR_START:
                    return PREPARATION;
                case PREPARATION:
                    return SHOOTING;
                case SHOOTING:
                    return COUNTDOWN;
                case COUNTDOWN:
                    return FINISHED;
                case FINISHED:
                    return EXIT;
            }
            return WAIT_FOR_START;
        }

        private interface ValueCallback {
            int getValue();
        }
    }
}
