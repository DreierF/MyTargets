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
import com.raizlabs.android.dbflow.sql.migration.BaseMigration
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper
import de.dreier.mytargets.base.db.dao.StandardRoundDAO
import de.dreier.mytargets.shared.AppDatabase
import de.dreier.mytargets.shared.utils.StandardRoundFactory

@Migration(version = 0, database = AppDatabase::class)
class Migration0 : BaseMigration() {

    override fun migrate(database: DatabaseWrapper) {
        fillStandardRound(database)
        createScoreTriggers(database)
    }

    companion object {
        fun fillStandardRound(db: DatabaseWrapper) {
            val standardRounds = StandardRoundFactory.initTable()
            for (standardRound in standardRounds) {
                StandardRoundDAO.saveStandardRound(db, standardRound.standardRound, standardRound.roundTemplates)
            }
        }

        fun createScoreTriggers(database: DatabaseWrapper) {
            database.execSQL("CREATE TRIGGER round_sum_score " +
                    "AFTER UPDATE ON `End` " +
                    "BEGIN " +
                    "UPDATE `Round` SET " +
                    "scoreReachedPoints = (SELECT SUM(scoreReachedPoints) FROM `End` WHERE round = NEW.round), " +
                    "scoreTotalPoints = (SELECT SUM(scoreTotalPoints) FROM `End` WHERE round = NEW.round), " +
                    "scoreShotCount = (SELECT SUM(scoreShotCount) FROM `End` WHERE round = NEW.round) " +
                    "WHERE _id = NEW.round;" +
                    "END;")

            database.execSQL("CREATE TRIGGER training_sum_score " +
                    "AFTER UPDATE ON `Round` " +
                    "BEGIN " +
                    "UPDATE `Training` SET " +
                    "scoreReachedPoints = (SELECT SUM(scoreReachedPoints) FROM `Round` WHERE training = NEW.training), " +
                    "scoreTotalPoints = (SELECT SUM(scoreTotalPoints) FROM `Round` WHERE training = NEW.training), " +
                    "scoreShotCount = (SELECT SUM(scoreShotCount) FROM `Round` WHERE training = NEW.training) " +
                    "WHERE _id = NEW.training;" +
                    "END;")
        }
    }
}
