/*
 * Copyright (C) 2016 Florian Dreier
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

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

public class PathUtils {
    /**
     * Workaround for http://stackoverflow.com/questions/16090607/blurry-offset-paths-when-canvas-is-scaled-under-hardware-acceleration
     */
    public static void drawPath(Canvas canvas, Path path, final Paint pen) {
        canvas.save();

        // get the current matrix
        Matrix mat = canvas.getMatrix();

        // reverse the effects of the current matrix
        Matrix inv = new Matrix();
        mat.invert(inv);
        canvas.concat(inv);

        // transform the path
        path.transform(mat);

        // get the scale for transforming the Paint
        float[] pts = {0, 0, 1, 0}; // two points 1 unit away from each other
        mat.mapPoints(pts);
        float scale = (float) Math.sqrt(Math.pow(pts[0] - pts[2], 2) + Math.pow(pts[1] - pts[3], 2));

        // copy the existing Paint
        Paint pen2 = new Paint();
        pen2.set(pen);

        // scale the Paint
        pen2.setStrokeMiter(pen.getStrokeMiter() * scale);
        pen2.setStrokeWidth(pen.getStrokeWidth() * scale);

        // draw the path
        canvas.drawPath(path, pen2);
        path.transform(inv);

        canvas.restore();
    }

    public static Region getScaledRegion(Path path, float scale) {
        // Scale the path
        Path scaledPath = new Path(path);
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        scaledPath.transform(matrix);

        // Get path bounds
        RectF bounds = new RectF();
        scaledPath.computeBounds(bounds, true);
        Rect rectBounds = new Rect();
        bounds.roundOut(rectBounds);

        // Create region from path
        Region region = new Region();
        region.setPath(scaledPath, new Region(rectBounds));
        return region;
    }
}
