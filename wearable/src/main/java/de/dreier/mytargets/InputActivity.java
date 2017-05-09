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

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.DelayedConfirmationView;
import android.view.View;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import de.dreier.mytargets.databinding.ActivityInputBinding;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.views.TargetViewBase;

public class InputActivity extends WearableActivity implements TargetViewBase.OnEndFinishedListener {

    public static final String EXTRA_ROUND = "round";
    private Round round;
    private ActivityInputBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_input);

        setAmbientEnabled();

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            round = Parcels.unwrap(intent.getParcelableExtra(EXTRA_ROUND));
        }

        // Workaround to avoid crash happening when setting invisible via xml layout
        binding.delayedConfirm.setVisibility(View.INVISIBLE);

        // Set up target view
        binding.target.setTarget(round.getTarget());
        binding.target.setEnd(new End(round.shotsPerEnd, 0));
        binding.target.setOnTargetSetListener(this);

        // Ensure Moto 360 is not cut off at the bottom
        binding.getRoot().setOnApplyWindowInsetsListener((v, insets) -> {
            int chinHeight = insets.getSystemWindowInsetBottom();
            binding.target.setChinHeight(chinHeight);
            return insets;
        });
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        binding.target.setBackgroundResource(R.color.md_black_1000);
        binding.target.setAmbientMode(true);
        binding.clock.time.setVisibility(View.VISIBLE);
        binding.clock.time.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date()));
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        binding.clock.time.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date()));
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
        binding.target.setBackgroundResource(R.color.md_wear_green_lighter_background);
        binding.target.setAmbientMode(false);
        binding.clock.time.setVisibility(View.GONE);
    }

    @Override
    public void onEndFinished(final List<Shot> shotList) {
        binding.delayedConfirm.setVisibility(View.VISIBLE);
        binding.delayedConfirm.setTotalTimeMs(2500);
        binding.delayedConfirm.start();
        binding.delayedConfirm
                .setListener(new DelayedConfirmationView.DelayedConfirmationListener() {
                    @Override
                    public void onTimerSelected(View view) {
                        binding.target.setEnd(new End(round.shotsPerEnd, 0));
                        binding.delayedConfirm.setVisibility(View.INVISIBLE);
                        binding.delayedConfirm.reset();
                    }

                    @Override
                    public void onTimerFinished(View view) {
                        Intent intent = new Intent(InputActivity.this, ConfirmationActivity.class);
                        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                                ConfirmationActivity.SUCCESS_ANIMATION);
                        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, getString(R.string.saved));
                        startActivity(intent);
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(200);
                        finish();
                        End end = new End(round.shotsPerEnd, 0);
                        end.setShots(shotList);
                        end.roundId = round.getId();
                        ApplicationInstance.wearableClient.sendEndUpdate(end);
                    }
                });
    }
}
