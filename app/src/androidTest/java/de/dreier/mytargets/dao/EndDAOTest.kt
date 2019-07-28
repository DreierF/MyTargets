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

package de.dreier.mytargets.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import de.dreier.mytargets.shared.models.augmented.AugmentedEnd
import de.dreier.mytargets.shared.models.db.End
import de.dreier.mytargets.shared.models.db.EndImage
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.Shot
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalTime

@RunWith(AndroidJUnit4::class)
open class EndDAOTest : DAOTestBase() {

    @Before
    fun initRound() {
        appDatabase.roundDAO().insertRound(Round(id = 1))
    }

    @Test
    fun saveCompleteEndSavesData() {
        val endToInsert = EndFactory.makeSimpleEnd(1)
        appDatabase.endDAO().insertCompleteEnd(endToInsert.end, endToInsert.images, endToInsert.shots)
        assertThat(endToInsert.id).isGreaterThan(0)

        val ends = appDatabase.endDAO().loadEnds(1)
        assertThat(ends.size).isEqualTo(1)
        assertThat(ends[0].id).isEqualTo(endToInsert.id)
        assertThat(ends[0].exact)
        assertThat(ends[0].saveTime).isEqualTo(LocalTime.of(12, 42))
        assertThat(ends[0].index).isEqualTo(0)

        val images = appDatabase.endDAO().loadEndImages(ends[0].id)
        assertThat(images).isEmpty()

        val shots = appDatabase.endDAO().loadShots(ends[0].id)
        assertThat(shots.size).isEqualTo(2)
        assertThat(shots[0].index).isEqualTo(0)
        assertThat(shots[1].index).isEqualTo(1)
        assertThat(shots[0].x).isEqualTo(0.5f)
        assertThat(shots[0].scoringRing).isEqualTo(4)
        assertThat(shots[1].y).isEqualTo(0.4f)
        assertThat(shots[1].scoringRing).isEqualTo(5)
    }

    @Test
    fun insertEndUpdatesIndicesWhenInsertedAtFront() {
        val firstEnd = EndFactory.makeSimpleEnd(1)
        appDatabase.endDAO().insertCompleteEnd(firstEnd.end, firstEnd.images, firstEnd.shots)

        val insertedEnd = EndFactory.makeSimpleEnd(1)
        appDatabase.endDAO().insertEnd(insertedEnd.end, insertedEnd.images, insertedEnd.shots)

        val ends = appDatabase.endDAO().loadEnds(1)
        assertThat(ends.size).isEqualTo(2)
        assertThat(ends[0].index).isEqualTo(0)
        assertThat(ends[1].index).isEqualTo(1)
    }

    @Test
    fun insertEndUpdatesIndicesWhenInsertedAtBack() {
        val firstEnd = EndFactory.makeSimpleEnd(1)
        appDatabase.endDAO().insertCompleteEnd(firstEnd.end, firstEnd.images, firstEnd.shots)

        val insertedEnd = EndFactory.makeSimpleEnd(1)
        insertedEnd.end.comment = "second"
        insertedEnd.end.index = 1
        appDatabase.endDAO().insertEnd(insertedEnd.end, insertedEnd.images, insertedEnd.shots)

        val ends = appDatabase.endDAO().loadEnds(1)
        assertThat(ends.size).isEqualTo(2)
        assertThat(ends[0].index).isEqualTo(0)
        assertThat(ends[1].index).isEqualTo(1)
        assertThat(ends[0].comment).isEqualTo("")
        assertThat(ends[1].comment).isEqualTo("second")
    }

    @Test
    fun deleteEndUpdatesIndices() {
        val firstEnd = EndFactory.makeSimpleEnd(1)
        appDatabase.endDAO().insertCompleteEnd(firstEnd.end, firstEnd.images, firstEnd.shots)

        val insertedEnd = EndFactory.makeSimpleEnd(1)
        insertedEnd.end.comment = "second"
        insertedEnd.end.index = 1
        appDatabase.endDAO().insertCompleteEnd(insertedEnd.end, insertedEnd.images, insertedEnd.shots)

        appDatabase.endDAO().deleteEnd(firstEnd.end)

        val ends = appDatabase.endDAO().loadEnds(1)
        assertThat(ends.size).isEqualTo(1)
        assertThat(ends[0].index).isEqualTo(0)
        assertThat(ends[0].comment).isEqualTo("second")
    }
}

class EndFactory {
    companion object {
        fun makeSimpleEnd(roundId: Long): AugmentedEnd {
            val end =
                End(roundId = roundId, exact = true, index = 0, saveTime = LocalTime.of(12, 42))
            val images = mutableListOf<EndImage>()
            val shots = mutableListOf<Shot>()
            shots.add(Shot(index = 0, x = 0.5f, y = 0.4f, scoringRing = 4))
            shots.add(Shot(index = 1, x = 0.5f, y = 0.4f, scoringRing = 5))
            return AugmentedEnd(end = end, images = images, shots = shots)
        }
    }
}
