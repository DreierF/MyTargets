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

package de.dreier.mytargets.shared.models.db

import android.annotation.SuppressLint
import android.os.Parcelable
import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.structure.BaseModel
import de.dreier.mytargets.shared.AppDatabase
import de.dreier.mytargets.shared.models.*
import de.dreier.mytargets.shared.models.dao.ArrowDAO
import de.dreier.mytargets.shared.models.dao.BowDAO
import de.dreier.mytargets.shared.models.dao.SignatureDAO
import de.dreier.mytargets.shared.models.dao.TrainingDAO
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
        override var id: Long = 0L,

        @Column
        var title: String = "",

        @Column(typeConverter = LocalDateConverter::class)
        var date: LocalDate = LocalDate.now(),

        @ForeignKey(tableClass = StandardRound::class, references = [(ForeignKeyReference(columnName = "standardRound", foreignKeyColumnName = "_id"))], onDelete = ForeignKeyAction.SET_NULL)
        var standardRoundId: Long? = null,

        @ForeignKey(tableClass = Bow::class, references = [(ForeignKeyReference(columnName = "bow", foreignKeyColumnName = "_id"))], onDelete = ForeignKeyAction.SET_NULL)
        var bowId: Long? = null,

        @ForeignKey(tableClass = Arrow::class, references = [(ForeignKeyReference(columnName = "arrow", foreignKeyColumnName = "_id"))], onDelete = ForeignKeyAction.SET_NULL)
        var arrowId: Long? = null,

        @Column(getterName = "getArrowNumbering", setterName = "setArrowNumbering")
        var arrowNumbering: Boolean = false,

        @Column(getterName = "getIndoor", setterName = "setIndoor")
        var indoor: Boolean = false,

        @Column(typeConverter = EWeatherConverter::class)
        var weather: EWeather = EWeather.SUNNY,

        @Column
        var windDirection: Int = 0,

        @Column
        var windSpeed: Int = 0,

        @Column
        var location: String = "",

        @Column
        var comment: String = "",

        @ForeignKey(tableClass = Signature::class, references = [(ForeignKeyReference(columnName = "archerSignature", foreignKeyColumnName = "_id"))], onDelete = ForeignKeyAction.SET_NULL)
        var archerSignatureId: Long? = null,

        @ForeignKey(tableClass = Signature::class, references = [(ForeignKeyReference(columnName = "witnessSignature", foreignKeyColumnName = "_id"))], onDelete = ForeignKeyAction.SET_NULL)
        var witnessSignatureId: Long? = null
) : BaseModel(), IIdSettable, Parcelable {

    var environment: Environment
        get() = Environment(indoor, weather, windSpeed, windDirection, location)
        set(env) {
            indoor = env.indoor
            weather = env.weather
            windDirection = env.windDirection
            windSpeed = env.windSpeed
            location = env.location
        }

    val bow: Bow?
        get() = if (bowId == null) null else BowDAO.loadBowOrNull(bowId!!)

    val arrow: Arrow?
        get() = if (arrowId == null) null else ArrowDAO.loadArrowOrNull(arrowId!!)

    val orCreateArcherSignature: Signature
        get() {
            if (archerSignatureId != null) {
                val signature = SignatureDAO.loadSignatureOrNull(archerSignatureId!!)
                if (signature != null) {
                    return signature
                }
            }
            val signature = Signature()
            SignatureDAO.saveSignature(signature)
            archerSignatureId = signature.id
            TrainingDAO.saveTraining(this)
            return signature
        }

    val orCreateWitnessSignature: Signature
        get() {
            if (witnessSignatureId != null) {
                val signature = SignatureDAO.loadSignatureOrNull(witnessSignatureId!!)
                if (signature != null) {
                    return signature
                }
            }
            val signature = Signature()
            SignatureDAO.saveSignature(signature)
            witnessSignatureId = signature.id
            TrainingDAO.saveTraining(this)
            return signature
        }

    val formattedDate: String
        get() = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))

    val reachedScore: Score
        get() = TrainingDAO.loadRounds(id)
                .map { it.reachedScore }
                .sum()
}
