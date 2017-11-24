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
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder.DriveFileResult;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.drive.query.SortOrder;
import com.google.android.gms.drive.query.SortableField;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import de.dreier.mytargets.features.settings.backup.BackupEntry;
import de.dreier.mytargets.features.settings.backup.BackupException;

public class GoogleDriveBackup {
    private static final String TAG = "GoogleDriveBackup";
    private static final String MYTARGETS_MIME_TYPE = "application/zip";

    public static class AsyncRestore implements IAsyncBackupRestore {

        /**
         * Request code for auto Google Play Services error resolution.
         */
        public static final int REQUEST_CODE_RESOLUTION = 1;

        @Nullable
        private GoogleApiClient googleApiClient;
        @Nullable
        private Activity activity;

        @Override
        public void connect(@NonNull Activity activity, @NonNull ConnectionListener listener) {
            this.activity = activity;
            if (googleApiClient == null) {
                googleApiClient = new GoogleApiClient.Builder(activity)
                        .addApi(Drive.API)
                        .addScope(Drive.SCOPE_APPFOLDER)
                        .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(@Nullable Bundle bundle) {
                                Drive.DriveApi.requestSync(googleApiClient)
                                        .setResultCallback(result -> listener.onConnected());
                            }

                            @Override
                            public void onConnectionSuspended(int cause) {
                                listener.onConnectionSuspended();
                            }
                        })
                        .addOnConnectionFailedListener(result -> {
                            Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
                            if (!result.hasResolution()) {
                                GoogleApiAvailability.getInstance()
                                        .getErrorDialog(activity, result.getErrorCode(), 0)
                                        .show();
                                listener.onConnectionSuspended();
                                return;
                            }
                            try {
                                result.startResolutionForResult(activity, REQUEST_CODE_RESOLUTION);
                            } catch (IntentSender.SendIntentException e) {
                                Log.e(TAG, "Exception while starting resolution activity", e);
                            }
                        })
                        .build();
            }
            googleApiClient.connect();
        }

        @Override
        public void getBackups(@NonNull OnLoadFinishedListener listener) {
            Query query = new Query.Builder()
                    .addFilter(Filters.eq(SearchableField.MIME_TYPE, MYTARGETS_MIME_TYPE))
                    .addFilter(Filters.eq(SearchableField.TRASHED, false))
                    .setSortOrder(new SortOrder.Builder()
                            .addSortDescending(SortableField.MODIFIED_DATE).build())
                    .build();
            Drive.DriveApi.getAppFolder(googleApiClient).queryChildren(googleApiClient, query)
                    .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {

                        @NonNull
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
         */
        @Override
        public void restoreBackup(@NonNull BackupEntry backup, @NonNull BackupStatusListener listener) {
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
                            BackupUtils.importZip(activity, input);
                            listener.onFinished();
                        } catch (IOException e) {
                            e.printStackTrace();
                            listener.onError(e.getLocalizedMessage());
                        }
                    });
        }

        @Override
        public void deleteBackup(@NonNull BackupEntry backup, @NonNull BackupStatusListener listener) {
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
    }

    public static class Backup implements IBlockingBackup {
        @Override
        public void performBackup(@NonNull Context context) throws BackupException {
            GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_APPFOLDER)
                    .build();
            ConnectionResult connectionResult = googleApiClient.blockingConnect();
            if (!connectionResult.isSuccess()) {
                throw new BackupException(connectionResult.getErrorMessage());
            }

            DriveContentsResult result = Drive.DriveApi.newDriveContents(googleApiClient).await();
            if (!result.getStatus().isSuccess()) {
                throw new BackupException(result.getStatus().getStatusMessage());
            }

            final DriveContents driveContents = result.getDriveContents();
            OutputStream outputStream = driveContents.getOutputStream();

            try {
                BackupUtils.zip(context, outputStream);
            } catch (IOException e) {
                throw new BackupException(e.getLocalizedMessage(), e);
            }

            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                    .setTitle(BackupUtils.getBackupName())
                    .setMimeType(MYTARGETS_MIME_TYPE)
                    .setStarred(true)
                    .build();

            // create a file in selected folder
            DriveFileResult result1 = Drive.DriveApi.getAppFolder(googleApiClient)
                    .createFile(googleApiClient, changeSet, driveContents).await();
            if (!result1.getStatus().isSuccess()) {
                throw new BackupException(result1.getStatus().getStatusMessage());
            }
        }
    }
}
