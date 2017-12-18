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

package de.dreier.mytargets.features.settings.backup.provider;

import android.app.Activity;

import java.util.List;

import de.dreier.mytargets.features.settings.backup.BackupEntry;

public interface IAsyncBackupRestore {
    void connect(Activity activity, ConnectionListener listener);

    void getBackups(OnLoadFinishedListener listener);

    void restoreBackup(BackupEntry backup, BackupStatusListener listener);

    void deleteBackup(BackupEntry backup, BackupStatusListener listener);

    void stop();

    interface ConnectionListener {
        void onConnected();

        void onConnectionSuspended();
    }

    interface OnLoadFinishedListener {
        void onLoadFinished(List<BackupEntry> backupEntries);

        void onError(String message);
    }

    interface BackupStatusListener {
        void onFinished();

        void onError(String message);
    }
}
