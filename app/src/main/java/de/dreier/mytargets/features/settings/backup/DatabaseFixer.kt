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

package de.dreier.mytargets.features.settings.backup

import de.dreier.mytargets.base.db.AppDatabase
import timber.log.Timber

object DatabaseFixer {
    fun fix(db: AppDatabase) {
        val openHelper = db.openHelper
        openHelper.setWriteAheadLoggingEnabled(false)
        val database = openHelper.writableDatabase
        Timber.d("db integrity: %b", database.isDatabaseIntegrityOk)

        database.query("reindex", arrayOf())
        database.query("vacuum", arrayOf())

        val cursor = database.query("pragma integrity_check;", arrayOf())
        cursor.moveToNext()
        val string = cursor.getString(0)
        Timber.w("pragma integrity_check => $string")
        cursor.close()

        val cursor2 = database.query("SELECT * FROM `Training`", arrayOf())
        cursor2.moveToNext()
        Timber.w("trainings => ${cursor2.count}")
        cursor2.close()

        openHelper.close()
    }
}
