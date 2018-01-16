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

package de.dreier.mytargets.base.db.dao

import com.raizlabs.android.dbflow.sql.language.SQLite
import de.dreier.mytargets.shared.models.db.ArrowImage
import de.dreier.mytargets.shared.models.db.BowImage
import de.dreier.mytargets.shared.models.db.EndImage
import java.util.*

object ImageDAO {
    /**
     * Returns a list of file names, which are implicitly placed in the ../files/ folder of the app.
     */
    fun loadAllFileNames(): List<String> {
        val list = ArrayList<String>()
        list.addAll(SQLite.select()
                .from(BowImage::class.java)
                .queryList()
                .map { (_, fileName) -> fileName })
        list.addAll(SQLite.select()
                .from(EndImage::class.java)
                .queryList()
                .map { (_, fileName) -> fileName }
        )
        list.addAll(SQLite.select()
                .from(ArrowImage::class.java)
                .queryList()
                .map { (_, fileName) -> fileName }
        )
        return list
    }
}
