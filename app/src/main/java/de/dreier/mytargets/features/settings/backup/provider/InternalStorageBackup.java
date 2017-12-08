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
import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.features.settings.backup.BackupEntry;
import de.dreier.mytargets.features.settings.backup.BackupException;

import static de.dreier.mytargets.features.settings.backup.provider.BackupUtils.getBackupName;
import static de.dreier.mytargets.shared.SharedApplicationInstance.get;

public class InternalStorageBackup {
    private static final String FOLDER_NAME = "MyTargets";

    private static void createDirectory(@NonNull File directory) throws IOException {
        //noinspection ResultOfMethodCallIgnored
        directory.mkdir();
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IOException(get(R.string.dir_not_created));
        }
    }

    public static class AsyncRestore implements IAsyncBackupRestore {

        @Nullable
        private Activity activity;

        @Override
        public void connect(Activity activity, @NonNull ConnectionListener listener) {
            this.activity = activity;
            listener.onConnected();
        }

        @Override
        public void getBackups(@NonNull OnLoadFinishedListener listener) {
            File backupDir = new File(Environment.getExternalStorageDirectory(), FOLDER_NAME);
            if (backupDir.isDirectory()) {
                List<BackupEntry> backups = new ArrayList<>();
                for (File file : backupDir.listFiles()) {
                    if (isBackup(file)) {
                        final BackupEntry entry = new BackupEntry(file.getAbsolutePath(),
                                new Date(file.lastModified()),
                                file.length());
                        backups.add(entry);
                    }
                }
                Collections.sort(backups, (b1, b2) -> b2.getModifiedDate()
                        .compareTo(b1.getModifiedDate()));
                listener.onLoadFinished(backups);
            }
        }

        private boolean isBackup(@NonNull File file) {
            return file.isFile() && file.getName().contains("backup_") && file.getName()
                    .endsWith(".zip");
        }

        @Override
        public void restoreBackup(@NonNull BackupEntry backup, @NonNull BackupStatusListener listener) {
            File file = new File(backup.getFileId());
            try {
                BackupUtils.importZip(activity, new FileInputStream(file));
                listener.onFinished();
            } catch (IOException e) {
                listener.onError(e.getLocalizedMessage());
                e.printStackTrace();
            }
        }

        @Override
        public void deleteBackup(@NonNull BackupEntry backup, @NonNull BackupStatusListener listener) {
            if (new File(backup.getFileId()).delete()) {
                listener.onFinished();
            } else {
                listener.onError("Backup could not be deleted!");
            }
        }

        @Override
        public void stop() {
            activity = null;
        }
    }

    public static class Backup implements IBlockingBackup {

        @Override
        public void performBackup(@NonNull Context context) throws BackupException {
            try {
                File backupDir = new File(Environment.getExternalStorageDirectory(),
                        FOLDER_NAME);
                createDirectory(backupDir);
                final File zipFile = new File(backupDir, getBackupName());
                BackupUtils.zip(context, new FileOutputStream(zipFile));
            } catch (IOException e) {
                throw new BackupException(e.getLocalizedMessage(), e);
            }
        }
    }
}
