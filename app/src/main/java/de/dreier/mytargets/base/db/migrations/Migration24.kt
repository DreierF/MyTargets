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

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration

object Migration24 : Migration(23, 24) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add signature table
        database.execSQL("CREATE TABLE IF NOT EXISTS `Signature`( " +
                "`_id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "`name` TEXT, " +
                "`bitmap` BLOB" +
                ")")

        // Rename existing tables
        database.execSQL("ALTER TABLE `Shot` RENAME TO SHOT_OLD")
        database.execSQL("ALTER TABLE `End` RENAME TO END_OLD")
        database.execSQL("ALTER TABLE `Training` RENAME TO TRAINING_OLD")

        // Training migration
        database.execSQL("CREATE TABLE IF NOT EXISTS `Training`( " +
                "`_id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "`title` TEXT, " +
                "`date` TEXT, " +
                "`standardRound` INTEGER, " +
                "`bow` INTEGER, " +
                "`arrow` INTEGER, " +
                "`arrowNumbering` INTEGER, " +
                "`indoor` INTEGER, " +
                "`weather` INTEGER, " +
                "`windDirection` INTEGER, " +
                "`windSpeed` INTEGER, " +
                "`location` TEXT, " +
                "`comment` TEXT, " +
                "`archerSignature` INTEGER, " +
                "`witnessSignature` INTEGER, " +
                "FOREIGN KEY(`standardRound`) REFERENCES StandardRound(`_id`) ON UPDATE NO ACTION ON DELETE SET NULL, " +
                "FOREIGN KEY(`bow`) REFERENCES Bow(`_id`) ON UPDATE NO ACTION ON DELETE SET NULL, " +
                "FOREIGN KEY(`arrow`) REFERENCES Arrow(`_id`) ON UPDATE NO ACTION ON DELETE SET NULL, " +
                "FOREIGN KEY(`archerSignature`) REFERENCES Signature(`_id`) ON UPDATE NO ACTION ON DELETE SET NULL, " +
                "FOREIGN KEY(`witnessSignature`) REFERENCES Signature(`_id`) ON UPDATE NO ACTION ON DELETE SET NULL " +
                ")")
        database.execSQL("INSERT INTO `Training` " +
                "SELECT `_id`, `title`, date(`date`/1000, 'unixepoch', 'localtime'), " +
                "`standardRound`, `bow`, `arrow`, " +
                "`arrowNumbering`, `indoor`, " +
                "`weather`, `windDirection`, `windSpeed`, `location`, '', NULL, NULL " +
                "FROM TRAINING_OLD")
        database.execSQL("UPDATE Round SET comment = \"\" WHERE comment = NULL")
        database.execSQL("UPDATE Round SET targetDiameter = \"-1 m\" WHERE targetDiameter = NULL")
        database.execSQL("UPDATE RoundTemplate SET targetDiameter = \"-1 m\" WHERE targetDiameter = NULL")

        // End migration
        database.execSQL("CREATE TABLE IF NOT EXISTS `End`( " +
                "`_id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "`index` INTEGER, " +
                "`round` INTEGER, " +
                "`exact` INTEGER, " +
                "`saveTime` TEXT, " +
                "`comment` TEXT, " +
                "FOREIGN KEY(`round`) REFERENCES Round(`_id`) ON UPDATE NO ACTION ON DELETE CASCADE " +
                ")")
        database.execSQL("INSERT INTO `End` " +
                "SELECT e.`_id`, e.`index`, e.`round`, e.`exact`, time(e.`saveTime`/1000, 'unixepoch', 'localtime'), " +
                "TRIM(GROUP_CONCAT(s.`comment`, x'0a'), x'0a' || \" \") " +
                "FROM END_OLD e LEFT OUTER JOIN SHOT_OLD s ON s.`end`=e._id " +
                "GROUP BY e._id")
        database.execSQL("CREATE TABLE IF NOT EXISTS `Shot`( " +
                "`_id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "`index` INTEGER, " +
                "`end` INTEGER, " +
                "`x` REAL, " +
                "`y` REAL, " +
                "`scoringRing` INTEGER, " +
                "`arrowNumber` TEXT, " +
                "FOREIGN KEY(`end`) REFERENCES End(`_id`) ON UPDATE NO ACTION ON DELETE CASCADE " +
                ")")
        database.execSQL("INSERT INTO `Shot` " +
                "SELECT `_id`,`index`,`end`,`x`,`y`,`scoringRing`,`arrowNumber` " +
                "FROM SHOT_OLD")

        // Remove old tables
        database.execSQL("DROP TABLE SHOT_OLD")
        database.execSQL("DROP TABLE END_OLD")
        database.execSQL("DROP TABLE TRAINING_OLD")

        // Add max arrow number
        database.execSQL("ALTER TABLE `Arrow` ADD COLUMN maxArrowNumber INTEGER")
        database.execSQL("UPDATE `Arrow` SET maxArrowNumber = 12")
    }
}
