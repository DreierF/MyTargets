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

package de.dreier.mytargets.app

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.support.test.InstrumentationRegistry.getContext
import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4
import com.google.common.truth.Truth
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.structure.database.AndroidDatabase
import com.raizlabs.android.dbflow.structure.database.DatabaseHelperDelegate
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper
import de.dreier.mytargets.features.settings.backup.provider.BackupUtils
import de.dreier.mytargets.shared.AppDatabase
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER
import de.dreier.mytargets.shared.models.Dimension.Unit.METER
import de.dreier.mytargets.shared.models.EWeather
import de.dreier.mytargets.shared.models.dao.BowDAO
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.Training
import de.dreier.mytargets.test.base.InstrumentedTestBase
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

@SmallTest
@RunWith(AndroidJUnit4::class)
class MigrationTest : InstrumentedTestBase() {

    /**
     * Handle to a database based on schema 16 is updated to the current schema during setUp.
     */
    private var upgradedDb: DatabaseWrapper? = null

    /**
     * A sqlite open helper for the app's data layer.
     */
    private var helper: DatabaseHelperDelegate? = null

    @Before
    @Throws(IOException::class)
    fun setUp() {
        ApplicationInstance.initFlowManager(getTargetContext())

        // Create artificial image file to ensure the according database entry is not deleted (Migration23)
        getTargetContext()
                .openFileOutput("img175420839671886584-0f61-43a6-bc1e-dbcd8526056c794370927.jpg", Context.MODE_PRIVATE)
                .close()

        val tmpDb = getTargetContext().getDatabasePath(DatabaseHelperDelegate
                .getTempDbFileName(FlowManager.getDatabase(AppDatabase.NAME)))

        BackupUtils.copy(getContext().assets.open("database.db"), FileOutputStream(tmpDb))

        helper = FlowManager.getDatabase(AppDatabase.NAME).helper.delegate
        upgradedDb = FlowManager.getDatabase(AppDatabase.NAME).writableDatabase
    }

    @After
    fun cleanup() {
        FlowManager.reset()
        FlowManager.destroy()
    }

    @Test
    @Throws(IOException::class)
    fun upgradeShouldKeepData() {
        val trainings = Training.all
        assertTraining1(trainings[0])
        assertTraining2(trainings[1])

        val bows = BowDAO.loadBows()
        Truth.assertThat(BowDAO.loadBowImages(bows[0].id)[0].fileName)
                .isEqualTo("img175420839671886584-0f61-43a6-bc1e-dbcd8526056c794370927.jpg")
    }

    private fun assertTraining1(training: Training) {
        Truth.assertThat(training.id).isEqualTo(1)
        Truth.assertThat(training.title).isEqualTo("Training im Dez")
        Truth.assertThat(training.standardRoundId).isEqualTo(null)
        Truth.assertThat(training.bowId).isEqualTo(null)
        Truth.assertThat(training.arrowId).isEqualTo(null)
        Truth.assertThat(training.arrowNumbering).isEqualTo(false)
        Truth.assertThat(training.indoor).isEqualTo(true)
        Truth.assertThat(training.weather).isEqualTo(EWeather.SUNNY)
        Truth.assertThat(training.windDirection).isEqualTo(0)
        Truth.assertThat(training.windSpeed).isEqualTo(3)
        Truth.assertThat(training.location).isEqualTo("Neufahrn bei Freising")
        val rounds = training.loadRounds()

        assertRound11(rounds)

        Truth.assertThat(rounds[1].id).isEqualTo(2L)
        Truth.assertThat(rounds[1].trainingId).isEqualTo(1L)
        Truth.assertThat(rounds[1].index).isEqualTo(1)
        Truth.assertThat(rounds[1].shotsPerEnd).isEqualTo(3)
        Truth.assertThat(rounds[1].maxEndCount).isEqualTo(null)
        Truth.assertThat(rounds[1].distance).isEqualTo(Dimension(20f, METER))
        Truth.assertThat(rounds[1].comment).isEqualTo("Kommentar")
        Truth.assertThat(rounds[1].target.id).isEqualTo(0)
        Truth.assertThat(rounds[1].target.scoringStyleIndex).isEqualTo(0)
        Truth.assertThat(rounds[1].target.diameter).isEqualTo(Dimension(60f, CENTIMETER))
        Truth.assertThat(rounds[1].loadEnds()).hasSize(2)
    }

    private fun assertRound11(rounds: List<Round>) {
        val round1 = rounds[0]
        Truth.assertThat(round1.id).isEqualTo(1L)
        Truth.assertThat(round1.trainingId).isEqualTo(1L)
        Truth.assertThat(round1.index).isEqualTo(0)
        Truth.assertThat(round1.shotsPerEnd).isEqualTo(4)
        Truth.assertThat(round1.maxEndCount).isEqualTo(null)
        Truth.assertThat(round1.distance).isEqualTo(Dimension(50f, METER))
        Truth.assertThat(round1.comment).isEqualTo("")
        Truth.assertThat(round1.target.id).isEqualTo(1)
        Truth.assertThat(round1.target.scoringStyleIndex).isEqualTo(2)
        Truth.assertThat(round1.target.diameter).isEqualTo(Dimension(40f, CENTIMETER))
        val ends = round1.loadEnds()
        Truth.assertThat(ends).hasSize(3)
        Truth.assertThat(ends[0].id).isEqualTo(1L)
        Truth.assertThat(ends[0].roundId).isEqualTo(1L)
        Truth.assertThat(ends[0].index).isEqualTo(0)
        Truth.assertThat(ends[0].loadImages()).isEmpty()
        Truth.assertThat(ends[0].exact).isEqualTo(true)
        Truth.assertThat(ends[2].exact).isEqualTo(false)
        val shots = ends[0].loadShots()
        Truth.assertThat(shots).hasSize(4)
        Truth.assertThat(shots[0].index).isEqualTo(0)
        Truth.assertThat(shots[0].x).isWithin(0f).of(-0.41206896f)
        Truth.assertThat(shots[0].y).isWithin(0f).of(0.03448276f)
        Truth.assertThat(shots[0].scoringRing).isEqualTo(3)
        Truth.assertThat(shots[0].arrowNumber).isEqualTo(null)
        Truth.assertThat(shots[1].index).isEqualTo(1)
        Truth.assertThat(shots[1].scoringRing).isEqualTo(2)
        Truth.assertThat(shots[2].index).isEqualTo(2)
        Truth.assertThat(shots[2].scoringRing).isEqualTo(1)
        Truth.assertThat(shots[3].index).isEqualTo(3)
        Truth.assertThat(shots[3].scoringRing).isEqualTo(0)
    }

    private fun assertTraining2(training: Training) {
        Truth.assertThat(training.id).isEqualTo(2)
        Truth.assertThat(training.title).isEqualTo("Training")
        Truth.assertThat(training.standardRoundId).isEqualTo(32L)
        Truth.assertThat(training.bowId).isEqualTo(1)
        Truth.assertThat(training.arrowId).isEqualTo(1)
        Truth.assertThat(training.arrowNumbering).isEqualTo(false)
        Truth.assertThat(training.indoor).isEqualTo(false)
        Truth.assertThat(training.weather).isEqualTo(EWeather.LIGHT_RAIN)
        Truth.assertThat(training.windDirection).isEqualTo(0)
        Truth.assertThat(training.windSpeed).isEqualTo(3)
        Truth.assertThat(training.location).isEqualTo("Neufahrn bei Freising")
        val rounds = training.loadRounds()
        Truth.assertThat(rounds[0].loadEnds()).hasSize(6)
        Truth.assertThat(rounds[1].loadEnds()).hasSize(6)
    }

    @Test
    @Throws(IOException::class)
    fun upgradeShouldKeepModel() {
        val upgradedSchema = extractSchema(upgradedDb!!)

        val newFile = File.createTempFile("newDb", ".db")
        val newDb = AndroidDatabase
                .from(SQLiteDatabase.openOrCreateDatabase(newFile, null))
        helper!!.onCreate(newDb)
        val newSchema = extractSchema(newDb)

        Truth.assertThat(upgradedSchema).isEqualTo(newSchema)
        Truth.assertThat(upgradedSchema.isNotEmpty())
    }

    private fun extractSchema(db: DatabaseWrapper): Set<String> {
        val c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)
        val schema = TreeSet<String>()
        c.moveToFirst()
        while (!c.isAfterLast) {
            val tableName = c.getString(0)

            val cu = db.rawQuery("PRAGMA table_info($tableName)", null)
            cu.moveToFirst()
            while (!cu.isAfterLast) {
                val columnName = cu.getString(1)
                val columnType = cu.getString(2)
                val columnNullable = cu.getString(3)
                val columnDefault = cu.getString(4)

                schema.add("TABLE " + tableName +
                        " COLUMN " + columnName + " " + columnType +
                        " NULLABLE=" + columnNullable +
                        " DEFAULT=" + columnDefault)
                cu.moveToNext()
            }
            cu.close()
            c.moveToNext()
        }
        c.close()
        return schema
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun prepare() {
            FlowManager.reset()
            FlowManager.destroy()
        }
    }
}
