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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;

import org.parceler.Parcels;

import de.dreier.mytargets.databinding.ActivityMainBinding;
import de.dreier.mytargets.shared.models.NotificationInfo;
import de.dreier.mytargets.utils.WearWearableClient;
import timber.log.Timber;

public class MainActivity extends Activity {

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.d("onReceive() called with: context = [" + context + "], intent = [" + intent + "]");
            NotificationInfo info = Parcels.unwrap(intent.getExtras().getParcelable(WearWearableClient.EXTRA_INFO));
            setNotificationInfo(info);
        }
    };
    private ActivityMainBinding binding;
    private NotificationInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.drawerLayout.peekDrawer(Gravity.BOTTOM);

        binding.root.setOnClickListener(v -> {
            Intent i = new Intent(this, RoundActivity.class);
            i.putExtra(RoundActivity.EXTRA_ROUND, Parcels.wrap(info.round));
            i.putExtra(RoundActivity.EXTRA_TIMER, info.timerEnabled);
            startActivity(i);
        });

        binding.primaryActionAdd.setOnClickListener(view -> ApplicationInstance.wearableClient.createOnPhone());

        final IntentFilter intentFilter = new IntentFilter(WearWearableClient.BROADCAST_TRAINING_UPDATED);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
        ApplicationInstance.wearableClient.sendRequestInfo();
    }

    public void setNotificationInfo(NotificationInfo info) {
        this.info = info;
        binding.title.setText(info.title);
        String rounds = getResources().getQuantityString(R.plurals.rounds, info.roundCount, info.roundCount);
        Integer endCount = info.round.maxEndCount == null ? info.round.getEnds().size() : info.round.maxEndCount;
        String ends = getResources().getQuantityString(R.plurals.ends_arrow, info.round.shotsPerEnd, endCount, info.round.shotsPerEnd);
        binding.rounds.setText(rounds);
        binding.ends.setText(ends);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onDestroy();
    }
}
