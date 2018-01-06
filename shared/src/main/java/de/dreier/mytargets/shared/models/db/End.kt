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
import android.os.Parcelable
import android.support.v4.util.Pair
import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.BaseModel
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper
import de.dreier.mytargets.shared.AppDatabase
import de.dreier.mytargets.shared.models.IIdSettable
import de.dreier.mytargets.shared.models.SelectableZone
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.utils.typeconverters.LocalTimeConverter
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalTime
import java.util.*

@SuppressLint("ParcelCreator")
@Parcelize
@Table(database = AppDatabase::class)
data class End(

        @Column(name = "_id")
        @PrimaryKey(autoincrement = true)
        override var id: Long = 0,

        @Column
        var index: Int = 0,

        @ForeignKey(tableClass = Round::class, references = [(ForeignKeyReference(columnName = "round", foreignKeyColumnName = "_id"))], onDelete = ForeignKeyAction.CASCADE)
        var roundId: Long? = null,

        @Column(getterName = "getExact", setterName = "setExact")
        var exact: Boolean = false,

        @Column(typeConverter = LocalTimeConverter::class)
        var saveTime: LocalTime? = null,

        @Column
        var comment: String = ""
) : BaseModel(), IIdSettable, Comparable<End>, Parcelable {

    @IgnoredOnParcel
    var images: MutableList<EndImage>? = null

    @IgnoredOnParcel
    internal var shots: MutableList<Shot>? = null

    val isEmpty: Boolean
        get() = loadShots().any { it.scoringRing == Shot.NOTHING_SELECTED } && loadImages().isEmpty()

    constructor(shotCount: Int, index: Int) : this(index = index) {
        shots = (0 until shotCount)
                .map { Shot(it) }
                .toMutableList()
    }

    constructor(end: End) : this(index = end.index) {
        this.shots = ArrayList(end.loadShots())
    }

    @OneToMany(methods = [], variableName = "shots")
    fun loadShots(): MutableList<Shot> {
        if (shots == null) {
            shots = SQLite.select()
                    .from(Shot::class.java)
                    .where(Shot_Table.end.eq(id))
                    .queryList().toMutableList()
        }
        return shots!!
    }

    @OneToMany(methods = [], variableName = "images")
    fun loadImages(): MutableList<EndImage> {
        if (images == null) {
            images = SQLite.select()
                    .from(EndImage::class.java)
                    .where(EndImage_Table.end.eq(id))
                    .queryList().toMutableList()
        }
        return images!!
    }

    override fun save(): Boolean {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction({ this.save(it) })
        return true
    }

    override fun save(databaseWrapper: DatabaseWrapper): Boolean {
        if (saveTime == null) {
            saveTime = LocalTime.now()
        }
        super.save(databaseWrapper)
        if (shots != null) {
            SQLite.delete(Shot::class.java)
                    .where(Shot_Table.end.eq(id))
                    .execute(databaseWrapper)
            // TODO Replace this super ugly workaround by stubbed Relationship in version 4 of dbFlow
            for (s in shots!!) {
                s.endId = id
                s.save(databaseWrapper)
            }
        }
        if (images != null) {
            SQLite.delete(EndImage::class.java)
                    .where(EndImage_Table.end.eq(id))
                    .execute(databaseWrapper)
            // TODO Replace this super ugly workaround by stubbed Relationship in version 4 of dbFlow
            loadImages().forEach { image ->
                image.endId = id
                image.save(databaseWrapper)
            }
        }
        return true
    }

    override fun delete(): Boolean {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction({ this.delete(it) })
        return true
    }

    override fun delete(databaseWrapper: DatabaseWrapper): Boolean {
        loadShots().forEach { it.delete(databaseWrapper) }
        loadImages().forEach { it.delete(databaseWrapper) }
        super.delete(databaseWrapper)
        updateEndIndicesForRound(databaseWrapper)
        return true
    }

    private fun updateEndIndicesForRound(databaseWrapper: DatabaseWrapper) {
        // FIXME very inefficient
        val round = Round[roundId!!] ?: return

        for ((i, end) in round.loadEnds(databaseWrapper).withIndex()) {
            end.index = i
            end.save(databaseWrapper)
        }
    }

    override fun compareTo(other: End) = compareBy(End::index).compare(this, other)

    fun saveRecursively() {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction({ this.saveRecursively(it) })
    }

    private fun saveRecursively(databaseWrapper: DatabaseWrapper) {
        val round = Round[roundId!!]
        val ends = round!!.loadEnds(databaseWrapper).toMutableList()

        val pos = Collections.binarySearch(ends, this)
        if (pos < 0) {
            ends.add(-pos - 1, this)
        } else {
            ends.add(pos, this)
        }

        for ((i, end) in ends.withIndex()) {
            end.index = i
            end.save(databaseWrapper)
        }
    }

    companion object {

        private fun getRoundScores(rounds: List<Round>): Map<SelectableZone, Int> {
            val t = rounds[0].target
            val scoreCount = getAllPossibleZones(t)
            rounds.flatMap { it.loadEnds() }
                    .forEach {
                        it.loadShots().forEach { s ->
                                    if (s.scoringRing != Shot.NOTHING_SELECTED) {
                                        val tuple = SelectableZone(s.scoringRing,
                                                t.model.getZone(s.scoringRing),
                                                t.zoneToString(s.scoringRing, s.index),
                                                t.getScoreByZone(s.scoringRing, s.index))
                                        val integer = scoreCount[tuple]
                                        if (integer != null) {
                                            val count = integer + 1
                                            scoreCount[tuple] = count
                                        }
                                    }
                                }
                    }
            return scoreCount
        }

        private fun getAllPossibleZones(t: Target): MutableMap<SelectableZone, Int> {
            val scoreCount = HashMap<SelectableZone, Int>()
            for (arrow in 0..2) {
                val zoneList = t.getSelectableZoneList(arrow)
                for (selectableZone in zoneList) {
                    scoreCount[selectableZone] = 0
                }
                if (!t.model.dependsOnArrowIndex()) {
                    break
                }
            }
            return scoreCount
        }

        fun getTopScoreDistribution(sortedScore: List<Map.Entry<SelectableZone, Int>>): List<Pair<String, Int>> {
            val result = sortedScore.map { Pair(it.key.text, it.value) }.toMutableList()

            // Collapse first two entries if they yield the same score points,
            // e.g. 10 and X => {X, 10+X, 9, ...}
            if (sortedScore.size > 1) {
                val first = sortedScore[0]
                val second = sortedScore[1]
                if (first.key.points == second.key.points) {
                    val newTitle = second.key.text + "+" + first.key.text
                    result[1] = Pair(newTitle, second.value + first.value)
                }
            }
            return result
        }

        /**
         * Compound 9ers are already collapsed to one SelectableZone.
         */
        fun getSortedScoreDistribution(rounds: List<Round>): List<Map.Entry<SelectableZone, Int>> {
            return getRoundScores(rounds).entries.sortedBy { it.key }
        }
    }
}
