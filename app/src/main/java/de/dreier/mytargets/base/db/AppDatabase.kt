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

package de.dreier.mytargets.base.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import de.dreier.mytargets.base.db.dao.*
import de.dreier.mytargets.base.db.typeconverters.*
import de.dreier.mytargets.shared.models.db.*

@Database(entities = [
    Arrow::class,
    ArrowImage::class,
    Bow::class,
    BowImage::class,
    End::class,
    EndImage::class,
    Round::class,
    RoundTemplate::class,
    Shot::class,
    SightMark::class,
    Signature::class,
    StandardRound::class,
    Training::class
], version = AppDatabase.VERSION)
@TypeConverters(
        DimensionConverters::class,
        ThumbnailConverters::class,
        LocalDateConverters::class,
        EWeatherConverters::class,
        BitmapConverters::class,
        LocalTimeConverters::class,
        EBowTypeConverters::class
)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val NAME = "database"
        const val DATABASE_FILE_NAME = "$NAME.db"
        const val DATABASE_IMPORT_FILE_NAME = "database"
        const val VERSION = 26
    }

    abstract fun arrowDAO(): ArrowDAO
    abstract fun bowDAO(): BowDAO
    abstract fun dimensionDAO(): DimensionDAO
    abstract fun endDAO(): EndDAO
    abstract fun imageDAO(): ImageDAO
    abstract fun roundDAO(): RoundDAO
    abstract fun signatureDAO(): SignatureDAO
    abstract fun standardRoundDAO(): StandardRoundDAO
    abstract fun trainingDAO(): TrainingDAO
}
