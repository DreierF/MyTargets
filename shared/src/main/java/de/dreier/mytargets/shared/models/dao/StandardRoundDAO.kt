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

package de.dreier.mytargets.shared.models.dao

import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper
import de.dreier.mytargets.shared.AppDatabase
import de.dreier.mytargets.shared.models.db.RoundTemplate
import de.dreier.mytargets.shared.models.db.RoundTemplate_Table
import de.dreier.mytargets.shared.models.db.StandardRound
import de.dreier.mytargets.shared.models.db.StandardRound_Table

object StandardRoundDAO {
    fun loadStandardRounds(): List<StandardRound> = SQLite.select().from(StandardRound::class.java).queryList()

    fun loadStandardRound(id: Long): StandardRound = SQLite.select()
            .from(StandardRound::class.java)
            .where(StandardRound_Table._id.eq(id))
            .querySingle() ?: throw IllegalStateException("StandardRound $id does not exist")

    fun loadStandardRoundOrNull(id: Long): StandardRound? = SQLite.select()
            .from(StandardRound::class.java)
            .where(StandardRound_Table._id.eq(id))
            .querySingle()

    fun getAllSearch(query: String): List<StandardRound> {
        val queryString = "%${query.replace(' ', '%')}%"
        return SQLite.select()
                .from(StandardRound::class.java)
                .where(StandardRound_Table.name.like(queryString))
                .and(StandardRound_Table.club.notEq(512))
                .queryList()
    }

    fun loadRoundTemplates(id: Long): ArrayList<RoundTemplate> = ArrayList(SQLite.select()
            .from(RoundTemplate::class.java)
            .where(RoundTemplate_Table.standardRound.eq(id))
            .queryList()
            .sortedBy { roundTemplate -> roundTemplate.distance }
            .toMutableList())

    fun saveStandardRound(standardRound: StandardRound, roundTemplates: List<RoundTemplate>) {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction { db ->
            saveStandardRound(db, standardRound, roundTemplates)
        }
    }

    fun saveStandardRound(db: DatabaseWrapper, standardRound: StandardRound, roundTemplates: List<RoundTemplate>) {
        standardRound.save(db)
        SQLite.delete(RoundTemplate::class.java)
                .where(RoundTemplate_Table.standardRound.eq(standardRound.id))
                .execute(db)
        for (roundTemplate in roundTemplates) {
            roundTemplate.standardRound = standardRound.id
            roundTemplate.save(db)
        }
    }

    fun deleteStandardRound(standardRound: StandardRound) {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction { db ->
            SQLite.delete(RoundTemplate::class.java)
                    .where(RoundTemplate_Table.standardRound.eq(standardRound.id))
                    .execute(db)
            standardRound.delete(db)
        }
    }
}
