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
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

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

public class ExternalStorageBackup {
    private static final String FOLDER_NAME = "MyTargets";

    private static void createDirectory(File directory) throws IOException {
        //noinspection ResultOfMethodCallIgnored
        directory.mkdir();
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IOException(get(R.string.dir_not_created));
        }
    }

    public static File getMicroSdCardPath() {
        String strSDCardPath = System.getenv("SECONDARY_STORAGE");

        if ((strSDCardPath == null) || (strSDCardPath.length() == 0)) {
            strSDCardPath = System.getenv("EXTERNAL_SDCARD_STORAGE");
        }

        //If may get a full path that is not the right one, even if we don't have the SD Card there.
        //We just need the "/mnt/extSdCard/" i.e and check if it's writable
        if (strSDCardPath != null) {
            if (strSDCardPath.contains(":")) {
                strSDCardPath = strSDCardPath.substring(0, strSDCardPath.indexOf(":"));
            }
            Log.d("External", "getMicroSdCardPath: " + strSDCardPath);
            File externalFilePath = new File(strSDCardPath);

            if (externalFilePath.exists() && externalFilePath.canWrite()) {
                Log.d("External", "getMicroSdCardPath: " + externalFilePath.getAbsolutePath());
                //do what you need here
                return externalFilePath;
            }
        }
        return null;
    }

    @NonNull
    private static File getBackupFolder() {
        return new File(getMicroSdCardPath(), FOLDER_NAME);
    }

    public static class AsyncRestore implements IAsyncBackupRestore {
        private Activity activity;

        @Override
        public void connect(Activity activity, ConnectionListener listener) {
            this.activity = activity;
            listener.onConnected();
        }

        @Override
        public void getBackups(OnLoadFinishedListener listener) {
            File backupDir = getBackupFolder();
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

        private boolean isBackup(File file) {
            return file.isFile() && file.getName().contains("backup_") && file.getName()
                    .endsWith(".zip");
        }

        @Override
        public void restoreBackup(BackupEntry backup, BackupStatusListener listener) {
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
        public void deleteBackup(BackupEntry backup, BackupStatusListener listener) {
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

        @Override
        public String getBackupFolderString() {
            return getBackupFolder().getPath();
        }
    }

    public static class Backup implements IBlockingBackup {
        @Override
        public void performBackup(Context context) throws BackupException {
            try {
                File backupDir = getBackupFolder();
                createDirectory(backupDir);
                final File zipFile = new File(backupDir, getBackupName());
                BackupUtils.zip(context, new FileOutputStream(zipFile));
            } catch (IOException e) {
                throw new BackupException(e.getLocalizedMessage(), e);
            }
        }
    }
}
