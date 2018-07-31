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

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.SET_NULL
import android.os.Parcelable
import de.dreier.mytargets.shared.models.Environment
import de.dreier.mytargets.shared.models.IIdSettable
import de.dreier.mytargets.shared.models.Score
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

@Parcelize
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Arrow::class,
            parentColumns = ["id"],
            childColumns = ["arrowId"],
            onDelete = SET_NULL
        ),
        ForeignKey(
            entity = Bow::class,
            parentColumns = ["id"],
            childColumns = ["bowId"],
            onDelete = SET_NULL
        ),
        ForeignKey(
            entity = StandardRound::class,
            parentColumns = ["id"],
            childColumns = ["standardRoundId"],
            onDelete = SET_NULL
        ),
        ForeignKey(
            entity = Signature::class,
            parentColumns = ["id"],
            childColumns = ["archerSignatureId"],
            onDelete = SET_NULL
        ),
        ForeignKey(
            entity = Signature::class,
            parentColumns = ["id"],
            childColumns = ["witnessSignatureId"],
            onDelete = SET_NULL
        )
    ],
    indices = [
        Index(value = ["arrowId"]),
        Index(value = ["bowId"]),
        Index(value = ["standardRoundId"]),
        Index(value = ["archerSignatureId"]),
        Index(value = ["witnessSignatureId"])
    ]
)
data class Training(

    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0L,
    var title: String = "",
    var date: LocalDate = LocalDate.now(),
    var standardRoundId: Long? = null,
    var bowId: Long? = null,
    var arrowId: Long? = null,
    var arrowNumbering: Boolean = false,
    @Embedded var environment: Environment = Environment(),
    var comment: String = "",
    var archerSignatureId: Long? = null,
    var witnessSignatureId: Long? = null,
    @Embedded var score: Score = Score()

) : IIdSettable, Parcelable {
    val formattedDate: String
        get() = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
}
