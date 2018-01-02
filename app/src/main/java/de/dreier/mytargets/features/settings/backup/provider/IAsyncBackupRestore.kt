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

package de.dreier.mytargets.features.settings.backup.provider

import android.app.Activity

import de.dreier.mytargets.features.settings.backup.BackupEntry

interface IAsyncBackupRestore {
    fun connect(activity: Activity, listener: ConnectionListener)

    fun getBackups(listener: OnLoadFinishedListener)

    fun restoreBackup(backup: BackupEntry, listener: BackupStatusListener)

    fun deleteBackup(backup: BackupEntry, listener: BackupStatusListener)

    fun stop()

    interface ConnectionListener {
        fun onConnected()

        fun onConnectionSuspended()
    }

    interface OnLoadFinishedListener {
        fun onLoadFinished(backupEntries: List<BackupEntry>)

        fun onError(message: String)
    }

    interface BackupStatusListener {
        fun onFinished()

        fun onError(message: String)
    }
}
