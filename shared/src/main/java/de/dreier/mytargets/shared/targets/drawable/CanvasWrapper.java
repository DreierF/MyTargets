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

package de.dreier.mytargets.shared.targets.drawable;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextPaint;

public class CanvasWrapper {

    private final Paint tempPen = new Paint();
    // two points 1 unit away from each other
    private final float[] PTS = new float[]{0, 0, 1, 0};
    private final float[] tmpPts = new float[4];
    private final float[] tmpPt = new float[2];
    private final Path tmpPath = new Path();
    private Canvas canvas;
    private Matrix matrix;
    private float scale;
    private TextPaint tempTextPaint = new TextPaint();

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
        canvas.save();
    }

    public void releaseCanvas() {
        canvas.restore();
        canvas = null;
    }

    public void setMatrix(Matrix matrix) {
        canvas.restore();
        canvas.save();
        this.matrix = matrix;

        // get the scale for transforming the Paint
        this.matrix.mapPoints(tmpPts, PTS);
        scale = (float) Math
                .sqrt(Math.pow(tmpPts[0] - tmpPts[2], 2) + Math.pow(tmpPts[1] - tmpPts[3], 2));
    }

    public void drawPath(Path path, final Paint pen) {
        // transform the path
        tmpPath.set(path);
        tmpPath.transform(matrix);

        // copy the existing Paint
        scalePaint(pen);

        // draw the path
        canvas.drawPath(tmpPath, tempPen);
    }

    public void drawCircle(float x, float y, float radius, Paint paintFill) {
        // copy the existing Paint
        scalePaint(paintFill);
        tmpPt[0] = x;
        tmpPt[1] = y;
        this.matrix.mapPoints(tmpPt);
        canvas.drawCircle(tmpPt[0], tmpPt[1], radius * scale, tempPen);
    }

    public void drawLine(float startX, float startY, float endX, float endY, Paint paintStroke) {
        // copy the existing Paint
        scalePaint(paintStroke);
        tmpPts[0] = startX;
        tmpPts[1] = startY;
        tmpPts[2] = endX;
        tmpPts[3] = endY;
        this.matrix.mapPoints(tmpPts);
        canvas.drawLine(tmpPts[0], tmpPts[1], tmpPts[2], tmpPts[3], tempPen);
    }

    public void drawRect(RectF rect, Paint paint) {
        scalePaint(paint);
        tmpPts[0] = rect.left;
        tmpPts[1] = rect.top;
        tmpPts[2] = rect.right;
        tmpPts[3] = rect.bottom;
        this.matrix.mapPoints(tmpPts);
        canvas.drawRect(tmpPts[0], tmpPts[1], tmpPts[2], tmpPts[3], tempPen);
    }

    private void scalePaint(Paint paint) {
        tempPen.set(paint);
        tempPen.setStrokeMiter(paint.getStrokeMiter() * scale);
        tempPen.setStrokeWidth(paint.getStrokeWidth() * scale);
    }

    public void drawText(String text, RectF rect, TextPaint paintText) {
        tmpPts[0] = rect.left;
        tmpPts[1] = rect.top;
        tmpPts[2] = rect.right;
        tmpPts[3] = rect.bottom;
        this.matrix.mapPoints(tmpPts);
        rect.left = tmpPts[0];
        rect.top = tmpPts[1];
        rect.right = tmpPts[2];
        rect.bottom = tmpPts[3];

        tempTextPaint.set(paintText);
        tempTextPaint.setTextAlign(Paint.Align.CENTER);
        tempTextPaint.setTextSize(rect.height() * 0.8f);
        canvas.drawText(text, rect.centerX(), rect.bottom - rect.height() * 0.2f, tempTextPaint);
    }
}
