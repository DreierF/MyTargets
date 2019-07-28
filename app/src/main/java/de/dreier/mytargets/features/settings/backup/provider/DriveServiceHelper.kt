/*
 * Copyright (C) 2019 Florian Dreier
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

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.concurrent.Callable
import java.util.concurrent.Executors

/**
 * A utility for performing read/write operations on Drive files via the REST API and opening a
 * file picker UI via Storage Access Framework.
 */
class DriveServiceHelper(private val mDriveService: Drive) {
    private val executor = Executors.newSingleThreadExecutor()

    /**
     * Creates a text file in the user's My Drive folder and returns its file ID.
     */
    fun createFile(): Task<String> {
        return Tasks.call(executor, Callable<String> {
            val metadata = File()
                .setParents(listOf("root"))
                .setMimeType("text/plain")
                .setName("Untitled file")

            val googleFile = mDriveService.files().create(metadata).execute()
                ?: throw IOException("Null result when requesting file creation.")

            googleFile.id
        })
    }

    /**
     * Opens the file identified by `fileId` and returns a [Pair] of its name and
     * contents.
     */
    fun readFile(fileId: String, consume: (InputStream) -> Unit): Task<Unit> {
        return Tasks.call(executor, Callable<Unit> {
            val inputStream = mDriveService.files().get(fileId).executeMediaAsInputStream()
            consume(inputStream)
        })
    }

    /**
     * Updates the file identified by `fileId` with the given `name` and `content`.
     */
    fun deleteFile(fileId: String): Task<Void> {
        return Tasks.call(executor, Callable<Void> {
            mDriveService.files().delete(fileId).execute()
            null
        })
    }

    /**
     * Returns a [FileList] containing all the visible files in the user's My Drive.
     *
     *
     * The returned list will only contain files visible to this app, i.e. those which were
     * created by this app. To perform operations on files not created by the app, the project must
     * request Drive Full Scope in the [Google
 * Developer's Console](https://play.google.com/apps/publish) and be submitted to Google for verification.
     */
    fun queryFiles(): Task<FileList> {
        return Tasks.call(
            executor,
            Callable<FileList> {
                mDriveService.files().list()
                    .setQ("mimeType = '${GoogleDriveBackup.MYTARGETS_MIME_TYPE}'")
                    .setFields("files(id,name,modifiedTime,trashed,size)")
                    .setSpaces("drive,appDataFolder").execute()
            })
    }

    /**
     * Returns an [Intent] for opening the Storage Access Framework file picker.
     */
    fun createFilePickerIntent(): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "text/plain"

        return intent
    }

    /**
     * Opens the file at the `uri` returned by a Storage Access Framework [Intent]
     * created by [.createFilePickerIntent] using the given `contentResolver`.
     */
    fun openFileUsingStorageAccessFramework(
        contentResolver: ContentResolver, uri: Uri
    ): Task<Pair<String, String>> {
        return Tasks.call(executor, Callable<Pair<String, String>> {
            // Retrieve the document's display name from its metadata.
            var name = ""
            contentResolver.query(uri, null, null, null, null).use()
            { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    name = cursor.getString(nameIndex)
                } else {
                    throw IOException("Empty cursor returned for file.")
                }
            }

            // Read the document's contents as a String.
            var content = ""
            contentResolver.openInputStream(uri)!!.use()
            { `is` ->
                BufferedReader(InputStreamReader(`is`)).use { reader ->
                    val stringBuilder = StringBuilder()
                    var line: String? = reader.readLine()
                    while (line != null) {
                        stringBuilder.append(line)
                        line = reader.readLine()
                    }
                    content = stringBuilder.toString()
                }
            }

            Pair(name, content)
        })
    }
}