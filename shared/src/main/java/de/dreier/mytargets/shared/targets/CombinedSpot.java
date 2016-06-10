/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.targets;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import java.util.List;

public class CombinedSpot extends Drawable {

    private final List<TargetDrawable> faces;
    private final Rect faceRect;

    public CombinedSpot(List<TargetDrawable> faces) {
        this.faces = faces;
        this.faceRect = new Rect();
    }

    @Override
    public void draw(Canvas canvas) {
        Rect rect = getBounds();
        int faceRadius = (int) Math.min(rect.width() * 1.2 / faces.size(), rect.width() / 2);
        int x = (rect.width() - faceRadius*2) / Math.max(faces.size() - 1, 1);
        for (int i = 0; i < faces.size(); i++) {
            faceRect.left = x * i;
            faceRect.top = x * i;
            faceRect.right = faceRect.left + faceRadius * 2;
            faceRect.bottom = faceRect.top + faceRadius * 2;
            faces.get(i).draw(canvas, faceRect);
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
