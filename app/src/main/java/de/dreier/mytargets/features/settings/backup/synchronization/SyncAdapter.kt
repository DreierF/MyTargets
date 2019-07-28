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

import android.accounts.Account
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.Context
import android.content.SyncResult
import android.os.Bundle
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.features.settings.backup.BackupException
import timber.log.Timber

/**
 * Define a sync adapter for the app.
 *
 * This class is instantiated in [SyncService], which also binds SyncAdapter to the system.
 * SyncAdapter should only be initialized in SyncService, never anywhere else.
 *
 * The system calls onPerformSync() via an RPC call through the IBinder object supplied by
 * SyncService.
 */
internal class SyncAdapter(context: Context, autoInitialize: Boolean) :
    AbstractThreadedSyncAdapter(context, autoInitialize) {

    /**
     * Called by the Android system in response to a request to run the sync adapter. The work
     * required to read data from the network, parse it, and store it in the content provider is
     * done here. Extending AbstractThreadedSyncAdapter ensures that all methods within SyncAdapter
     * run on a background thread. For this reason, blocking I/O and other long-running tasks can be
     * run *in situ*, and you don't have to set up a separate thread for them.
     *
     * This is where we actually perform any work required to perform a sync.
     * [AbstractThreadedSyncAdapter] guarantees that this will be called on a non-UI thread,
     * so it is safe to perform blocking I/O here.
     *
     * The syncResult argument allows you to pass information back to the method that triggered
     * the sync.
     */
    override fun onPerformSync(
        account: Account, extras: Bundle, authority: String,
        provider: ContentProviderClient, syncResult: SyncResult
    ) {
        Timber.i("Beginning network synchronization")
        val backup = SettingsManager.backupLocation.createBackup()
        try {
            backup.performBackup(context)
        } catch (e: BackupException) {
            Timber.w(e)
            syncResult.stats.numIoExceptions++
        }

        Timber.i("Network synchronization complete")
    }
}
