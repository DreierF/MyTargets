package de.dreier.mytargets.utils.backup;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.dreier.mytargets.managers.DatabaseManager;

import static de.dreier.mytargets.utils.backup.BackupUtils.createDirectory;
import static de.dreier.mytargets.utils.backup.BackupUtils.getBackupName;
import static pl.aprilapps.easyphotopicker.BundleKeys.FOLDER_NAME;

public class LocalDeviceBackup implements Backup {
    private Activity activity;
    private OnLoadFinishedListener listener;

    @Override
    public void start(Activity activity, OnLoadFinishedListener listener) {
        this.activity = activity;
        this.listener = listener;
        getBackups();
    }

    @Override
    public void startBackup(BackupStatusListener listener) {
        try {
            listener.onStarted();
            File backupDir = new File(Environment.getExternalStorageDirectory(), FOLDER_NAME);
            createDirectory(backupDir);
            final File zipFile = new File(backupDir, getBackupName());
            BackupUtils.zip(activity, new FileOutputStream(zipFile));
            listener.onFinished();
        } catch (IOException e) {
            listener.onError(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void getBackups() {
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
            listener.onLoadFinished(backups);
        }
    }

    private boolean isBackup(File file) {
        return file.isFile() && file.getName().startsWith("backup_") && file.getName()
                .endsWith(".zip");
    }

    @Override
    public void restoreBackup(BackupEntry backup, BackupStatusListener listener) {
        File file = new File(backup.getFileId());
        try {
            DatabaseManager.Import(activity, new FileInputStream(file));
        } catch (FileNotFoundException e) {
            listener.onError(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        activity = null;
    }
}
