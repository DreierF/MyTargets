package de.dreier.mytargets.utils.backup;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.drive.query.SortOrder;
import com.google.android.gms.drive.query.SortableField;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import de.dreier.mytargets.managers.DatabaseManager;

import static android.app.Activity.RESULT_OK;

public class GoogleDriveBackup implements Backup,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "GoogleDriveBackup";

    /**
     * Request code for auto Google Play Services error resolution.
     */
    private static final int REQUEST_CODE_RESOLUTION = 1;

    private GoogleApiClient googleApiClient;
    private Activity activity;
    private OnLoadFinishedListener listener;

    @Override
    public void start(Activity activity, OnLoadFinishedListener listener) {
        this.activity = activity;
        this.listener = listener;
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(activity)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_APPFOLDER) // required for App Folder sample
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        googleApiClient.connect();
    }

    @Override
    public void startBackup(BackupStatusListener listener) {
        Drive.DriveApi.newDriveContents(googleApiClient)
                .setResultCallback(result -> {
                    if (!result.getStatus().isSuccess()) {
                        listener.onError(result.getStatus().getStatusMessage());
                        return;
                    }
                    final DriveContents driveContents = result.getDriveContents();

                    // Perform I/O off the UI thread.
                    new Thread() {
                        @Override
                        public void run() {
                            // write content to DriveContents
                            OutputStream outputStream = driveContents.getOutputStream();

                            try {
                                BackupUtils.zip(activity, outputStream);
                            } catch (IOException e) {
                                listener.onError(e.getLocalizedMessage());
                                e.printStackTrace();
                                return;
                            }

                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle(BackupUtils.getBackupName())
                                    .setMimeType("mytargets/zip")
                                    .build();

                            // create a file in selected folder
                            Drive.DriveApi.getAppFolder(googleApiClient)
                                    .createFile(googleApiClient, changeSet, driveContents)
                                    .setResultCallback(
                                            result1 -> {
                                                if (!result1.getStatus().isSuccess()) {
                                                    Log.d(TAG, "Error while trying to create the file");
                                                    listener.onError(result1.getStatus().getStatusMessage());
                                                    return;
                                                }
                                                listener.onFinished();
                                            });
                        }
                    }.start();
                });
    }

    /**
     * Called when {@code mGoogleApiClient} is connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "GoogleApiClient connected");
        getBackups();
    }

    /**
     * Called when {@code mGoogleApiClient} is disconnected.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    /**
     * Called when {@code mGoogleApiClient} is trying to connect but failed.
     * Handle {@code result.getResolution()} if there is a resolution is
     * available.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance()
                    .getErrorDialog(activity, result.getErrorCode(), 0)
                    .show();
            return;
        }
        try {
            result.startResolutionForResult(activity, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_RESOLUTION:
                if (resultCode == RESULT_OK) {
                    googleApiClient.connect();
                }
                break;
        }
    }

    @Override
    public void getBackups() {
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.MIME_TYPE, "mytargets/zip"))
                .addFilter(Filters.eq(SearchableField.TRASHED, false))
                .setSortOrder(new SortOrder.Builder()
                        .addSortDescending(SortableField.MODIFIED_DATE).build())
                .build();
        Drive.DriveApi.getAppFolder(googleApiClient).queryChildren(googleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {

                    private ArrayList<BackupEntry> backupsArray = new ArrayList<>();

                    @Override
                    public void onResult(@NonNull DriveApi.MetadataBufferResult result) {
                        MetadataBuffer buffer = result.getMetadataBuffer();
                        int size = buffer.getCount();
                        for (int i = 0; i < size; i++) {
                            Metadata metadata = buffer.get(i);
                            DriveId driveId = metadata.getDriveId();
                            long backupSize = metadata.getFileSize();
                            backupsArray.add(new BackupEntry(driveId.encodeToString(),
                                    metadata.getModifiedDate(), backupSize));
                        }
                        listener.onLoadFinished(backupsArray);
                        buffer.release();

                    }
                });
    }

    /**
     * Restores the given backup and restarts the app if the restore was successful.
     * */
    @Override
    public void restoreBackup(BackupEntry backup, BackupStatusListener listener) {
        DriveId.decodeFromString(backup.getFileId())
                .asDriveFile()
                .open(googleApiClient, DriveFile.MODE_READ_ONLY, null)
                .setResultCallback(result -> {
                    if (!result.getStatus().isSuccess()) {
                        listener.onError(result.getStatus().getStatusMessage());
                        return;
                    }

                    // DriveContents object contains pointers to the actual byte stream
                    DriveContents contents = result.getDriveContents();
                    InputStream input = contents.getInputStream();
                    try {
                        DatabaseManager.Import(activity, input);
                        listener.onFinished();
                    } catch (IOException e) {
                        e.printStackTrace();
                        listener.onError(e.getLocalizedMessage());
                    } finally {
                        safeCloseClosable(input);
                    }
                });
    }

    @Override
    public void deleteBackup(BackupEntry backup, BackupStatusListener listener) {
        DriveId.decodeFromString(backup.getFileId())
                .asDriveFile()
                .delete(googleApiClient)
                .setResultCallback(result -> {
                    if (result.getStatus().isSuccess()) {
                        listener.onFinished();
                    } else {
                        listener.onError(result.getStatus().getStatusMessage());
                    }
                });
    }

    @Override
    public void stop() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
            googleApiClient = null;
        }
        activity = null;
    }

    private void safeCloseClosable(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}