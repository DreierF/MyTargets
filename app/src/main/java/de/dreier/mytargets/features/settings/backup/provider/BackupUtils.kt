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

import android.content.Context
import de.dreier.mytargets.base.db.dao.ImageDAO
import de.dreier.mytargets.shared.AppDatabase
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object BackupUtils {

    val backupName: String
        get() {
            val format = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US)
            return "MyTargets_backup_" + format.format(Date()) + ".zip"
        }

    @Throws(IOException::class)
    fun importZip(context: Context, `in`: InputStream) {
        // Unzip all images and database
        val file = unzip(context, `in`)

        // Replace database file
        file!!.copyTo(context.getDatabasePath(AppDatabase.DATABASE_IMPORT_FILE_NAME))
    }

    @Throws(IOException::class)
    fun zip(context: Context, dest: OutputStream) {
        ZipOutputStream(BufferedOutputStream(dest)).use { out ->
            val db = context.getDatabasePath(AppDatabase.DATABASE_FILE_NAME)

            var entry = ZipEntry("/data.db")
            out.putNextEntry(entry)
            FileInputStream(db).use { it.copyTo(out) }

            for (file in ImageDAO.loadAllFileNames()) {
                try {
                    entry = ZipEntry("/" + file)
                    out.putNextEntry(entry)
                    FileInputStream(File(context.filesDir, file)).use {
                        it.copyTo(out)
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun unzip(context: Context, `in`: InputStream): File? {
        val tmpDb = File.createTempFile("import", ".db")
        var dbFiles = 0
        `in`.use {
            val zin = ZipInputStream(`in`)
            var sourceEntry: ZipEntry?
            while (true) {

                sourceEntry = zin.nextEntry

                if (sourceEntry == null) {
                    break
                }

                if (sourceEntry.isDirectory) {
                    zin.closeEntry()
                    continue
                }

                val fOut: FileOutputStream
                if (sourceEntry.name.endsWith(".db")) {
                    // Write database to tmp file
                    fOut = FileOutputStream(tmpDb!!)
                    dbFiles++
                } else {
                    // Write all other files(images) to files dir in apps data
                    val start = sourceEntry.name.lastIndexOf("/") + 1
                    val name = sourceEntry.name.substring(start)
                    fOut = context.openFileOutput(name, Context.MODE_PRIVATE)
                }

                fOut.use {
                    zin.copyTo(fOut)
                    fOut.flush()
                }
                zin.closeEntry()
            }
        }
        if (dbFiles != 1) {
            throw IOException("Input file is not a valid backup")
        }
        return tmpDb
    }
}
