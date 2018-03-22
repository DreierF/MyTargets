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
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.models.db.Shot

object Migration26 : Migration(25, 26) {
    override fun migrate(database: SupportSQLiteDatabase) {

        database.execSQL("ALTER TABLE `Training` ADD COLUMN reachedPoints INTEGER;")
        database.execSQL("ALTER TABLE `Training` ADD COLUMN totalPoints INTEGER;")
        database.execSQL("ALTER TABLE `Training` ADD COLUMN shotCount INTEGER;")

        database.execSQL("ALTER TABLE `Round` ADD COLUMN reachedPoints INTEGER;")
        database.execSQL("ALTER TABLE `Round` ADD COLUMN totalPoints INTEGER;")
        database.execSQL("ALTER TABLE `Round` ADD COLUMN shotCount INTEGER;")

        database.execSQL("ALTER TABLE `End` ADD COLUMN reachedPoints INTEGER;")
        database.execSQL("ALTER TABLE `End` ADD COLUMN totalPoints INTEGER;")
        database.execSQL("ALTER TABLE `End` ADD COLUMN shotCount INTEGER;")

        RoomCreationCallback.createScoreTriggers(database)

        val rounds = database.query(
            "SELECT id, targetId, targetScoringStyle, targetDiameter " +
                    "FROM Round"
        )

        while (rounds.moveToNext()) {
            val diameterData = rounds.getString(3)
            val index = diameterData.indexOf(' ')
            val value = diameterData.substring(0, index)
            val unit = diameterData.substring(index + 1)
            val diameter = Dimension.from(value.toFloat(), unit)
            val target = Target(rounds.getLong(1), rounds.getInt(2), diameter)
            val roundId = rounds.getLong(0)

            val ends = database.query("SELECT _id FROM `End` WHERE round = $roundId")
            while (ends.moveToNext()) {
                val endId = ends.getLong(0)

                val shotsCursor =
                    database.query("SELECT `index`, scoringRing FROM Shot WHERE end = $endId ORDER BY `index`")
                val shots = mutableListOf<Shot>()
                while (shotsCursor.moveToNext()) {
                    val shot =
                        Shot(index = shotsCursor.getInt(0), scoringRing = shotsCursor.getInt(1))
                    shots.add(shot)
                }

                val score = target.getReachedScore(shots)
                database.execSQL(
                    "UPDATE `End` SET " +
                            "reachedPoints = ${score.reachedPoints}, " +
                            "totalPoints = ${score.totalPoints}, " +
                            "shotCount = ${score.shotCount} " +
                            "WHERE _id = $endId"
                )
            }
        }

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
                    columnName = "standardRoundId",
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
                    columnName = "standardRoundId",
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
                    name = "target.id",
                    columnName = "targetId",
                    affinity = "INTEGER",
                    notNull = true
                )
                .field(
                    name = "target.scoringStyleIndex",
                    columnName = "targetScoringStyle",
                    affinity = "INTEGER",
                    notNull = true
                )
                .field(name = "target.diameter", columnName = "targetDiameter", notNull = true)
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
                name = name,
                columnName = columnName ?: name,
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
                + "SELECT " + fieldMigrations.joinToString(separator = ", ") { it.columnName }
                + " FROM $tableName")
    }

    fun replaceOldTableWithNewTable(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE $tableName")
        database.execSQL("ALTER TABLE $newTableName RENAME TO $tableName")
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
        return "`$name` $affinity${if (notNull) " NOT NULL" else ""}"
    }

    fun referenceToString(): String {
        return "FOREIGN KEY(`$name`) REFERENCES `$refTable`(`$refColumn`) ON UPDATE $onUpdate ON DELETE $onDelete"
    }
}
