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

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration

object Migration18 : Migration(17, 18) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Rename existing tables
        database.execSQL("DROP TABLE IF EXISTS ROUND_OLD")
        database.execSQL("DROP TABLE IF EXISTS `End`")
        database.execSQL("DROP TABLE IF EXISTS `StandardRound`")
        database.execSQL("DROP TABLE IF EXISTS `RoundTemplate`")
        database.execSQL("DROP TABLE IF EXISTS `Shot`")
        database.execSQL("DROP TABLE IF EXISTS `SightMark`")
        database.execSQL("ALTER TABLE ARROW RENAME TO ARROW_OLD")
        database.execSQL("ALTER TABLE BOW RENAME TO BOW_OLD")
        database.execSQL("ALTER TABLE PASSE RENAME TO PASSE_OLD")
        database.execSQL("ALTER TABLE ROUND RENAME TO ROUND_OLD")
        database.execSQL("ALTER TABLE ROUND_TEMPLATE RENAME TO ROUND_TEMPLATE_OLD")
        database.execSQL("ALTER TABLE SHOOT RENAME TO SHOOT_OLD")
        database.execSQL("ALTER TABLE STANDARD_ROUND_TEMPLATE RENAME TO STANDARD_ROUND_TEMPLATE_OLD")
        database.execSQL("ALTER TABLE TRAINING RENAME TO TRAINING_OLD")
        database.execSQL("ALTER TABLE VISIER RENAME TO VISIER_OLD")

        // Arrow migration
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `Arrow`( " +
                    "`_id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "`name` TEXT," +
                    "`length` TEXT," +
                    "`material` TEXT," +
                    "`spine` TEXT," +
                    "`weight` TEXT," +
                    "`tipWeight` TEXT," +
                    "`vanes` TEXT," +
                    "`nock` TEXT," +
                    "`comment` TEXT," +
                    "`diameter` TEXT," +
                    "`thumbnail` BLOB " +
                    ")"
        )
        database.execSQL(
            "INSERT INTO `Arrow` " +
                    "SELECT `_id`,`name`,`length`,`material`,`spine`," +
                    "`weight`,`tip_weight`,`vanes`,`nock`," +
                    "`comment`,`diameter` || ' ' || `diameter_unit`," +
                    "`thumbnail` " +
                    "FROM ARROW_OLD"
        )

        // Arrow image migration
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `ArrowImage`( " +
                    "`_id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "`fileName` TEXT," +
                    "`arrow` INTEGER," +
                    "FOREIGN KEY(`arrow`) REFERENCES Arrow(`_id`) ON UPDATE NO ACTION ON DELETE CASCADE " +
                    ")"
        )
        database.execSQL(
            "INSERT INTO `ArrowImage`(`fileName`,`arrow`) " +
                    "SELECT `image`, `_id` " +
                    "FROM ARROW_OLD " +
                    "WHERE `image` <> ''"
        )

        // Bow migration
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `Bow`( " +
                    "`_id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "`name` TEXT," +
                    "`type` INTEGER," +
                    "`brand` TEXT," +
                    "`size` TEXT," +
                    "`braceHeight` TEXT," +
                    "`tiller` TEXT," +
                    "`limbs` TEXT," +
                    "`sight` TEXT," +
                    "`drawWeight` TEXT," +
                    "`stabilizer` TEXT," +
                    "`clicker` TEXT," +
                    "`button` TEXT," +
                    "`string` TEXT," +
                    "`nockingPoint` TEXT," +
                    "`letoffWeight` TEXT," +
                    "`arrowRest` TEXT," +
                    "`restHorizontalPosition` TEXT," +
                    "`restVerticalPosition` TEXT," +
                    "`restStiffness` TEXT," +
                    "`camSetting` TEXT," +
                    "`scopeMagnification` TEXT," +
                    "`description` TEXT," +
                    "`thumbnail` BLOB " +
                    ")"
        )
        database.execSQL(
            "INSERT INTO `Bow` " +
                    "SELECT `_id`,`name`,`type`,`brand`,`size`," +
                    "`height`,`tiller`,`limbs`,`sight`," +
                    "`draw_weight`,`stabilizer`,`clicker`," +
                    "'', '', '', '', ''," +
                    "'', '', '', '', ''," +
                    "`description`,`thumbnail` " +
                    "FROM BOW_OLD"
        )
        // Bow image migration
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `BowImage`(" +
                    "`_id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "`fileName` TEXT," +
                    "`bow` INTEGER," +
                    "FOREIGN KEY(`bow`) REFERENCES Bow(`_id`) ON UPDATE NO ACTION ON DELETE CASCADE " +
                    ")"
        )
        database.execSQL(
            "INSERT INTO `BowImage`(`fileName`,`bow`) " +
                    "SELECT `image`, `_id` " +
                    "FROM BOW_OLD " +
                    "WHERE `image` <> ''"
        )
        // StandardRound migration
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `StandardRound`(" +
                    "`_id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "`club` INTEGER," +
                    "`name` TEXT " +
                    ")"
        )
        database.execSQL(
            "INSERT INTO `StandardRound` " +
                    "SELECT `_id`,`club`,`name` " +
                    "FROM STANDARD_ROUND_TEMPLATE_OLD " +
                    "WHERE `club` < 512"
        )
        // RoundTemplate migration
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `RoundTemplate`( " +
                    "`_id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "`standardRound` INTEGER," +
                    "`index` INTEGER," +
                    "`shotsPerEnd` INTEGER," +
                    "`endCount` INTEGER," +
                    "`distance` TEXT," +
                    "`targetId` INTEGER," +
                    "`targetScoringStyle` INTEGER," +
                    "`targetDiameter` TEXT," +
                    "FOREIGN KEY(`standardRound`) REFERENCES StandardRound(`_id`) ON UPDATE NO ACTION ON DELETE CASCADE " +
                    ")"
        )
        database.execSQL(
            "INSERT INTO `RoundTemplate` " +
                    "SELECT `_id`,`sid`,`r_index`,`arrows`,`passes`," +
                    "`distance` || ' ' || `unit`," +
                    "`target`,`scoring_style`,`size` || ' ' || `target_unit` " +
                    "FROM ROUND_TEMPLATE_OLD"
        )
        // Training migration
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `Training`( " +
                    "`_id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "`title` TEXT," +
                    "`date` INTEGER," +
                    "`standardRound` INTEGER," +
                    "`bow` INTEGER," +
                    "`arrow` INTEGER," +
                    "`arrowNumbering` INTEGER," +
                    "`indoor` INTEGER," +
                    "`weather` INTEGER," +
                    "`windDirection` INTEGER," +
                    "`windSpeed` INTEGER," +
                    "`location` TEXT," +
                    "FOREIGN KEY(`standardRound`) REFERENCES StandardRound(`_id`) ON UPDATE NO ACTION ON DELETE NO ACTION," +
                    "FOREIGN KEY(`bow`) REFERENCES Bow(`_id`) ON UPDATE NO ACTION ON DELETE SET NULL," +
                    "FOREIGN KEY(`arrow`) REFERENCES Arrow(`_id`) ON UPDATE NO ACTION ON DELETE SET NULL " +
                    ")"
        )
        database.execSQL("UPDATE TRAINING_OLD SET bow = null WHERE bow < 1")
        database.execSQL("UPDATE TRAINING_OLD SET arrow = null WHERE arrow < 1")

        database.execSQL(
            "INSERT INTO `Training` " +
                    "SELECT t.`_id`,t.`title`,t.`datum`," +
                    "CASE WHEN s.club < 512 THEN t.`standard_round` ELSE NULL END," +
                    "t.`bow`,t.`arrow`, t.`arrow_numbering`," +
                    "s.`indoor`,t.`weather`,t.`wind_direction`,t.`wind_speed`,t.`location` " +
                    "FROM TRAINING_OLD t, STANDARD_ROUND_TEMPLATE_OLD s " +
                    "WHERE t.standard_round=s._id"
        )

        // Round migration
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `Round`( " +
                    "`_id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "`training` INTEGER," +
                    "`index` INTEGER," +
                    "`shotsPerEnd` INTEGER," +
                    "`maxEndCount` INTEGER," +
                    "`distance` TEXT," +
                    "`comment` TEXT," +
                    "`targetId` INTEGER," +
                    "`targetScoringStyle` INTEGER," +
                    "`targetDiameter` TEXT," +
                    "FOREIGN KEY(`training`) REFERENCES Training(`_id`) ON UPDATE NO ACTION ON DELETE CASCADE " +
                    ")"
        )
        database.execSQL(
            "INSERT INTO `Round` " +
                    "SELECT r.`_id`,r.`training`,t.`r_index`,t.`arrows`," +
                    "CASE WHEN s.club = 512 THEN NULL ELSE t.`passes` END," +
                    "t.`distance` || ' ' || t.`unit`, r.`comment`,r.`target`,r.`scoring_style`," +
                    "t.`size` || ' ' || t.`target_unit` " +
                    "FROM ROUND_OLD r, ROUND_TEMPLATE_OLD t, STANDARD_ROUND_TEMPLATE_OLD s " +
                    "WHERE r.template=t._id " +
                    "AND s._id=t.sid"
        )

        // End migration
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `End`( " +
                    "`_id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "`index` INTEGER," +
                    "`round` INTEGER," +
                    "`exact` INTEGER," +
                    "`saveTime` INTEGER," +
                    "FOREIGN KEY(`round`) REFERENCES Round(`_id`) ON UPDATE NO ACTION ON DELETE CASCADE " +
                    ")"
        )
        database.execSQL(
            "INSERT INTO `End` " +
                    "SELECT `_id`, (SELECT COUNT(*) " +
                    "     FROM PASSE_OLD p " +
                    "     WHERE PASSE_OLD.round = p.round " +
                    "     AND PASSE_OLD._id > p._id)," +
                    "     `round`,`exact`,`save_time` " +
                    "FROM PASSE_OLD"
        )

        // End image migration
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `EndImage`( " +
                    "`_id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "`fileName` TEXT," +
                    "`end` INTEGER," +
                    "FOREIGN KEY(`end`) REFERENCES End(`_id`) ON UPDATE NO ACTION ON DELETE CASCADE" +
                    ")"
        )
        database.execSQL(
            "INSERT INTO `EndImage`(`fileName`,`end`) " +
                    "SELECT `image`, `_id` " +
                    "FROM PASSE_OLD " +
                    "WHERE `image` <> ''"
        )

        // Shot migration
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `Shot`( " +
                    "`_id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "`index` INTEGER," +
                    "`end` INTEGER," +
                    "`x` REAL," +
                    "`y` REAL," +
                    "`scoringRing` INTEGER," +
                    "`comment` TEXT," +
                    "`arrowNumber` TEXT," +
                    "FOREIGN KEY(`end`) REFERENCES End(`_id`) ON UPDATE NO ACTION ON DELETE CASCADE " +
                    ")"
        )
        database.execSQL(
            "INSERT INTO `Shot` " +
                    "SELECT `_id`,`arrow_index`,`passe`,`x`,`y`,`points`,`comment`,`arrow` " +
                    "FROM SHOOT_OLD"
        )

        // SightMark migration
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `SightMark`( " +
                    "`_id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "`bow` INTEGER," +
                    "`distance` TEXT," +
                    "`value` TEXT," +
                    "FOREIGN KEY(`bow`) REFERENCES Bow(`_id`) ON UPDATE NO ACTION ON DELETE CASCADE " +
                    ")"
        )
        database.execSQL(
            "INSERT INTO `SightMark` " +
                    "SELECT `_id`,`bow`,`distance` || ' ' || `unit`,`setting` " +
                    "FROM VISIER_OLD"
        )

        // Remove old tables
        database.execSQL("DROP TABLE ARROW_OLD")
        database.execSQL("DROP TABLE BOW_OLD")
        database.execSQL("DROP TABLE IF EXISTS NUMBER")
        database.execSQL("DROP TABLE PASSE_OLD")
        database.execSQL("DROP TABLE ROUND_OLD")
        database.execSQL("DROP TABLE ROUND_TEMPLATE_OLD")
        database.execSQL("DROP TABLE SHOOT_OLD")
        database.execSQL("DROP TABLE STANDARD_ROUND_TEMPLATE_OLD")
        database.execSQL("DROP TABLE TRAINING_OLD")
        database.execSQL("DROP TABLE VISIER_OLD")
    }
}
