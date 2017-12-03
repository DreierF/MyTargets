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

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.parceler.ParcelConstructor;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.models.db.EndImage;
import de.dreier.mytargets.shared.models.db.Image;

public class ImageList {
    List<String> images = new ArrayList<>();
    List<String> removed = new ArrayList<>();

    @ParcelConstructor
    public ImageList() {
    }

    public ImageList(List<? extends Image> images) {
        for (Image image : images) {
            this.images.add(image.getFileName());
        }
    }


    public int size() {
        return images.size();
    }

    public boolean isEmpty() {
        return images.isEmpty();
    }

    public Image get(int i) {
        return new EndImage(images.get(i));
    }

    public void remove(int i) {
        removed.add(images.remove(i));
    }

    public void addAll(List<String> images) {
        this.images.addAll(images);
    }

    public List<String> getRemovedImages() {
        return removed;
    }

    public List<EndImage> toEndImageList() {
        return Stream.of(images).map(EndImage::new).collect(Collectors.toList());
    }
}
