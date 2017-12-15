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
import de.dreier.mytargets.shared.utils.typeconverters.EWeatherConverter
import de.dreier.mytargets.shared.utils.typeconverters.LocalDateConverter
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

@SuppressLint("ParcelCreator")
@Parcelize
@Table(database = AppDatabase::class)
data class Training(

        @Column(name = "_id")
        @PrimaryKey(autoincrement = true)
        override var id: Long? = null,

        @Column
        var title: String? = "",

        @Column(typeConverter = LocalDateConverter::class)
        var date: LocalDate? = null,

        @ForeignKey(tableClass = StandardRound::class, references = [(ForeignKeyReference(columnName = "standardRound", columnType = Long::class, foreignKeyColumnName = "_id"))], onDelete = ForeignKeyAction.SET_NULL)
        var standardRoundId: Long? = null,

        @ForeignKey(tableClass = Bow::class, references = [(ForeignKeyReference(columnName = "bow", columnType = Long::class, foreignKeyColumnName = "_id"))], onDelete = ForeignKeyAction.SET_NULL)
        var bowId: Long? = null,

        @ForeignKey(tableClass = Arrow::class, references = [(ForeignKeyReference(columnName = "arrow", columnType = Long::class, foreignKeyColumnName = "_id"))], onDelete = ForeignKeyAction.SET_NULL)
        var arrowId: Long? = null,

        @Column(getterName = "getArrowNumbering", setterName = "setArrowNumbering")
        var arrowNumbering: Boolean = false,

        @Column(getterName = "getIndoor", setterName = "setIndoor")
        var indoor: Boolean = false,

        @Column(typeConverter = EWeatherConverter::class)
        var weather: EWeather? = null,

        @Column
        var windDirection: Int = 0,

        @Column
        var windSpeed: Int = 0,

        @Column
        var location: String? = "",

        @Column
        var comment: String = "",

        @ForeignKey(tableClass = Signature::class, references = [(ForeignKeyReference(columnName = "archerSignature", columnType = Long::class, foreignKeyColumnName = "_id"))], onDelete = ForeignKeyAction.SET_NULL)
        var archerSignatureId: Long? = null,

        @ForeignKey(tableClass = Signature::class, references = [(ForeignKeyReference(columnName = "witnessSignature", columnType = Long::class, foreignKeyColumnName = "_id"))], onDelete = ForeignKeyAction.SET_NULL)
        var witnessSignatureId: Long? = null
) : BaseModel(), IIdSettable, Comparable<Training>, IRecursiveModel, Parcelable {

    @Transient
    var rounds: MutableList<Round>? = null

    var environment: Environment
        get() = Environment(indoor, weather!!, windSpeed, windDirection, location!!)
        set(env) {
            indoor = env.indoor
            weather = env.weather
            windDirection = env.windDirection
            windSpeed = env.windSpeed
            location = env.location
        }

    val standardRound: StandardRound?
        get() = SQLite.select()
                .from(StandardRound::class.java)
                .where(StandardRound_Table._id.eq(standardRoundId))
                .querySingle()

    val bow: Bow?
        get() = SQLite.select()
                .from(Bow::class.java)
                .where(Bow_Table._id.eq(bowId))
                .querySingle()

    val arrow: Arrow?
        get() = SQLite.select()
                .from(Arrow::class.java)
                .where(Arrow_Table._id.eq(arrowId))
                .querySingle()

    val orCreateArcherSignature: Signature
        get() {
            if (archerSignatureId != null) {
                val signature = SQLite.select()
                        .from(Signature::class.java)
                        .where(Signature_Table._id.eq(archerSignatureId))
                        .querySingle()
                if (signature != null) {
                    return signature
                }
            }
            val signature = Signature()
            signature.save()
            archerSignatureId = signature._id
            save()
            return signature
        }

    val orCreateWitnessSignature: Signature
        get() {
            if (witnessSignatureId != null) {
                val signature = SQLite.select()
                        .from(Signature::class.java)
                        .where(Signature_Table._id.eq(witnessSignatureId))
                        .querySingle()
                if (signature != null) {
                    return signature
                }
            }
            val signature = Signature()
            signature.save()
            witnessSignatureId = signature._id
            save()
            return signature
        }

    val formattedDate: String
        get() = date!!.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))

    val reachedScore: Score
        get() = loadRounds()!!
                .map { obj: Round -> obj.reachedScore }
                .fold(Score()) { score, s ->
                    score.add(s)
                }

    @OneToMany(methods = [], variableName = "rounds")
    fun loadRounds(): List<Round>? {
        if (rounds == null) {
            rounds = SQLite.select()
                    .from(Round::class.java)
                    .where(Round_Table.training.eq(id!!))
                    .orderBy(Round_Table.index, true)
                    .queryList()
        }
        return rounds
    }

    override fun compareTo(other: Training): Int {
        return if (date == other.date) {
            (id!! - other.id!!).toInt()
        } else date!!.compareTo(other.date!!)
    }

    override fun save() {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction({ this.save(it) })
    }

    override fun save(databaseWrapper: DatabaseWrapper) {
        super.save(databaseWrapper)
        // TODO Replace this super ugly workaround by stubbed Relationship in version 4 of dbFlow
        loadRounds()?.forEach { s ->
            s.trainingId = id
            s.save(databaseWrapper)
        }
    }

    override fun delete() {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction({ this.delete(it) })
    }

    override fun delete(databaseWrapper: DatabaseWrapper) {
        loadRounds()?.forEach { round -> round.delete(databaseWrapper) }
        super.delete(databaseWrapper)
    }

    @Deprecated(message = "Use AugmentedTraining instead")
    fun ensureLoaded(): Training {
        loadRounds()?.forEach { round ->
            round.loadEnds()?.forEach { end -> end.loadShots() }
        }
        return this
    }

    fun initRoundsFromTemplate(standardRound: StandardRound) {
        rounds = mutableListOf()
        for (template in standardRound.loadRounds()) {
            val round = Round(template)
            round.trainingId = id
            round.target = template.targetTemplate!!
            round.comment = ""
            round.save()
            rounds!!.add(round)
        }
    }

    override fun saveRecursively() {
        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction({ this.saveRecursively(it) })
    }

    override fun saveRecursively(databaseWrapper: DatabaseWrapper) {
        super.save(databaseWrapper)
        loadRounds()?.forEach { s ->
            s.trainingId = id
            s.saveRecursively(databaseWrapper)
        }
    }

    fun loadRounds(databaseWrapper: DatabaseWrapper): List<Round> {
        return SQLite.select()
                .from(Round::class.java)
                .where(Round_Table.training.eq(id!!))
                .orderBy(Round_Table.index, true)
                .queryList(databaseWrapper)
    }

    companion object {

        operator fun get(id: Long?): Training? {
            return SQLite.select()
                    .from(Training::class.java)
                    .where(Training_Table._id.eq(id))
                    .querySingle()
        }

        val all: List<Training>
            get() = SQLite.select().from(Training::class.java).queryList()
    }
}
