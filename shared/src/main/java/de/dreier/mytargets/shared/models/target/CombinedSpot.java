/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models.target;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.List;

import de.dreier.mytargets.shared.R;

public class CombinedSpot extends SpotBase {

    private final Target[] faces;

    public CombinedSpot(Context context, List<Target> faces) {
        super(context, -1, R.string.small);
        this.faces = faces.toArray(new Target[faces.size()]);
        facePositions = new int[faces.size()][];
        faceRadius = Math.max(500 - 62 * faces.size(), 250);
        int x = (500 - faceRadius) / Math.max(faces.size() - 1, 1);
        for (int i = 0; i < faces.size(); i++) {
            facePositions[i] = new int[2];
            facePositions[i][0] = faceRadius + x * i;
            facePositions[i][1] = facePositions[i][0];
        }
    }

    @Override
    protected void draw(Canvas canvas, Rect rect) {
        for (int i = 0; i < facePositions.length; i++) {
            faces[i].draw(canvas, getTargetBounds(rect, i));
        }
        onPostDraw(canvas, rect);
    }
}
