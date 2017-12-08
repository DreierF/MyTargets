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

package de.dreier.mytargets.features.settings.backup.synchronization;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import timber.log.Timber;

/**
 * Service to handle sync requests.
 * <p>
 * <p>This service is invoked in response to Intents with action android.content.SyncAdapter, and
 * returns a Binder connection to SyncAdapter.
 * <p>
 * <p>For performance, only one sync adapter will be initialized within this application's context.
 * <p>
 * <p>Note: The SyncService itself is not notified when a new sync occurs. It's role is to
 * manage the lifecycle of our {@link SyncAdapter} and provide a handle to said SyncAdapter to the
 * OS on request.
 */
public class SyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    @Nullable
    private static SyncAdapter sSyncAdapter = null;

    /**
     * Thread-safe constructor, creates static {@link SyncAdapter} instance.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Timber.i("Service created");
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    /**
     * Logging-only destructor.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.i("Service destroyed");
    }

    /**
     * Return Binder handle for IPC communication with {@link SyncAdapter}.
     * <p>
     * <p>New sync requests will be sent directly to the SyncAdapter using this channel.
     *
     * @param intent Calling intent
     * @return Binder handle for {@link SyncAdapter}
     */
    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
