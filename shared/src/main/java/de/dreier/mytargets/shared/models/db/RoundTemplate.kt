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

import android.os.Parcel
import android.os.Parcelable
import com.raizlabs.android.dbflow.annotation.*
import de.dreier.mytargets.shared.AppDatabase
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.IIdSettable
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.utils.typeconverters.DimensionConverter

@Table(database = AppDatabase::class)
data class RoundTemplate(
        @Column(name = "_id")
        @PrimaryKey(autoincrement = true)
        override var id: Long = 0,

        @ForeignKey(tableClass = StandardRound::class, references = [(ForeignKeyReference(columnName = "standardRound", foreignKeyColumnName = "_id"))], onDelete = ForeignKeyAction.CASCADE)
        var standardRound: Long? = null,

        @Column
        var index: Int = 0,

        @Column
        var shotsPerEnd: Int = 0,

        @Column
        var endCount: Int = 0,

        @Column(typeConverter = DimensionConverter::class)
        var distance: Dimension = Dimension.UNKNOWN,

        @Column
        var targetId: Int = 0,

        @Column
        var targetScoringStyle: Int = 0,

        @Column(typeConverter = DimensionConverter::class)
        var targetDiameter: Dimension = Dimension.UNKNOWN
) : IIdSettable, Parcelable {
    var targetTemplate: Target
        get() = Target(targetId.toLong(), targetScoringStyle, targetDiameter)
        set(targetTemplate) {
            targetId = targetTemplate.id.toInt()
            targetScoringStyle = targetTemplate.scoringStyleIndex
            targetDiameter = targetTemplate.diameter
        }

    constructor(source: Parcel) : this(
            source.readLong(),
            source.readValue(Long::class.java.classLoader) as Long?,
            source.readInt(),
            source.readInt(),
            source.readInt(),
            source.readParcelable<Dimension>(Dimension::class.java.classLoader),
            source.readInt(),
            source.readInt(),
            source.readParcelable<Dimension>(Dimension::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(id)
        writeValue(standardRound)
        writeInt(index)
        writeInt(shotsPerEnd)
        writeInt(endCount)
        writeParcelable(distance, 0)
        writeInt(targetId)
        writeInt(targetScoringStyle)
        writeParcelable(targetDiameter, 0)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<RoundTemplate> = object : Parcelable.Creator<RoundTemplate> {
            override fun createFromParcel(source: Parcel): RoundTemplate = RoundTemplate(source)
            override fun newArray(size: Int): Array<RoundTemplate?> = arrayOfNulls(size)
        }
    }
}
