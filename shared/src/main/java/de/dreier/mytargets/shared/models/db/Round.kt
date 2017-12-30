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
import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.BaseModel
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper
import de.dreier.mytargets.shared.AppDatabase
import de.dreier.mytargets.shared.models.*
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.utils.typeconverters.DimensionConverter
import kotlinx.android.parcel.Parcelize
import java.util.*

@SuppressLint("ParcelCreator")
@Parcelize
@Table(database = AppDatabase::class)
data class Round(
        @Column(name = "_id")
        @PrimaryKey(autoincrement = true)
        override var id: Long = 0,

        @ForeignKey(tableClass = Training::class, references = [(ForeignKeyReference(columnName = "training", foreignKeyColumnName = "_id"))], onDelete = ForeignKeyAction.CASCADE)
        var trainingId: Long? = null,

        @Column
        var index: Int = 0,

        @Column
        var shotsPerEnd: Int = 0,

        @Column
        var maxEndCount: Int? = null,

        @Column(typeConverter = DimensionConverter::class)
        var distance: Dimension = Dimension.UNKNOWN,

        @Column
        var comment: String = "",

        @Column
        var targetId: Int = 0,

        @Column
        var targetScoringStyle: Int = 0,

        @Column(typeConverter = DimensionConverter::class)
        var targetDiameter: Dimension = Dimension.UNKNOWN
) : BaseModel(), IIdSettable, Comparable<Round>, IRecursiveModel, Parcelable {

    @Transient
    var ends: MutableList<End>? = null

    constructor(info: RoundTemplate) : this(
            distance = info.distance,
            shotsPerEnd = info.shotsPerEnd,
            maxEndCount = info.endCount,
            index = info.index,
            targetId = info.targetId,
            targetScoringStyle = info.targetScoringStyle,
            targetDiameter = info.targetDiameter
    )

    constructor(round: Round) : this(
            id = round.id,
            trainingId = round.trainingId,
            index = round.index,
            shotsPerEnd = round.shotsPerEnd,
            maxEndCount = round.maxEndCount,
            distance = round.distance,
            comment = round.comment,
            targetId = round.targetId,
            targetScoringStyle = round.targetScoringStyle,
            targetDiameter = round.targetDiameter
    ) {
        ends = round.ends
    }

    var target: Target
        get() = Target(targetId.toLong(), targetScoringStyle, targetDiameter)
        set(targetTemplate) {
            targetId = targetTemplate.id.toInt()
            targetScoringStyle = targetTemplate.scoringStyleIndex
            targetDiameter = targetTemplate.diameter
        }

    val reachedScore: Score
        get() {
            val target = target
            return loadEnds().
                    map { end: End -> target.getReachedScore(end.loadShots()) }
                    .sum()
        }

    val training: Training
        get() = Training[trainingId!!]!!

    override fun delete(): Boolean {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction({ this.delete(it) })
        return true
    }

    override fun delete(databaseWrapper: DatabaseWrapper): Boolean {
        loadEnds().forEach { it.delete(databaseWrapper) }
        super.delete(databaseWrapper)
        updateRoundIndicesForTraining(databaseWrapper)
        return true
    }

    private fun updateRoundIndicesForTraining(databaseWrapper: DatabaseWrapper) {
        // TODO very inefficient
        val training = Training[trainingId!!] ?: return  //FIXME This should not happen, but does for some users
        for ((i, r) in training.loadRounds().withIndex()) {
            r.index = i
            r.save(databaseWrapper)
        }
    }

    @OneToMany(methods = [], variableName = "ends")
    fun loadEnds(): MutableList<End> {
        if (ends == null) {
            ends = SQLite.select()
                    .from(End::class.java)
                    .where(End_Table.round.eq(id))
                    .orderBy(End_Table.index, true)
                    .queryList().toMutableList()
        }
        return ends!!
    }

    fun loadEnds(databaseWrapper: DatabaseWrapper): List<End> {
        return SQLite.select()
                .from(End::class.java)
                .where(End_Table.round.eq(id))
                .orderBy(End_Table.index, true)
                .queryList(databaseWrapper)
    }

    override fun compareTo(other: Round): Int {
        return index - other.index
    }

    override fun saveRecursively() {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction({ this.saveRecursively(it) })
    }

    /**
     * Saves this round and all of its ends and updates all sibling round indices.
     * Gets called when deletion of a round has been canceled by the user via undo
     * or when deleting a training has been canceled.
     */
    override fun saveRecursively(databaseWrapper: DatabaseWrapper) {
        val training = Training[trainingId!!]!!
        val rounds = training.loadRounds(databaseWrapper).toMutableList()

        val pos = Collections.binarySearch(rounds, this)
        if (pos < 0) {
            rounds.add(-pos - 1, this)
        } else {
            rounds.add(pos, this)
        }

        for ((i, round) in rounds.withIndex()) {
            round.index = i
            round.save(databaseWrapper)
        }
        for (end in ends!!) {
            end.roundId = id
            end.save(databaseWrapper)
        }
    }

    companion object {

        operator fun get(id: Long): Round? {
            return SQLite.select()
                    .from(Round::class.java)
                    .where(Round_Table._id.eq(id))
                    .querySingle()
        }

        fun getAll(roundIds: LongArray): List<Round> {
            return SQLite.select()
                    .from(Round::class.java)
                    .where(Round_Table._id.`in`(roundIds.toList()))
                    .queryList()
        }
    }
}
