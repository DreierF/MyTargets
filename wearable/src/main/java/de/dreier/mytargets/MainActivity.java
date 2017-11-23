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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.util.Date;

import de.dreier.mytargets.databinding.ActivityMainBinding;
import de.dreier.mytargets.shared.models.TrainingInfo;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.utils.WearWearableClient;

import static de.dreier.mytargets.utils.WearWearableClient.BROADCAST_TRAINING_TEMPLATE;
import static de.dreier.mytargets.utils.WearWearableClient.BROADCAST_TRAINING_UPDATED;

public class MainActivity extends WearableActivity {

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BROADCAST_TRAINING_TEMPLATE:
                    Training training = Parcels.unwrap(intent.getParcelableExtra(WearWearableClient.EXTRA_TRAINING));
                    setTraining(training);
                    binding.root.setClickable(false);
                    binding.wearableDrawerView.setVisibility(View.VISIBLE);

                    // Replaces the on click behaviour that open the (empty) drawer
                    LinearLayout peekView = ((LinearLayout) binding.primaryActionAdd.getParent());
                    ViewGroup peekContainer = ((ViewGroup) peekView.getParent());
                    peekContainer.setOnClickListener(view -> ApplicationInstance.wearableClient.sendCreateTraining(training));
                    binding.wearableDrawerView.getController().peekDrawer();
                    break;
                case BROADCAST_TRAINING_UPDATED:
                    TrainingInfo info = Parcels.unwrap(intent.getParcelableExtra(WearWearableClient.EXTRA_INFO));
                    setTrainingInfo(info);
                    binding.root.setClickable(true);
                    binding.root.setOnClickListener(v -> {
                        Intent i = new Intent(MainActivity.this, RoundActivity.class);
                        i.putExtra(RoundActivity.EXTRA_ROUND, Parcels.wrap(info.round));
                        startActivity(i);
                    });
                    binding.wearableDrawerView.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setAmbientEnabled();

        final IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_TRAINING_TEMPLATE);
        filter.addAction(BROADCAST_TRAINING_UPDATED);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter);
        ApplicationInstance.wearableClient.requestNewTrainingTemplate();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        binding.drawerLayout.setBackgroundResource(R.color.md_black_1000);
        binding.wearableDrawerView.setBackgroundResource(R.color.md_black_1000);
        binding.date.setTextColor(ContextCompat.getColor(this, R.color.md_white_1000));
        binding.icon.setVisibility(View.INVISIBLE);
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
        binding.drawerLayout.setBackgroundResource(R.color.md_wear_green_dark_background);
        binding.wearableDrawerView.setBackgroundResource(R.color.md_wear_green_lighter_ui_element);
        binding.date.setTextColor(ContextCompat.getColor(this, R.color.md_wear_green_lighter_ui_element));
        binding.icon.setVisibility(View.VISIBLE);
        binding.clock.time.setVisibility(View.GONE);
    }

    public void setTrainingInfo(TrainingInfo info) {
        setCommonTrainingInfo(info);
        binding.date.setText(R.string.today);
    }

    private void setTraining(Training training) {
        Round round = training.getRounds().get(0);
        TrainingInfo info = new TrainingInfo(training, round);
        setCommonTrainingInfo(info);
        binding.date.setText("");
    }

    private void setCommonTrainingInfo(TrainingInfo info) {
        binding.title.setText(info.title);
        binding.rounds.setText(info.getRoundDetails(this));
        binding.ends.setText(info.getEndDetails(this));
        binding.distance.setText(info.round.distance.toString());
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}
