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

package de.dreier.mytargets.test.utils.rules

import android.content.Context
import android.support.test.InstrumentationRegistry
import de.dreier.mytargets.R
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.base.db.AppDatabase
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.EBowType
import de.dreier.mytargets.shared.models.EWeather
import de.dreier.mytargets.shared.models.Thumbnail
import de.dreier.mytargets.shared.models.augmented.AugmentedRound
import de.dreier.mytargets.shared.models.db.Arrow
import de.dreier.mytargets.shared.models.db.Bow
import de.dreier.mytargets.shared.models.db.Training
import de.dreier.mytargets.utils.addEnd
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import java.io.IOException
import java.util.*

abstract class DbTestRuleBase : TestRule {
    private val context: Context = InstrumentationRegistry.getTargetContext()

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                context.deleteDatabase(AppDatabase.DATABASE_FILE_NAME)
                ApplicationInstance.initRoomDb(context)
                addDatabaseContent()
                base.evaluate()
                try {
                    ApplicationInstance.db.close()
                } catch (e: IOException) {}
            }
        }
    }

    protected fun buildEnd(round: AugmentedRound, vararg shots: Int) {
        val end = round.addEnd()
        end.end.roundId = round.round.id
        end.end.saveTime = LocalTime.now()
        for (i in shots.indices) {
            end.shots[i].index = i
            end.shots[i].scoringRing = shots[i]
        }
        end.end.score = round.round.target.getReachedScore(end.shots)
        ApplicationInstance.db.endDAO().saveCompleteEnd(end.end, end.images, end.shots)
    }

    protected fun randomEnd(round: AugmentedRound, arrowsPerEnd: Int, gen: Random) {
        val end = round.addEnd()
        end.end.exact = true
        for (i in 0 until arrowsPerEnd) {
            end.shots[i].index = i
            end.shots[i].x = gaussianRand(gen)
            end.shots[i].y = gaussianRand(gen)
            end.shots[i].scoringRing = round.round.target.model
                .getZoneFromPoint(end.shots[i].x, end.shots[i].y, 0.05f)
        }
        end.end.score = round.round.target.getReachedScore(end.shots)
        end.end.saveTime = LocalTime.of(14, gen.nextInt(59), gen.nextInt(59), 0)
        ApplicationInstance.db.endDAO().saveCompleteEnd(end.end, end.images, end.shots)
    }

    private fun gaussianRand(gen: Random): Float {
        val rand1 = gen.nextFloat()
        val rand2 = gen.nextFloat()
        return (Math.sqrt(-2 * Math.log(rand1.toDouble()) / Math.log(Math.E)) * Math.cos(2.0 * Math.PI * rand2.toDouble())).toFloat() * 0.4f
    }

    protected abstract fun addDatabaseContent()

    protected fun addBow(name: String): Bow {
        val bow = Bow()
        bow.name = name
        bow.brand = "PSE"
        bow.size = "64\""
        bow.braceHeight = "6 3/8\""
        bow.type = EBowType.COMPOUND_BOW
        bow.thumbnail = Thumbnail.from(context, R.drawable.recurve_bow)
        ApplicationInstance.db.bowDAO().saveBow(bow, emptyList(), emptyList())
        return bow
    }

    protected fun addArrow(name: String): Arrow {
        val arrow = Arrow()
        arrow.name = name
        arrow.length = "30inch"
        arrow.comment = "some comment"
        arrow.diameter = Dimension(4f, Dimension.Unit.MILLIMETER)
        arrow.nock = "Awesome nock"
        arrow.thumbnail = Thumbnail.from(context, R.drawable.arrows)
        ApplicationInstance.db.arrowDAO().saveArrow(arrow, emptyList())
        return arrow
    }

    protected fun saveDefaultTraining(standardRoundId: Long?, generator: Random): Training {
        val training = Training()
        training.title = InstrumentationRegistry.getTargetContext().getString(R.string.training)
        training.date = LocalDate.of(2016, 4 + generator.nextInt(5), generator.nextInt(29))
        training.environment.location = ""
        training.environment.weather = EWeather.SUNNY
        training.environment.windSpeed = 1
        training.environment.windDirection = 0
        training.standardRoundId = standardRoundId
        training.bowId = null
        training.arrowId = null
        training.arrowNumbering = false
        ApplicationInstance.db.trainingDAO().saveTraining(training)
        return training
    }
}
