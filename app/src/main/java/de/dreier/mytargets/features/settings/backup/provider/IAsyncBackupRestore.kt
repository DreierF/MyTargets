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

package de.dreier.mytargets.features.settings.backup.provider

import android.content.Context
import android.content.Intent
import de.dreier.mytargets.features.settings.backup.BackupEntry

interface IAsyncBackupRestore {
    fun connect(
        context: Context,
        listener: ConnectionListener
    )

    fun getBackups(listener: OnLoadFinishedListener)

    fun restoreBackup(backup: BackupEntry, listener: BackupStatusListener)

    fun deleteBackup(backup: BackupEntry, listener: BackupStatusListener)

    fun onActivityResult(requestCode: Int, resultCode: Int, `data`: Intent?): Boolean

    interface ConnectionListener {
        fun onStartIntent(intent: Intent, code: Int)

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
