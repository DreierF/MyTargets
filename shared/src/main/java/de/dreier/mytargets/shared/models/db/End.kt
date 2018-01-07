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
import de.dreier.mytargets.shared.AppDatabase
import de.dreier.mytargets.shared.models.IIdSettable
import de.dreier.mytargets.shared.utils.typeconverters.LocalTimeConverter
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalTime

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
) : IIdSettable, Parcelable
