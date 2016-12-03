/*
 * Copyright (C) 2016 Florian Dreier
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
import android.os.AsyncTask;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.android.Auth;
import com.dropbox.core.http.OkHttp3Requestor;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.WriteMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import de.dreier.mytargets.BuildConfig;
import de.dreier.mytargets.features.settings.backup.BackupEntry;
import de.dreier.mytargets.features.settings.backup.BackupException;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.managers.SettingsManager;

public class DropboxBackup {

    private static final String SEPARATOR = "#!#";
    private static final String APP_KEY = "cn8fltup424mmlr";

    private static class DropboxClientFactory {

        private static DbxClientV2 sDbxClient;

        public static void init(String accessToken) {
            if (sDbxClient == null) {
                DbxRequestConfig requestConfig = DbxRequestConfig
                        .newBuilder("MyTargets/" + BuildConfig.VERSION_NAME + "")
                        .withHttpRequestor(OkHttp3Requestor.INSTANCE)
                        .build();

                sDbxClient = new DbxClientV2(requestConfig, accessToken);
            }
        }

        public static DbxClientV2 getClient() {
            if (sDbxClient == null) {
                throw new IllegalStateException("Client not initialized.");
            }
            return sDbxClient;
        }
    }

    public static class AsyncRestore implements IAsyncBackupRestore {
        private Activity activity;

        @Override
        public void connect(Activity activity, ConnectionListener listener) {
            this.activity = activity;
            String accessToken = SettingsManager.getDropboxAccessToken();
            if (accessToken == null) {
                accessToken = Auth.getOAuth2Token();
                if (accessToken != null) {
                    SettingsManager.setDropboxAccessToken(accessToken);
                    DropboxClientFactory.init(accessToken);
                    listener.onConnected();
                } else {
                    Auth.startOAuth2Authentication(activity, APP_KEY);
                }
            } else {
                DropboxClientFactory.init(accessToken);
                listener.onConnected();
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
        }

        @Override
        public void getBackups(OnLoadFinishedListener listener) {
            new AsyncTask<String, Void, List<BackupEntry>>() {
                private Exception mException;

                @Override
                protected List<BackupEntry> doInBackground(String... params) {
                    try {
                        ListFolderResult result = DropboxClientFactory.getClient().files()
                                .listFolder("");
                        return Stream.of(result.getEntries())
                                .filter(metadata -> metadata.getName().endsWith(".zip"))
                                .filter(metadata -> metadata instanceof FileMetadata)
                                .map(metadata -> backupEntryFrom((FileMetadata) metadata))
                                .sorted((a, b) -> b.getModifiedDate()
                                        .compareTo(a.getModifiedDate()))
                                .collect(Collectors.toList());
                    } catch (DbxException e) {
                        mException = e;
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(List<BackupEntry> result) {
                    super.onPostExecute(result);
                    if (mException != null) {
                        listener.onError(mException.getLocalizedMessage());
                    } else {
                        listener.onLoadFinished(result);
                    }
                }
            }.execute();
        }

        private BackupEntry backupEntryFrom(FileMetadata metadata) {
            String fileId = metadata.getName() + SEPARATOR +
                    metadata.getId() + SEPARATOR +
                    metadata.getServerModified().getTime() + SEPARATOR +
                    metadata.getRev() + SEPARATOR +
                    metadata.getPathLower() + SEPARATOR +
                    metadata.getPathDisplay() + SEPARATOR +
                    metadata.getParentSharedFolderId();
            return new BackupEntry(fileId,
                    metadata.getClientModified(),
                    metadata.getSize());
        }

        private FileMetadata metadataFrom(BackupEntry file) {
            String[] split = file.getFileId().split(SEPARATOR);
            return new FileMetadata(split[0], split[1], file.getModifiedDate(),
                    new Date(Long.parseLong(split[2])), split[3],
                    file.getBackupSize(), split[4], split[5], split[6],
                    null, null, null, null);
        }

        /**
         * Restores the given backup and restarts the app if the restore was successful.
         */
        @Override
        public void restoreBackup(BackupEntry backup, BackupStatusListener listener) {
            new AsyncTask<Void, Void, File>() {
                private Exception mException;

                @Override
                protected File doInBackground(Void... params) {
                    try {
                        FileMetadata metadata = metadataFrom(backup);
                        File file = File.createTempFile(metadata.getName(), ".zip");

                        // Download the file.
                        OutputStream outputStream = new FileOutputStream(file);
                        DropboxClientFactory.getClient()
                                .files().download(metadata.getPathLower(), metadata.getRev())
                                .download(outputStream);
                        BackupUtils.safeCloseClosable(outputStream);

                        try {
                            final FileInputStream in = new FileInputStream(file);
                            DatabaseManager.Import(activity, in);
                            listener.onFinished();
                        } catch (IOException e) {
                            e.printStackTrace();
                            listener.onError(e.getLocalizedMessage());
                        } finally {
                            //noinspection ResultOfMethodCallIgnored
                            file.delete();
                        }

                        return file;
                    } catch (DbxException | IOException e) {
                        e.printStackTrace();
                        mException = e;
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(File result) {
                    super.onPostExecute(result);
                    if (mException != null) {
                        listener.onError(mException.getLocalizedMessage());
                    } else {
                        listener.onFinished();
                    }
                }
            }.execute();
        }

        @Override
        public void deleteBackup(BackupEntry backup, BackupStatusListener listener) {
            new AsyncTask<Void, Void, File>() {
                private Exception mException;

                @Override
                protected File doInBackground(Void... params) {
                    try {
                        FileMetadata metadata = metadataFrom(backup);
                        DropboxClientFactory.getClient().files().delete(metadata.getPathLower());
                    } catch (DbxException e) {
                        e.printStackTrace();
                        mException = e;
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(File result) {
                    super.onPostExecute(result);
                    if (mException != null) {
                        listener.onError(mException.getLocalizedMessage());
                    } else {
                        listener.onFinished();
                    }
                }
            }.execute();
        }

        @Override
        public void stop() {
            activity = null;
        }
    }

    public static class Backup implements IBlockingBackup {

        @Override
        public void performBackup(Context context) throws BackupException {
            String accessToken = SettingsManager.getDropboxAccessToken();
            if (accessToken == null) {
                accessToken = Auth.getOAuth2Token();
                if (accessToken == null) {
                    throw new BackupException("No access token");
                }
                SettingsManager.setDropboxAccessToken(accessToken);
            }
            DropboxClientFactory.init(accessToken);

            String remoteFileName = "/" + BackupUtils.getBackupName();

            InputStream inputStream = null;
            File localFile = null;
            try {
                localFile = File.createTempFile("backup_", ".zip");
                BackupUtils.zip(context, new FileOutputStream(localFile));

                inputStream = new FileInputStream(localFile);
                DropboxClientFactory.getClient().files()
                        .uploadBuilder(remoteFileName)
                        .withMode(WriteMode.OVERWRITE)
                        .uploadAndFinish(inputStream);
            } catch (DbxException | IOException e) {
                throw new BackupException("Failed to upload file.", e);
            } finally {
                BackupUtils.safeCloseClosable(inputStream);
                if (localFile != null) {
                    //noinspection ResultOfMethodCallIgnored
                    localFile.delete();
                }
            }
        }
    }

}
