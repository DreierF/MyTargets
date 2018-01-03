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
import com.raizlabs.android.dbflow.structure.BaseModel
import de.dreier.mytargets.shared.AppDatabase
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
@Table(database = AppDatabase::class)
data class ArrowImage(
        @Column(name = "_id")
        @PrimaryKey(autoincrement = true)
        var id: Long = 0,

        @Column
        override var fileName: String = "",

        @ForeignKey(tableClass = Arrow::class, references = [(ForeignKeyReference(columnName = "arrow", foreignKeyColumnName = "_id"))], onDelete = ForeignKeyAction.CASCADE)
        var arrowId: Long? = null
) : BaseModel(), Image, Parcelable
