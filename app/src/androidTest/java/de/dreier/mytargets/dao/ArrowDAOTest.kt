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

import android.support.test.runner.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import de.dreier.mytargets.shared.models.db.Arrow
import de.dreier.mytargets.shared.models.db.ArrowImage
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
open class ArrowDAOTest : DAOTestBase() {

    @Test
    fun saveArrowSavesData() {
        val arrow = Arrow(name = "arrow")
        val arrowImages = mutableListOf<ArrowImage>()
        arrowImages.add(ArrowImage(fileName = "image"))
        appDatabase.arrowDAO().saveArrow(arrow, arrowImages)
        assertThat(arrow.id).isGreaterThan(0)

        val loadedArrow = appDatabase.arrowDAO().loadArrow(arrow.id)
        assertThat(loadedArrow.name ).isEqualTo( "arrow")

        val images = appDatabase.arrowDAO().loadArrowImages(arrow.id)
        assertThat(images.size ).isEqualTo( 1)
        assertThat(images[0].fileName ).isEqualTo( "image")
    }

    @Test
    fun saveArrowReplacesImages() {
        val arrow = Arrow(name = "arrow")
        val arrowImages = mutableListOf<ArrowImage>()
        arrowImages.add(ArrowImage(fileName = "image1"))
        arrowImages.add(ArrowImage(fileName = "image2"))
        appDatabase.arrowDAO().saveArrow(arrow, arrowImages)

        val imagesBefore = appDatabase.arrowDAO().loadArrowImages(arrow.id)
        assertThat(imagesBefore.size ).isEqualTo( 2)

        arrowImages.removeAt(0)
        appDatabase.arrowDAO().saveArrow(arrow, arrowImages)

        val imagesAfter = appDatabase.arrowDAO().loadArrowImages(arrow.id)
        assertThat(imagesAfter.size).isEqualTo(1)
        assertThat(imagesAfter[0].fileName).isEqualTo("image2")
    }

}
