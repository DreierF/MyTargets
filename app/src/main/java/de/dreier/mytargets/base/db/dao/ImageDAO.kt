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

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query

@Dao
interface ImageDAO {
    /**
     * Returns a list of file names, which are implicitly placed in the ../files/ folder of the app.
     */
    @Query("SELECT fileName FROM BowImage " +
            "UNION SELECT fileName FROM EndImage " +
            "UNION SELECT fileName FROM ArrowImage")
    fun loadAllFileNames(): List<String>
}
