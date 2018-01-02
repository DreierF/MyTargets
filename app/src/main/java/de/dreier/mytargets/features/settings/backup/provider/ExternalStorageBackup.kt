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

package de.dreier.mytargets.features.settings.backup.provider

import android.app.Activity
import android.content.Context
import de.dreier.mytargets.R
import de.dreier.mytargets.features.settings.backup.BackupEntry
import de.dreier.mytargets.features.settings.backup.BackupException
import de.dreier.mytargets.shared.SharedApplicationInstance.Companion
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

object ExternalStorageBackup {
    private const  val FOLDER_NAME = "MyTargets"

    //If may get a full path that is not the right one, even if we don't have the SD Card there.
    //We just need the "/mnt/extSdCard/" i.e and check if it's writable
    //do what you need here
    val microSdCardPath: File?
        get() {
            var strSDCardPath: String? = System.getenv("SECONDARY_STORAGE")

            if (strSDCardPath == null || strSDCardPath.isEmpty()) {
                strSDCardPath = System.getenv("EXTERNAL_SDCARD_STORAGE")
            }
            if (strSDCardPath != null) {
                if (strSDCardPath.contains(":")) {
                    strSDCardPath = strSDCardPath.substring(0, strSDCardPath.indexOf(":"))
                }
                Timber.d("getMicroSdCardPath: %s", strSDCardPath)
                val externalFilePath = File(strSDCardPath)

                if (externalFilePath.exists() && externalFilePath.canWrite()) {
                    Timber.d("getMicroSdCardPath: %s", externalFilePath.absolutePath)
                    return externalFilePath
                }
            }
            return null
        }

    @Throws(IOException::class)
    private fun createDirectory(directory: File) {

        directory.mkdir()
        if (!directory.exists() || !directory.isDirectory) {
            throw IOException(Companion.getStr(R.string.dir_not_created))
        }
    }

    class AsyncRestore : IAsyncBackupRestore {
        private var activity: Activity? = null

        override fun connect(activity: Activity, listener: IAsyncBackupRestore.ConnectionListener) {
            this.activity = activity
            listener.onConnected()
        }

        override fun getBackups(listener: IAsyncBackupRestore.OnLoadFinishedListener) {
            val backupDir = File(microSdCardPath, FOLDER_NAME)
            if (backupDir.isDirectory) {
                val backups = backupDir.listFiles()
                        .filter { isBackup(it) }
                        .map {
                            BackupEntry(it.absolutePath,
                                    Date(it.lastModified()),
                                    it.length())
                        }
                        .sortedByDescending { it.modifiedDate }
                listener.onLoadFinished(backups)
            }
        }

        private fun isBackup(file: File): Boolean {
            return file.isFile && file.name.contains("backup_") && file.name.endsWith(".zip")
        }

        override fun restoreBackup(backup: BackupEntry, listener: IAsyncBackupRestore.BackupStatusListener) {
            val file = File(backup.fileId)
            try {
                BackupUtils.importZip(activity!!, FileInputStream(file))
                listener.onFinished()
            } catch (e: IOException) {
                listener.onError(e.localizedMessage)
                e.printStackTrace()
            }

        }

        override fun deleteBackup(backup: BackupEntry, listener: IAsyncBackupRestore.BackupStatusListener) {
            if (File(backup.fileId).delete()) {
                listener.onFinished()
            } else {
                listener.onError("Backup could not be deleted!")
            }
        }

        override fun stop() {
            activity = null
        }
    }

    class Backup : IBlockingBackup {
        @Throws(BackupException::class)
        override fun performBackup(context: Context) {
            try {
                val backupDir = File(microSdCardPath, FOLDER_NAME)
                createDirectory(backupDir)
                val zipFile = File(backupDir, BackupUtils.backupName)
                BackupUtils.zip(context, FileOutputStream(zipFile))
            } catch (e: IOException) {
                throw BackupException(e.localizedMessage, e)
            }
        }
    }
}
