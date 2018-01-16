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

package de.dreier.mytargets.base.db.migrations

import com.raizlabs.android.dbflow.annotation.Migration
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.sql.migration.BaseMigration
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper
import de.dreier.mytargets.base.db.migrations.Migration0.Companion.createScoreTriggers
import de.dreier.mytargets.shared.AppDatabase
import de.dreier.mytargets.shared.models.db.*

@Migration(version = 26, database = AppDatabase::class)
class Migration26 : BaseMigration() {

    override fun migrate(database: DatabaseWrapper) {
        database.execSQL("ALTER TABLE `Training` ADD COLUMN scoreReachedPoints INTEGER;")
        database.execSQL("ALTER TABLE `Training` ADD COLUMN scoreTotalPoints INTEGER;")
        database.execSQL("ALTER TABLE `Training` ADD COLUMN scoreShotCount INTEGER;")

        database.execSQL("ALTER TABLE `Round` ADD COLUMN scoreReachedPoints INTEGER;")
        database.execSQL("ALTER TABLE `Round` ADD COLUMN scoreTotalPoints INTEGER;")
        database.execSQL("ALTER TABLE `Round` ADD COLUMN scoreShotCount INTEGER;")

        database.execSQL("ALTER TABLE `End` ADD COLUMN scoreReachedPoints INTEGER;")
        database.execSQL("ALTER TABLE `End` ADD COLUMN scoreTotalPoints INTEGER;")
        database.execSQL("ALTER TABLE `End` ADD COLUMN scoreShotCount INTEGER;")

        Migration0.createScoreTriggers(database)

        val rounds = SQLite.select().from(Round::class.java).queryList(database)
        for (round in rounds) {
            val target = round.target
            val ends = SQLite.select().from(End::class.java).where(End_Table.round.eq(round.id))
                    .queryList(database)
            for (end in ends) {
                val shots = SQLite.select().from(Shot::class.java)
                        .where(Shot_Table.end.eq(end.id))
                        .orderBy(Shot_Table.index, true)
                        .queryList(database)
                end.score = target.getReachedScore(shots)
                end.save(database)
            }
        }
    }
}
