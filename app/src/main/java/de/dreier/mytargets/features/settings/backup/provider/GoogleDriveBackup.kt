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
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.features.settings.backup.BackupEntry
import de.dreier.mytargets.features.settings.backup.BackupException
import timber.log.Timber
import java.io.FileOutputStream
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList


object GoogleDriveBackup {

    const val MYTARGETS_MIME_TYPE = "application/zip"

    class AsyncRestore : IAsyncBackupRestore {

        private var context: WeakReference<Context>? = null

        private var listener: IAsyncBackupRestore.ConnectionListener? = null

        private var driveServiceHelper: DriveServiceHelper? = null

        override fun connect(
            context: Context,
            listener: IAsyncBackupRestore.ConnectionListener
        ) {
            Timber.d("connect: ")
            this.context = WeakReference(context)
            this.listener = listener

            val account = GoogleSignIn.getLastSignedInAccount(context)
            Timber.d("scopes: %s", account?.grantedScopes)
            if (account != null && GoogleSignIn.hasPermissions(
                    account,
                    Scope(DriveScopes.DRIVE_FILE),
                    Scope(DriveScopes.DRIVE_APPDATA)
                )
            ) {
                Timber.d("On connected")
                initServiceHelper(account)
                listener.onConnected()
            } else {
                Timber.d("Requesting sign-in")
                val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestScopes(Scope(DriveScopes.DRIVE_FILE), Scope(DriveScopes.DRIVE_APPDATA))
                    .build()
                val client = GoogleSignIn.getClient(context, signInOptions)

                // The result of the sign-in Intent is handled in onActivityResult.
                listener.onStartIntent(client.signInIntent, REQUEST_CODE_SIGN_IN)
            }
        }

        override fun getBackups(listener: IAsyncBackupRestore.OnLoadFinishedListener) {
            Timber.d("getBackups: ")
            driveServiceHelper?.let { mDriveServiceHelper ->
                Timber.d("Querying for files.")

                mDriveServiceHelper.queryFiles()
                    .addOnSuccessListener { fileList ->
                        Timber.d("getBackups: children")
                        val backupsArray = ArrayList<BackupEntry>()
                        for (file in fileList.files) {

                            if (file.trashed == true
                                || file.name?.endsWith(".zip") != true
                            ) {
                                continue
                            }
                            backupsArray.add(
                                BackupEntry(
                                    file.id,
                                    file.modifiedTime.value,
                                    // Do not replace with .size!! as that returns
                                    // the number of entries in the HashMap (=4)
                                    file.getSize()
                                )
                            )
                        }
                        backupsArray.sortByDescending { it.lastModifiedAt }

                        listener.onLoadFinished(backupsArray)
                    }
                    .addOnFailureListener { exception ->
                        Timber.e(
                            exception,
                            "Unable to query files."
                        )
                    }
            }
        }

        /**
         * Restores the given backup and restarts the app if the restore was successful.
         */
        override fun restoreBackup(
            backup: BackupEntry,
            listener: IAsyncBackupRestore.BackupStatusListener
        ) {
            driveServiceHelper?.readFile(backup.fileId) {
                BackupUtils.importZip(context!!.get()!!, it)
            }?.addOnSuccessListener {
                listener.onFinished()
            }?.addOnFailureListener { e -> listener.onError(e.localizedMessage) }
        }

        override fun deleteBackup(
            backup: BackupEntry,
            listener: IAsyncBackupRestore.BackupStatusListener
        ) {
            driveServiceHelper?.deleteFile(backup.fileId)
                ?.addOnSuccessListener { aVoid -> listener.onFinished() }
                ?.addOnFailureListener { e -> listener.onError(e.localizedMessage) }
        }

        override fun onActivityResult(
            requestCode: Int,
            resultCode: Int,
            resultData: Intent?
        ): Boolean {
            Timber.d("onActivityResult: %d", resultCode)

            when (requestCode) {
                REQUEST_CODE_SIGN_IN -> if (resultCode == Activity.RESULT_OK && resultData != null) {
                    handleSignInResult(resultData)
                } else if(resultCode == Activity.RESULT_CANCELED) {
                    listener?.onLoginCancelled()
                }

                //TODO allow backup location choice
//                REQUEST_CODE_OPEN_DOCUMENT -> if (resultCode == Activity.RESULT_OK && resultData != null) {
//                    val uri = resultData.getData()
//                    if (uri != null) {
//                        openFileFromFilePicker(uri)
//                    }
//                }
            }



            return false
        }

//        private fun openFileFromFilePicker(uri: Uri) {
//            driveServiceHelper?.let {driveServiceHelper->
//                Timber.d( "Opening %s",  uri.getPath())
//
//                driveServiceHelper.openFileUsingStorageAccessFramework(getContentResolver(), uri)
//                    .addOnSuccessListener { (name, content) ->
//
//                        mFileTitleEditText.setText(name)
//                        mDocContentEditText.setText(content)
//
//                    }
//                    .addOnFailureListener { exception ->
//                        Timber.e(
//                            exception,
//                            "Unable to open file from picker."
//                        )
//                    }
//            }
//        }

        /**
         * Handles the `result` of a completed sign-in activity initiated from [ ][.requestSignIn].
         */
        private fun handleSignInResult(result: Intent) {
            GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener { googleAccount ->
                    Timber.d("Signed in as %s", googleAccount.email!!)

                    initServiceHelper(googleAccount) //TODO check if redundant to connect
                    listener!!.onConnected()
                }
                .addOnFailureListener { exception ->
                    Timber.e(exception, "Unable to sign in.")
                    listener!!.onConnectionSuspended()
                }
        }

        private fun initServiceHelper(googleAccount: GoogleSignInAccount) {
            val context = context!!.get()!!
            val googleDriveService = buildDriveService(context, googleAccount)

            // The DriveServiceHelper encapsulates all REST API and SAF functionality.
            // Its instantiation is required before handling any onClick actions.
            driveServiceHelper = DriveServiceHelper(googleDriveService)
        }

        companion object {

            /**
             * Request code for auto Google Play Services error resolution.
             */
            const val REQUEST_CODE_SIGN_IN = 9001
        }
    }

    private fun buildDriveService(
        context: Context,
        googleAccount: GoogleSignInAccount
    ): Drive {
        // Use the authenticated account to sign in to the Drive service.
        val credential = GoogleAccountCredential.usingOAuth2(
            context,
            Arrays.asList(DriveScopes.DRIVE_FILE, DriveScopes.DRIVE_APPDATA)
        )
        credential.selectedAccount = googleAccount.account
        return Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory(),
            credential
        )
            .setApplicationName("MyTargets")
            .build()
    }

    class Backup : IBlockingBackup {
        @Throws(BackupException::class)
        override fun performBackup(context: Context) {
            val account = GoogleSignIn.getLastSignedInAccount(context)

            if (account == null || !GoogleSignIn.hasPermissions(
                    account,
                    Scope(DriveScopes.DRIVE_FILE)
                )
            ) {
                throw BackupException("Permission for file scope not granted!")
            }
            val googleDriveService = buildDriveService(context, account)

            val metadata = File()
                .setParents(listOf(SettingsManager.backupPathGoogleDrive))
                .setMimeType(MYTARGETS_MIME_TYPE)
                .setName(BackupUtils.backupName)

            val tempFile = java.io.File.createTempFile(BackupUtils.backupName, ".zip")

            try {
                BackupUtils.zip(context, ApplicationInstance.db, FileOutputStream(tempFile))
                val fileContent = FileContent(MYTARGETS_MIME_TYPE, tempFile)

                googleDriveService.files().create(metadata, fileContent).execute()
                    ?: throw BackupException("Null result when requesting file creation.")
            } catch (e: IOException) {
                // The Task failed, this is the same exception you'd get in a non-blocking
                // failure handler.
                throw BackupException(e.localizedMessage, e)
            } finally {
                tempFile.delete()
            }
        }
    }
}
