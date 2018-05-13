/*
 * Copyright (C) 2018 Florian Dreier
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

package de.dreier.mytargets.features.settings.backup.provider

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.os.Bundle
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.drive.*
import com.google.android.gms.drive.query.*
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.features.settings.backup.BackupEntry
import de.dreier.mytargets.features.settings.backup.BackupException
import timber.log.Timber
import java.io.IOException
import java.util.*

object GoogleDriveBackup {

    private const val MYTARGETS_MIME_TYPE = "application/zip"

    class AsyncRestore : IAsyncBackupRestore {

        private var googleApiClient: GoogleApiClient? = null
        private var activity: Activity? = null

        override fun connect(activity: Activity, listener: IAsyncBackupRestore.ConnectionListener) {
            this.activity = activity
            if (googleApiClient == null) {
                googleApiClient = GoogleApiClient.Builder(activity)
                        .addApi(Drive.API)
                        .addScope(Drive.SCOPE_APPFOLDER)
                        .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                            override fun onConnected(bundle: Bundle?) {
                                Drive.DriveApi.requestSync(googleApiClient)
                                        .setResultCallback { listener.onConnected() }
                            }

                            override fun onConnectionSuspended(cause: Int) {
                                listener.onConnectionSuspended()
                            }
                        })
                        .addOnConnectionFailedListener { result ->
                            Timber.i("GoogleApiClient connection failed: %s", result.toString())
                            if (!result.hasResolution()) {
                                GoogleApiAvailability.getInstance()
                                        .getErrorDialog(activity, result.errorCode, 0)
                                        .show()
                                listener.onConnectionSuspended()
                                return@addOnConnectionFailedListener
                            }
                            try {
                                result.startResolutionForResult(activity, REQUEST_CODE_RESOLUTION)
                            } catch (e: IntentSender.SendIntentException) {
                                Timber.e(e, "Exception while starting resolution activity")
                            }
                        }
                        .build()
            }
            googleApiClient!!.connect()
        }

        override fun getBackups(listener: IAsyncBackupRestore.OnLoadFinishedListener) {
            val query = Query.Builder()
                    .addFilter(Filters.eq(SearchableField.MIME_TYPE, MYTARGETS_MIME_TYPE))
                    .addFilter(Filters.eq(SearchableField.TRASHED, false))
                    .setSortOrder(SortOrder.Builder()
                            .addSortDescending(SortableField.MODIFIED_DATE).build())
                    .build()
            Drive.DriveApi.getAppFolder(googleApiClient)!!.queryChildren(googleApiClient, query)
                    .setResultCallback(object : ResultCallback<DriveApi.MetadataBufferResult> {

                        private val backupsArray = ArrayList<BackupEntry>()

                        override fun onResult(result: DriveApi.MetadataBufferResult) {
                            val buffer = result.metadataBuffer
                            val size = buffer.count
                            for (i in 0 until size) {
                                val metadata = buffer.get(i)
                                val driveId = metadata.driveId
                                val backupSize = metadata.fileSize
                                backupsArray.add(BackupEntry(driveId.encodeToString(),
                                        metadata.modifiedDate, backupSize))
                            }
                            listener.onLoadFinished(backupsArray)
                            buffer.release()
                        }
                    })
        }

        /**
         * Restores the given backup and restarts the app if the restore was successful.
         */
        override fun restoreBackup(backup: BackupEntry, listener: IAsyncBackupRestore.BackupStatusListener) {
            DriveId.decodeFromString(backup.fileId!!)
                    .asDriveFile()
                    .open(googleApiClient, DriveFile.MODE_READ_ONLY, null)
                    .setResultCallback { result ->
                        if (!result.status.isSuccess) {
                            listener.onError(result.status.statusMessage!!)
                            return@setResultCallback
                        }

                        // DriveContents object contains pointers to the actual byte stream
                        val contents = result.driveContents
                        val input = contents.inputStream
                        try {
                            BackupUtils.importZip(activity!!, input)
                            listener.onFinished()
                        } catch (e: IOException) {
                            e.printStackTrace()
                            listener.onError(e.localizedMessage)
                        }
                    }
        }

        override fun deleteBackup(backup: BackupEntry, listener: IAsyncBackupRestore.BackupStatusListener) {
            DriveId.decodeFromString(backup.fileId!!)
                    .asDriveFile()
                    .delete(googleApiClient)
                    .setResultCallback { result ->
                        if (result.status.isSuccess) {
                            listener.onFinished()
                        } else {
                            listener.onError(result.status.statusMessage!!)
                        }
                    }
        }

        override fun stop() {
            googleApiClient?.disconnect()
            googleApiClient = null
            activity = null
        }

        companion object {

            /**
             * Request code for auto Google Play Services error resolution.
             */
            const val REQUEST_CODE_RESOLUTION = 1
        }
    }

    class Backup : IBlockingBackup {
        @Throws(BackupException::class)
        override fun performBackup(context: Context) {
            val googleApiClient = GoogleApiClient.Builder(context)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_APPFOLDER)
                    .build()
            val connectionResult = googleApiClient.blockingConnect()
            if (!connectionResult.isSuccess) {
                throw BackupException(connectionResult.errorMessage)
            }

            val result = Drive.DriveApi.newDriveContents(googleApiClient).await()
            if (!result.status.isSuccess) {
                throw BackupException(result.status.statusMessage)
            }

            val driveContents = result.driveContents
            val outputStream = driveContents.outputStream

            try {
                BackupUtils.zip(context, ApplicationInstance.db, outputStream)
            } catch (e: IOException) {
                throw BackupException(e.localizedMessage, e)
            }

            val changeSet = MetadataChangeSet.Builder()
                    .setTitle(BackupUtils.backupName)
                    .setMimeType(MYTARGETS_MIME_TYPE)
                    .setStarred(true)
                    .build()

            // create a file in selected folder
            val result1 = Drive.DriveApi.getAppFolder(googleApiClient)!!
                    .createFile(googleApiClient, changeSet, driveContents).await()
            if (!result1.status.isSuccess) {
                throw BackupException(result1.status.statusMessage)
            }
        }
    }
}
