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

import android.database.Cursor
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.models.db.Shot
import timber.log.Timber

object Migration26 : Migration(25, 26) {

    override fun migrate(database: SupportSQLiteDatabase) {
        Timber.i("Migrating DB from version 25 to 26")

        try {
            database.execSQL("ALTER TABLE `Training` ADD COLUMN `reachedPoints` INTEGER DEFAULT 0;")
            database.execSQL("ALTER TABLE `Training` ADD COLUMN `totalPoints` INTEGER DEFAULT 0;")
            database.execSQL("ALTER TABLE `Training` ADD COLUMN `shotCount` INTEGER DEFAULT 0;")

            database.execSQL("ALTER TABLE `Round` ADD COLUMN `reachedPoints` INTEGER DEFAULT 0;")
            database.execSQL("ALTER TABLE `Round` ADD COLUMN `totalPoints` INTEGER DEFAULT 0;")
            database.execSQL("ALTER TABLE `Round` ADD COLUMN `shotCount` INTEGER DEFAULT 0;")

            database.execSQL("ALTER TABLE `End` ADD COLUMN `reachedPoints` INTEGER DEFAULT 0;")
            database.execSQL("ALTER TABLE `End` ADD COLUMN `totalPoints` INTEGER DEFAULT 0;")
            database.execSQL("ALTER TABLE `End` ADD COLUMN `shotCount` INTEGER DEFAULT 0;")

            recreateTablesWithNonNull(database)

            createScoreTriggers(database)

            createIndices(database)

            database.query("SELECT `id`, `targetId`, `targetScoringStyleIndex`, `targetDiameter` FROM `Round`")
                .useEach { round ->
                    val diameter = Dimension.parse(round.getString(3))
                    val target = Target(round.getLong(1), round.getInt(2), diameter)
                    val roundId = round.getLong(0)

                    database.query("SELECT `id` FROM `End` WHERE `roundId` = $roundId")
                        .useEach { end ->
                            val endId = end.getLong(0)

                            val shots = mutableListOf<Shot>()
                            database.query("SELECT `index`, `scoringRing` FROM `Shot` WHERE `endId` = $endId ORDER BY `index`")
                                .useEach { shotCursor ->
                                    shots.add(
                                        Shot(
                                            index = shotCursor.getInt(0),
                                            scoringRing = shotCursor.getInt(1)
                                        )
                                    )
                                }

                            val score = target.getReachedScore(shots)
                            database.execSQL(
                                "UPDATE `End` SET " +
                                        "`reachedPoints` = ${score.reachedPoints}, " +
                                        "`totalPoints` = ${score.totalPoints}, " +
                                        "`shotCount` = ${score.shotCount} " +
                                        "WHERE `id` = $endId"
                            )
                        }
                }
        } catch (e: Exception) {
            Timber.i(e, "DB has changed because of restoring a backup")
        }
    }

    private fun createScoreTriggers(database: SupportSQLiteDatabase) {
        // Insert Triggers
        database.execSQL(getInsertTrigger("Round", "End", "roundId"))
        database.execSQL(getInsertTrigger("Training", "Round", "trainingId"))

        // Update Triggers
        database.execSQL(getUpdateTrigger("Round", "End", "roundId"))
        database.execSQL(getUpdateTrigger("Training", "Round", "trainingId"))

        // Delete Triggers
        database.execSQL(getDeleteTrigger("Round", "End", "roundId"))
        database.execSQL(getDeleteTrigger("Training", "Round", "trainingId"))
    }

    private fun getInsertTrigger(
        parentTable: String,
        childTable: String,
        joinColumn: String
    ): String {
        return "CREATE TRIGGER insert_${parentTable.toLowerCase()}_sum_score " +
                "AFTER INSERT ON `$childTable` " +
                "BEGIN " +
                getUpdateQuery(parentTable, childTable, joinColumn, "NEW") +
                "END;"
    }

    private fun getUpdateTrigger(
        parentTable: String,
        childTable: String,
        joinColumn: String
    ): String {
        return "CREATE TRIGGER update_${parentTable.toLowerCase()}_sum_score " +
                "AFTER UPDATE OF reachedPoints, totalPoints, shotCount ON `$childTable` " +
                "BEGIN " +
                getUpdateQuery(parentTable, childTable, joinColumn, "NEW") +
                "END;"
    }

    private fun getDeleteTrigger(
        parentTable: String,
        childTable: String,
        joinColumn: String
    ): String {
        return "CREATE TRIGGER delete_${parentTable.toLowerCase()}_sum_score " +
                "AFTER DELETE ON `$childTable` " +
                "BEGIN " +
                getUpdateQuery(parentTable, childTable, joinColumn, "OLD") +
                "END;"
    }

    private fun getUpdateQuery(
        parentTable: String,
        childTable: String,
        joinColumn: String,
        reference: String
    ): String {
        return "UPDATE `$parentTable` SET " +
                "reachedPoints = (SELECT IFNULL(SUM(reachedPoints), 0) FROM `$childTable` WHERE $joinColumn = $reference.$joinColumn), " +
                "totalPoints = (SELECT IFNULL(SUM(totalPoints), 0) FROM `$childTable` WHERE $joinColumn = $reference.$joinColumn), " +
                "shotCount = (SELECT IFNULL(SUM(shotCount), 0) FROM `$childTable` WHERE $joinColumn = $reference.$joinColumn) " +
                "WHERE id = $reference.$joinColumn;"
    }

    private fun createIndices(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE  INDEX `index_ArrowImage_arrowId` ON `ArrowImage` (`arrowId`)")
        database.execSQL("CREATE  INDEX `index_BowImage_bowId` ON `BowImage` (`bowId`)")
        database.execSQL("CREATE  INDEX `index_End_roundId` ON `End` (`roundId`)")
        database.execSQL("CREATE  INDEX `index_EndImage_endId` ON `EndImage` (`endId`)")
        database.execSQL("CREATE  INDEX `index_Round_trainingId` ON `Round` (`trainingId`)")
        database.execSQL("CREATE  INDEX `index_RoundTemplate_standardRoundId` ON `RoundTemplate` (`standardRoundId`)")
        database.execSQL("CREATE  INDEX `index_Shot_endId` ON `Shot` (`endId`)")
        database.execSQL("CREATE  INDEX `index_SightMark_bowId` ON `SightMark` (`bowId`)")
        database.execSQL("CREATE  INDEX `index_Training_arrowId` ON `Training` (`arrowId`)")
        database.execSQL("CREATE  INDEX `index_Training_bowId` ON `Training` (`bowId`)")
        database.execSQL("CREATE  INDEX `index_Training_standardRoundId` ON `Training` (`standardRoundId`)")
        database.execSQL("CREATE  INDEX `index_Training_archerSignatureId` ON `Training` (`archerSignatureId`)")
        database.execSQL("CREATE  INDEX `index_Training_witnessSignatureId` ON `Training` (`witnessSignatureId`)")
    }

    private fun recreateTablesWithNonNull(database: SupportSQLiteDatabase) {
        val migrations = listOf(
            TableMigrationBuilder("Arrow")
                .field(
                    name = "id",
                    columnName = "_id",
                    affinity = "INTEGER PRIMARY KEY AUTOINCREMENT",
                    notNull = true
                )
                .field(name = "name", notNull = true)
                .field(name = "maxArrowNumber", affinity = "INTEGER", notNull = true)
                .field(name = "length", affinity = "TEXT")
                .field(name = "material", affinity = "TEXT")
                .field(name = "spine", affinity = "TEXT")
                .field(name = "weight", affinity = "TEXT")
                .field(name = "tipWeight", affinity = "TEXT")
                .field(name = "vanes", affinity = "TEXT")
                .field(name = "nock", affinity = "TEXT")
                .field(name = "comment", affinity = "TEXT")
                .field(name = "diameter", notNull = true)
                .field(name = "thumbnail", affinity = "BLOB"),

            TableMigrationBuilder("ArrowImage")
                .field(
                    name = "id",
                    columnName = "_id",
                    affinity = "INTEGER PRIMARY KEY AUTOINCREMENT",
                    notNull = true
                )
                .field(name = "fileName", notNull = true)
                .field(
                    name = "arrowId",
                    columnName = "arrow",
                    affinity = "INTEGER",
                    refTable = "Arrow",
                    refColumn = "id",
                    onDelete = "CASCADE"
                ),

            TableMigrationBuilder("Bow")
                .field(
                    name = "id",
                    columnName = "_id",
                    affinity = "INTEGER PRIMARY KEY AUTOINCREMENT",
                    notNull = true
                )
                .field(name = "name", notNull = true)
                .field(name = "type", affinity = "INTEGER")
                .field(name = "brand", affinity = "TEXT")
                .field(name = "size", affinity = "TEXT")
                .field(name = "braceHeight", affinity = "TEXT")
                .field(name = "tiller", affinity = "TEXT")
                .field(name = "limbs", affinity = "TEXT")
                .field(name = "sight", affinity = "TEXT")
                .field(name = "drawWeight", affinity = "TEXT")
                .field(name = "stabilizer", affinity = "TEXT")
                .field(name = "clicker", affinity = "TEXT")
                .field(name = "button", affinity = "TEXT")
                .field(name = "string", affinity = "TEXT")
                .field(name = "nockingPoint", affinity = "TEXT")
                .field(name = "letoffWeight", affinity = "TEXT")
                .field(name = "arrowRest", affinity = "TEXT")
                .field(name = "restHorizontalPosition", affinity = "TEXT")
                .field(name = "restVerticalPosition", affinity = "TEXT")
                .field(name = "restStiffness", affinity = "TEXT")
                .field(name = "camSetting", affinity = "TEXT")
                .field(name = "scopeMagnification", affinity = "TEXT")
                .field(name = "description", affinity = "TEXT")
                .field(name = "thumbnail", affinity = "BLOB"),

            TableMigrationBuilder("BowImage")
                .field(
                    name = "id",
                    columnName = "_id",
                    affinity = "INTEGER PRIMARY KEY AUTOINCREMENT",
                    notNull = true
                )
                .field(name = "fileName", notNull = true)
                .field(
                    name = "bowId",
                    columnName = "bow",
                    affinity = "INTEGER",
                    refTable = "Bow",
                    refColumn = "id",
                    onDelete = "CASCADE"
                ),

            TableMigrationBuilder("StandardRound")
                .field(
                    name = "id",
                    columnName = "_id",
                    affinity = "INTEGER PRIMARY KEY AUTOINCREMENT",
                    notNull = true
                )
                .field(name = "club", affinity = "INTEGER", notNull = true)
                .field(name = "name", notNull = true),

            TableMigrationBuilder("RoundTemplate")
                .field(
                    name = "id",
                    columnName = "_id",
                    affinity = "INTEGER PRIMARY KEY AUTOINCREMENT",
                    notNull = true
                )
                .field(
                    name = "standardRoundId",
                    columnName = "standardRound",
                    affinity = "INTEGER",
                    refTable = "StandardRound",
                    refColumn = "id",
                    onDelete = "CASCADE"
                )
                .field(name = "index", affinity = "INTEGER", notNull = true)
                .field(name = "shotsPerEnd", affinity = "INTEGER", notNull = true)
                .field(name = "endCount", affinity = "INTEGER", notNull = true)
                .field(name = "distance", notNull = true)
                .field(
                    name = "targetId",
                    columnName = "targetId",
                    affinity = "INTEGER",
                    notNull = true
                )
                .field(
                    name = "targetScoringStyleIndex",
                    columnName = "targetScoringStyle",
                    affinity = "INTEGER",
                    notNull = true
                )
                .field(
                    name = "targetDiameter",
                    columnName = "targetDiameter",
                    notNull = true
                ),

            TableMigrationBuilder("SightMark")
                .field(
                    name = "id",
                    columnName = "_id",
                    affinity = "INTEGER PRIMARY KEY AUTOINCREMENT",
                    notNull = true
                )
                .field(
                    name = "bowId",
                    columnName = "bow",
                    affinity = "INTEGER",
                    refTable = "Bow",
                    refColumn = "id",
                    onDelete = "CASCADE"
                )
                .field(name = "distance", notNull = true)
                .field(name = "value", affinity = "TEXT"),

            TableMigrationBuilder("Signature")
                .field(
                    name = "id",
                    columnName = "_id",
                    affinity = "INTEGER PRIMARY KEY AUTOINCREMENT",
                    notNull = true
                )
                .field(name = "name", notNull = true)
                .field(name = "bitmap", affinity = "BLOB"),

            TableMigrationBuilder("Training")
                .field(
                    name = "id",
                    columnName = "_id",
                    affinity = "INTEGER PRIMARY KEY AUTOINCREMENT",
                    notNull = true
                )
                .field(name = "title", notNull = true)
                .field(name = "date", notNull = true)
                .field(
                    name = "standardRoundId",
                    columnName = "standardRound",
                    affinity = "INTEGER",
                    refTable = "StandardRound",
                    refColumn = "id",
                    onDelete = "SET NULL"
                )
                .field(
                    name = "bowId",
                    columnName = "bow",
                    affinity = "INTEGER",
                    refTable = "Bow",
                    refColumn = "id",
                    onDelete = "SET NULL"
                )
                .field(
                    name = "arrowId",
                    columnName = "arrow",
                    affinity = "INTEGER",
                    refTable = "Arrow",
                    refColumn = "id",
                    onDelete = "SET NULL"
                )
                .field(name = "arrowNumbering", affinity = "INTEGER", notNull = true)
                .field(name = "comment", notNull = true)
                .field(
                    name = "archerSignatureId",
                    columnName = "archerSignature",
                    affinity = "INTEGER",
                    refTable = "Signature",
                    refColumn = "id",
                    onDelete = "SET NULL"
                )
                .field(
                    name = "witnessSignatureId",
                    columnName = "witnessSignature",
                    affinity = "INTEGER",
                    refTable = "Signature",
                    refColumn = "id",
                    onDelete = "SET NULL"
                )
                .field(name = "indoor", affinity = "INTEGER", notNull = true)
                .field(name = "weather", affinity = "INTEGER", notNull = true)
                .field(name = "windSpeed", affinity = "INTEGER", notNull = true)
                .field(name = "windDirection", affinity = "INTEGER", notNull = true)
                .field(name = "location", notNull = true)
                .field(name = "reachedPoints", affinity = "INTEGER", notNull = true)
                .field(name = "totalPoints", affinity = "INTEGER", notNull = true)
                .field(name = "shotCount", affinity = "INTEGER", notNull = true),

            TableMigrationBuilder("Round")
                .field(
                    name = "id",
                    columnName = "_id",
                    affinity = "INTEGER PRIMARY KEY AUTOINCREMENT",
                    notNull = true
                )
                .field(
                    name = "trainingId",
                    columnName = "training",
                    affinity = "INTEGER",
                    refTable = "Training",
                    refColumn = "id",
                    onDelete = "CASCADE"
                )
                .field(name = "index", affinity = "INTEGER", notNull = true)
                .field(name = "shotsPerEnd", affinity = "INTEGER", notNull = true)
                .field(name = "maxEndCount", affinity = "INTEGER")
                .field(name = "distance", notNull = true)
                .field(name = "comment", notNull = true)
                .field(
                    name = "targetId",
                    affinity = "INTEGER",
                    notNull = true
                )
                .field(
                    name = "targetScoringStyleIndex",
                    columnName = "targetScoringStyle",
                    affinity = "INTEGER",
                    notNull = true
                )
                .field(name = "targetDiameter", notNull = true)
                .field(name = "reachedPoints", affinity = "INTEGER", notNull = true)
                .field(name = "totalPoints", affinity = "INTEGER", notNull = true)
                .field(name = "shotCount", affinity = "INTEGER", notNull = true),

            TableMigrationBuilder("End")
                .field(
                    name = "id",
                    columnName = "_id",
                    affinity = "INTEGER PRIMARY KEY AUTOINCREMENT",
                    notNull = true
                )
                .field(name = "index", affinity = "INTEGER", notNull = true)
                .field(
                    name = "roundId",
                    columnName = "round",
                    affinity = "INTEGER",
                    refTable = "Round",
                    refColumn = "id",
                    onDelete = "CASCADE"
                )
                .field(name = "exact", affinity = "INTEGER", notNull = true)
                .field(name = "saveTime", affinity = "TEXT")
                .field(name = "comment", notNull = true)
                .field(name = "reachedPoints", affinity = "INTEGER", notNull = true)
                .field(name = "totalPoints", affinity = "INTEGER", notNull = true)
                .field(name = "shotCount", affinity = "INTEGER", notNull = true),

            TableMigrationBuilder("EndImage")
                .field(
                    name = "id",
                    columnName = "_id",
                    affinity = "INTEGER PRIMARY KEY AUTOINCREMENT",
                    notNull = true
                )
                .field(name = "fileName", notNull = true)
                .field(
                    name = "endId",
                    columnName = "end",
                    affinity = "INTEGER",
                    refTable = "End",
                    refColumn = "id",
                    onDelete = "CASCADE"
                ),

            TableMigrationBuilder("Shot")
                .field(
                    name = "id",
                    columnName = "_id",
                    affinity = "INTEGER PRIMARY KEY AUTOINCREMENT",
                    notNull = true
                )
                .field(name = "index", affinity = "INTEGER", notNull = true)
                .field(
                    name = "endId",
                    columnName = "end",
                    affinity = "INTEGER",
                    refTable = "End",
                    refColumn = "id",
                    onDelete = "CASCADE"
                )
                .field(name = "x", affinity = "REAL", notNull = true)
                .field(name = "y", affinity = "REAL", notNull = true)
                .field(name = "scoringRing", affinity = "INTEGER", notNull = true)
                .field(name = "arrowNumber", affinity = "TEXT")
        )

        for (migration in migrations) {
            migration.createNewTable(database)
        }
        for (migration in migrations) {
            migration.copyData(database)
        }
        for (migration in migrations) {
            migration.replaceOldTableWithNewTable(database)
        }
    }
}

class TableMigrationBuilder(val tableName: String) {

    private val newTableName = "NEW_$tableName"

    private val fieldMigrations = mutableListOf<FieldMigration>()

    fun field(
        name: String = "",
        notNull: Boolean = false,
        affinity: String = "TEXT",
        columnName: String? = null,
        refTable: String? = null,
        refColumn: String? = null,
        onUpdate: String = "NO ACTION",
        onDelete: String = "NO ACTION"
    ): TableMigrationBuilder {
        fieldMigrations.add(
            FieldMigration(
                name = "`$name`",
                columnName = "`${columnName ?: name}`",
                affinity = affinity,
                notNull = notNull,
                refTable = refTable,
                refColumn = refColumn,
                onUpdate = onUpdate,
                onDelete = onDelete
            )
        )
        return this
    }

    fun createNewTable(database: SupportSQLiteDatabase) {
        val entries = fieldMigrations.map { it.toString() }.toMutableList()
        entries.addAll(fieldMigrations.filter { it.refTable != null }.map { it.referenceToString() })

        val query =
            "CREATE TABLE IF NOT EXISTS `$newTableName` (" + entries.joinToString(separator = ", ") + ")"
        database.execSQL(query)
    }

    fun copyData(database: SupportSQLiteDatabase) {
        database.execSQL("INSERT INTO `$newTableName` (" + fieldMigrations.joinToString(separator = ", ") { it.name } + ") "
                + "SELECT " + fieldMigrations.joinToString(separator = ", ") { it.selector() }
                + " FROM `$tableName`")
    }

    fun replaceOldTableWithNewTable(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE `$tableName`")
        database.execSQL("ALTER TABLE `$newTableName` RENAME TO `$tableName`")
    }
}

data class FieldMigration(
    val name: String,
    val columnName: String,
    val affinity: String,
    val notNull: Boolean,
    val refTable: String?,
    val refColumn: String?,
    val onUpdate: String,
    val onDelete: String
) {
    override fun toString(): String {
        return "$name $affinity${if (notNull) " NOT NULL" else ""}"
    }

    fun referenceToString(): String {
        return "FOREIGN KEY($name) REFERENCES `$refTable`(`$refColumn`) ON UPDATE $onUpdate ON DELETE $onDelete"
    }

    fun selector(): String {
        if (notNull) {
            return "CASE WHEN $columnName IS NULL THEN ${getDefault()} ELSE $columnName END"
        }
        return columnName
    }

    private fun getDefault(): String {
        return when (affinity) {
            "TEXT" -> "\"\""
            else -> "0"
        }
    }
}

inline fun Cursor.useEach(callback: (Cursor) -> Unit) {
    use {
        while (moveToNext()) {
            callback(this)
        }
    }
}