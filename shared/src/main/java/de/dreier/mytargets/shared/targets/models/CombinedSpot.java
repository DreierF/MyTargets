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
package de.dreier.mytargets.shared.targets.models;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import java.util.List;

import de.dreier.mytargets.shared.targets.drawable.TargetDrawable;

public class CombinedSpot extends Drawable {

    private final List<TargetDrawable> faces;
    private final Rect faceRect;

    public CombinedSpot(List<TargetDrawable> faces) {
        this.faces = faces;
        this.faceRect = new Rect();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect rect = getBounds();
        int faceRadius = (int) Math.min(rect.width() * 1.2 / faces.size(), rect.width() / 2);
        int x = (rect.width() - faceRadius * 2) / Math.max(faces.size() - 1, 1);
        for (int i = 0; i < faces.size(); i++) {
            faceRect.left = x * i;
            faceRect.top = x * i;
            faceRect.right = faceRect.left + faceRadius * 2;
            faceRect.bottom = faceRect.top + faceRadius * 2;
            faces.get(i).setBounds(faceRect);
            faces.get(i).draw(canvas);
        }
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
