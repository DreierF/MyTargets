package de.dreier.mytargets.features.settings.backup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.List;

public interface IBackup {
    void start(Activity activity, OnLoadFinishedListener listener);
    void doBackupBlocking(Context context) throws BackupException;
    void onActivityResult(int requestCode, int resultCode, Intent data);
    void getBackups();
    void restoreBackup(BackupEntry backup, BackupStatusListener listener);
    void deleteBackup(BackupEntry backup, BackupStatusListener listener);
    void stop();

    interface OnLoadFinishedListener {
        void onLoadFinished(List<BackupEntry> backupEntries);
        void onError(String message);
    }

    interface BackupStatusListener {
        void onFinished();
        void onError(String message);
    }
}
