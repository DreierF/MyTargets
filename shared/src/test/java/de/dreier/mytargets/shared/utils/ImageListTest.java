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

package de.dreier.mytargets.shared.utils;

import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import de.dreier.mytargets.shared.models.db.EndImage;

import static junit.framework.Assert.assertEquals;

public class ImageListTest {
    @Test
    public void testEmptyList() {
        ImageList list = new ImageList();
        assertEquals(true, list.isEmpty());
        assertEquals(0, list.size());
        assertEquals(0, list.toEndImageList().size());
    }

    @Test
    public void testSingleImage() {
        ImageList list = new ImageList(Collections.singletonList(new EndImage("someImage")));
        assertEquals(false, list.isEmpty());
        assertEquals(1, list.size());
        assertEquals(1, list.toEndImageList().size());
        assertEquals("someImage", list.toEndImageList().get(0).getFileName());
    }

    @Test
    public void testMultiImage() {
        ImageList list = new ImageList(Collections.singletonList(new EndImage("someImage")));
        list.addAll(Arrays.asList(new File("myImage"), new File("oneMore")));
        list.remove(0);
        assertEquals(false, list.isEmpty());
        assertEquals(2, list.size());
        assertEquals(2, list.toEndImageList().size());
        assertEquals("myImage", list.toEndImageList().get(0).getFileName());
        assertEquals("oneMore", list.toEndImageList().get(1).getFileName());
        assertEquals("someImage", list.getRemovedImages().get(0).getPath());
    }
}
