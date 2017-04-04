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

import org.parceler.Parcels;

import de.dreier.mytargets.databinding.ActivityMainBinding;
import de.dreier.mytargets.shared.models.NotificationInfo;

import static android.provider.DocumentsContract.EXTRA_INFO;

public class MainActivity extends Activity {

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationInfo info = Parcels.unwrap(intent.getExtras().getParcelable(EXTRA_INFO));
            setNotificationInfo(info);
        }
    };
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        final IntentFilter intentFilter = new IntentFilter(WearableListener.BROADCAST_TRAINING_UPDATED);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
        WearableListener.sendRequestInfo(this);
    }

    public void setNotificationInfo(NotificationInfo info) {
        binding.title.setText(info.title);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
}
