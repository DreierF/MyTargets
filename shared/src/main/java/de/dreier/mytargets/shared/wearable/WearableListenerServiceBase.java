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

package de.dreier.mytargets.shared.wearable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.WearableListenerService;

public class WearableListenerServiceBase extends WearableListenerService implements GoogleApiClient.ConnectionCallbacks {
    private static final String BROADCAST_TIMER_ENABLED = "timer_enabled";
    private static final String EXTRA_TIMER_ENABLED = "timer_enabled";

    public static void sendTimerEnabled(Context context, boolean enabled) {
        Intent intent = new Intent(BROADCAST_TIMER_ENABLED);
        intent.putExtra(EXTRA_TIMER_ENABLED, enabled);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
