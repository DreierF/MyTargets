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
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;

import org.parceler.Parcels;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.TimerSettings;

import static de.dreier.mytargets.shared.base.fragment.ETimerState.COUNTDOWN;
import static de.dreier.mytargets.shared.base.fragment.ETimerState.FINISHED;
import static de.dreier.mytargets.shared.base.fragment.ETimerState.PREPARATION;
import static de.dreier.mytargets.shared.base.fragment.ETimerState.SHOOTING;
import static de.dreier.mytargets.shared.base.fragment.ETimerState.WAIT_FOR_START;

public abstract class TimerFragmentBase extends Fragment implements View.OnClickListener {
    public static final String ARG_TIMER_SETTINGS = "timer_settings";
    public static final String ARG_EXIT_AFTER_STOP = "exit_after_stop";

    private ETimerState currentStatus = WAIT_FOR_START;
    private CountDownTimer countdown;
    private MediaPlayer horn;
    public TimerSettings settings;
    private boolean exitAfterStop = true;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        horn = MediaPlayer.create(context, R.raw.horn);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = Parcels.unwrap(getArguments().getParcelable(ARG_TIMER_SETTINGS));
        exitAfterStop = getArguments().getBoolean(ARG_EXIT_AFTER_STOP);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(this);
        changeStatus(currentStatus);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(horn.isPlaying()) {
            horn.stop();
        }
        horn.release();
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
            if (exitAfterStop) {
                getActivity().finish();
                return;
            } else {
                status = WAIT_FOR_START;
            }
        }
        ETimerState finalStatus = status;
        currentStatus = status;
        applyStatus(status);
        playSignal(status.signalCount);

        if (status == FINISHED) {
            applyTime(getString(R.string.stop));
            countdown = new CountDownTimer(6000, 100) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    changeStatus(finalStatus.getNext());
                }
            }.start();
        } else {
            if (status != PREPARATION && status != SHOOTING && status != COUNTDOWN) {
                applyTime("");
            } else {
                final int offset = getOffset(status);
                countdown = new CountDownTimer(getDuration(status) * 1000, 100) {
                    public void onTick(long millisUntilFinished) {
                        final String text = String
                                .valueOf((millisUntilFinished / 1000) + offset);
                        applyTime(text);
                    }

                    public void onFinish() {
                        changeStatus(finalStatus.getNext());
                    }
                }.start();
            }
        }
    }

    protected int getDuration(@NonNull ETimerState status) {
        switch (status) {
            case PREPARATION:
                return settings.waitTime;
            case SHOOTING:
                return settings.shootTime - settings.warnTime;
            case COUNTDOWN:
                return settings.warnTime;
            default:
                throw new IllegalArgumentException();
        }
    }

    protected int getOffset(ETimerState status) {
        if (status == SHOOTING) {
            return settings.warnTime;
        } else {
            return 0;
        }
    }

    private void playSignal(final int n) {
        if (n > 0) {
            if (settings.sound) {
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

    public abstract void applyTime(String text);

    protected abstract void applyStatus(ETimerState status);

}
