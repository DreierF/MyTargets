/*
 * Copyright (C) 2018 Florian Dreier
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

package de.dreier.mytargets.features.settings.backup.synchronization

import android.app.Service
import android.content.Intent
import android.os.IBinder

import timber.log.Timber

/**
 * Service to handle sync requests.
 *
 * This service is invoked in response to Intents with action android.content.SyncAdapter, and
 * returns a Binder connection to SyncAdapter.
 *
 * For performance, only one sync adapter will be initialized within this application's context.
 *
 * Note: The SyncService itself is not notified when a new sync occurs. It's role is to
 * manage the lifecycle of our [SyncAdapter] and provide a handle to said SyncAdapter to the
 * OS on request.
 */
class SyncService : Service() {

    /**
     * Thread-safe constructor, creates static [SyncAdapter] instance.
     */
    override fun onCreate() {
        super.onCreate()
        Timber.i("Service created")
        synchronized(syncAdapterLock) {
            if (syncAdapter == null) {
                syncAdapter = SyncAdapter(applicationContext, true)
            }
        }
    }

    /**
     * Logging-only destructor.
     */
    override fun onDestroy() {
        super.onDestroy()
        Timber.i("Sync Service destroyed")
    }

    /**
     * Return Binder handle for IPC communication with [SyncAdapter].
     *
     * New sync requests will be sent directly to the SyncAdapter using this channel.
     *
     * @param intent Calling intent
     * @return Binder handle for [SyncAdapter]
     */
    override fun onBind(intent: Intent): IBinder? {
        return syncAdapter!!.syncAdapterBinder
    }

    companion object {
        private val syncAdapterLock = Any()
        private var syncAdapter: SyncAdapter? = null
    }
}
