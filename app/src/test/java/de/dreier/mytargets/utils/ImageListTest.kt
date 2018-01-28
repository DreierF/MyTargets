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

package de.dreier.mytargets.utils

import de.dreier.mytargets.shared.models.db.EndImage
import org.junit.Assert.assertEquals
import org.junit.Test

class ImageListTest {
    @Test
    fun testEmptyList() {
        val list = ImageList()
        assertEquals(true, list.isEmpty)
        assertEquals(0, list.size())
        assertEquals(0, list.toEndImageList().size)
    }

    @Test
    fun testSingleImage() {
        val list = ImageList(listOf(EndImage(fileName = "someImage")))
        assertEquals(false, list.isEmpty)
        assertEquals(1, list.size())
        assertEquals(1, list.toEndImageList().size)
        assertEquals("someImage", list.toEndImageList()[0].fileName)
    }

    @Test
    fun testMultiImage() {
        val list = ImageList(listOf(EndImage(fileName = "someImage")))
        list.addAll(listOf("myImage", "oneMore"))
        list.remove(0)
        assertEquals(false, list.isEmpty)
        assertEquals(2, list.size())
        assertEquals(2, list.toEndImageList().size)
        assertEquals("myImage", list.toEndImageList()[0].fileName)
        assertEquals("oneMore", list.toEndImageList()[1].fileName)
        assertEquals("someImage", list.removedImages[0])
    }
}
