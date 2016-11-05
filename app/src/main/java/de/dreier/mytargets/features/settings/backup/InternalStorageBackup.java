package de.dreier.mytargets.features.settings.backup;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;

import static de.dreier.mytargets.shared.SharedApplicationInstance.get;
import static de.dreier.mytargets.features.settings.backup.BackupUtils.getBackupName;

public class InternalStorageBackup implements Backup {
    private static final String FOLDER_NAME = "MyTargets";

    private Activity activity;
    private OnLoadFinishedListener listener;

    private static void createDirectory(File directory) throws IOException {
        //noinspection ResultOfMethodCallIgnored
        directory.mkdir();
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IOException(get(R.string.dir_not_created));
        }
    }

    @Override
    public void start(Activity activity, OnLoadFinishedListener listener) {
        this.activity = activity;
        this.listener = listener;
        getBackups();
    }

    @Override
    public void startBackup(BackupStatusListener listener) {
        try {
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
            Collections.sort(backups, (b1, b2) -> b2.getModifiedDate()
                    .compareTo(b1.getModifiedDate()));
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
}
