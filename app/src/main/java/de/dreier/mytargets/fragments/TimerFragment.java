/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.SettingsManager;

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
    private View root;
    private TextView mStatusField, mTimeField;
    private int mCurStatus = WAIT_FOR_START;
    private CountDownTimer countdown;
    private MediaPlayer horn;
    private boolean mSound, mVibrate;
    private PowerManager.WakeLock wakeLock;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_timer, container, false);
        root.setOnClickListener(this);
        mStatusField = (TextView) root.findViewById(R.id.timer_status);
        mTimeField = (TextView) root.findViewById(R.id.timer_time);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                root.setBackgroundResource(R.color.timer_red);
                mStatusField.setText(R.string.touch_to_start);
                mTimeField.setText("");
                break;
            case PREPARATION:
                playSignal(2);
                mStatusField.setText(R.string.preparation);
                countdown = new CountDownTimer(mWaitingTime * 1000, 100) {
                    public void onTick(long millisUntilFinished) {
                        mTimeField.setText(String.valueOf(millisUntilFinished / 1000));
                    }

                    public void onFinish() {
                        changeStatus(SHOOTING);
                    }
                }.start();
                break;
            case SHOOTING:
                playSignal(1);
                root.setBackgroundResource(R.color.timer_green);
                mStatusField.setText(R.string.shooting);
                countdown = new CountDownTimer(mShootingTime * 1000, 100) {
                    public void onTick(long millisUntilFinished) {
                        mTimeField.setText(String.valueOf(millisUntilFinished / 1000));
                        if (millisUntilFinished <= mWarnTime * 1000) {
                            root.setBackgroundResource(R.color.timer_orange);
                        }
                    }

                    public void onFinish() {
                        changeStatus(FINISHED);
                    }
                }.start();
                break;
            case FINISHED:
                playSignal(3);
                root.setBackgroundResource(R.color.timer_red);
                mTimeField.setText(R.string.stop);
                countdown = new CountDownTimer(6000, 100) {
                    public void onTick(long millisUntilFinished) {
                        mStatusField.setText(String.valueOf(millisUntilFinished / 1000));
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
