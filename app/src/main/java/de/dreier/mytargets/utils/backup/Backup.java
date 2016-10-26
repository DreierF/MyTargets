package de.dreier.mytargets.utils.backup;

import android.app.Activity;
import android.content.Intent;

import java.util.List;

public interface Backup {
    void start(Activity activity, OnLoadFinishedListener listener);
    void startBackup(BackupStatusListener listener);
    void onActivityResult(int requestCode, int resultCode, Intent data);
    void getBackups();
    void restoreBackup(BackupEntry backup, BackupStatusListener listener);
    void stop();


    interface OnLoadFinishedListener {
        void onLoadFinished(List<BackupEntry> backupEntries);
    }

    interface BackupStatusListener {
        void onStarted();
        void onFinished();
        void onError(String message);
    }
}
