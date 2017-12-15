/*
 * Copyright (C) 2017 Florian Dreier
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
package de.dreier.mytargets.shared.models.db

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.OneToMany
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.BaseModel
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper
import de.dreier.mytargets.shared.AppDatabase
import de.dreier.mytargets.shared.R
import de.dreier.mytargets.shared.models.IDetailProvider
import de.dreier.mytargets.shared.models.IIdSettable
import de.dreier.mytargets.shared.models.IImageProvider
import de.dreier.mytargets.shared.targets.models.CombinedSpot
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
@Table(database = AppDatabase::class)
data class StandardRound(
        @Column(name = "_id")
        @PrimaryKey(autoincrement = true)
        override var id: Long? = null,

        @Column
        var club: Int = 0,

        @Column
        override var name: String = ""
) : BaseModel(), IIdSettable, IImageProvider, IDetailProvider, Comparable<StandardRound>, Parcelable {

    @Transient internal var rounds: MutableList<RoundTemplate>? = null

    val targetDrawable: Drawable
        get() {
            val targets = loadRounds()?.map { it.targetTemplate.drawable }
            return CombinedSpot(targets)
        }

    fun insert(template: RoundTemplate) {
        template.index = loadRounds()!!.size
        template.standardRound = id
        loadRounds()!!.add(template)
    }

    fun getDescription(context: Context): String {
        var desc = ""
        for (r in loadRounds()!!) {
            if (!desc.isEmpty()) {
                desc += "\n"
            }
            desc += context.getString(R.string.round_desc, r.distance, r.endCount,
                    r.shotsPerEnd, r.targetTemplate.diameter)
        }
        return desc
    }

    @OneToMany(methods = [], variableName = "rounds")
    fun loadRounds(): MutableList<RoundTemplate>? {
        if (rounds == null) {
            rounds = SQLite.select()
                    .from(RoundTemplate::class.java)
                    .where(RoundTemplate_Table.standardRound.eq(id))
                    .queryList()
                    .toMutableList()
        }
        return rounds
    }

    fun setRounds(rounds: MutableList<RoundTemplate>) {
        this.rounds = rounds
    }

    override fun getDrawable(context: Context): Drawable {
        return targetDrawable
    }

    override fun getDetails(context: Context): String {
        return getDescription(context)
    }

    override fun compareTo(other: StandardRound) = compareBy(StandardRound::name, StandardRound::id).compare(this, other)

    override fun save() {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction({ this.save(it) })
    }

    override fun save(databaseWrapper: DatabaseWrapper) {
        super.save(databaseWrapper)
        if (rounds != null) {
            SQLite.delete(RoundTemplate::class.java)
                    .where(RoundTemplate_Table.standardRound.eq(id))
                    .execute(databaseWrapper)
            // TODO Replace this super ugly workaround by stubbed Relationship in version 4 of dbFlow
            for (s in rounds!!) {
                s.standardRound = id
                s.save(databaseWrapper)
            }
        }
    }

    override fun delete() {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction({ this.delete(it) })
    }

    override fun delete(databaseWrapper: DatabaseWrapper) {
        for (roundTemplate in loadRounds()!!) {
            roundTemplate.delete(databaseWrapper)
        }
        super.delete(databaseWrapper)
    }

    companion object {

        operator fun get(id: Long?): StandardRound? {
            return SQLite.select()
                    .from(StandardRound::class.java)
                    .where(StandardRound_Table._id.eq(id))
                    .querySingle()
        }

        val all: List<StandardRound>
            get() = SQLite.select().from(StandardRound::class.java).queryList()

        fun getAllSearch(query: String): List<StandardRound> {
            val queryString = "%" + query.replace(' ', '%') + "%"
            return SQLite.select()
                    .from(StandardRound::class.java)
                    .where(StandardRound_Table.name.like(queryString))
                    .and(StandardRound_Table.club.notEq(512))
                    .queryList()
        }
    }
}
