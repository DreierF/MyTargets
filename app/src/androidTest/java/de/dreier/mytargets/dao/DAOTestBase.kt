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

package de.dreier.mytargets.dao

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import de.dreier.mytargets.base.db.AppDatabase
import org.junit.After
import org.junit.Before

abstract class DAOTestBase {
    protected lateinit var appDatabase: AppDatabase

    @Before
    fun initDb() {
        appDatabase = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            AppDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() {
        appDatabase.close()
    }
}