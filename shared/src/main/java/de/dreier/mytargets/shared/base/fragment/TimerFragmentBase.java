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

package de.dreier.mytargets.shared.base.fragment;

import android.app.Fragment;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.view.View;

import org.parceler.Parcels;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.TimerSettings;

public abstract class TimerFragmentBase extends Fragment implements View.OnClickListener {
    public static final String ARG_TIMER_SETTINGS = "timer_settings";

    private ETimerState currentStatus = ETimerState.WAIT_FOR_START;
    private CountDownTimer countdown;
    private MediaPlayer horn;
    private PowerManager.WakeLock wakeLock;
    public TimerSettings settings;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = Parcels.unwrap(getArguments().getParcelable(ARG_TIMER_SETTINGS));
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setOnClickListener(this);
        PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        //noinspection deprecation
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "WakeLock");
        wakeLock.acquire();
        horn = MediaPlayer.create(getActivity(), R.raw.horn);

        changeStatus(currentStatus);
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
        changeStatus(currentStatus.getNext());
    }

    private void changeStatus(ETimerState status) {
        if (countdown != null) {
            countdown.cancel();
        }
        if (status == ETimerState.EXIT) {
            getActivity().finish();
            return;
        }
        currentStatus = status;
        applyStatus(status);
        playSignal(status.signalCount);

        if (status == ETimerState.FINISHED) {
            applyTime(getString(R.string.stop));
            countdown = new CountDownTimer(6000, 100) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    changeStatus(status.getNext());
                }
            }.start();
        } else if (status != ETimerState.PREPARATION && status != ETimerState.SHOOTING && status != ETimerState.COUNTDOWN) {
            applyTime("");
        } else {
            final int offset = getOffset(status);
            countdown = new CountDownTimer(getDuration(status) * 1000, 100) {
                public void onTick(long millisUntilFinished) {
                    final String text = String.valueOf((millisUntilFinished / 1000) + offset);
                    applyTime(text);
                }

                public void onFinish() {
                    changeStatus(status.getNext());
                }
            }.start();
        }
    }

    public abstract void applyTime(String text);

    protected abstract void applyStatus(ETimerState status);

    protected int getDuration(ETimerState status) {
        switch (status) {
            case PREPARATION:
                return settings.timerWaitTime;
            case SHOOTING:
                return settings.timerShootTime - settings.timerWarnTime;
            case COUNTDOWN:
                return settings.timerWarnTime;
            default:
                throw new IllegalArgumentException();
        }
    }

    protected int getOffset(ETimerState status) {
        if (status == ETimerState.SHOOTING) {
            return settings.timerWarnTime;
        } else {
            return 0;
        }
    }

    private void playSignal(final int n) {
        if (n > 0) {
            if (settings.soundEnabled) {
                playHorn(n);
            }
            if (settings.vibrate) {
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
}
