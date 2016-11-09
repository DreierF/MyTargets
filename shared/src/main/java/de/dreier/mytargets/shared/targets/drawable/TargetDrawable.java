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

package de.dreier.mytargets.shared.targets.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.models.TargetModelBase;
import de.dreier.mytargets.shared.targets.zone.ZoneBase;

public class TargetDrawable extends Drawable {

    protected final Target target;
    final TargetModelBase model;
    private Matrix matrix = new Matrix();
    private final List<ZoneBase> zonesToDraw;
    private final ArrayList<Matrix> targetFaceMatrices;
    private final ArrayList<Matrix> drawMatrices;

    public TargetDrawable(Target target) {
        this.model = target.getModel();
        this.target = target;
        this.zonesToDraw = new ArrayList<>();
        for (int i = getZones() - 1; i >= 0; i--) {
            if (!model.shouldDrawZone(i, target.scoringStyle)) {
                continue;
            }
            zonesToDraw.add(model.getZone(i));
        }
        targetFaceMatrices = new ArrayList<>();
        drawMatrices = new ArrayList<>();
        for (int faceIndex = 0; faceIndex < model.getFaceCount(); faceIndex++) {
            targetFaceMatrices.add(calculateTargetFaceMatrix(faceIndex));
            drawMatrices.add(new Matrix());
        }
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        matrix.setRectToRect(new RectF(-1, -1, 1, 1),
                new RectF(left, top, right, bottom),
                Matrix.ScaleToFit.CENTER);
    }

    @Override
    public void setBounds(@NonNull Rect bounds) {
        super.setBounds(bounds);
        matrix.setRectToRect(new RectF(-1, -1, 1, 1),
                new RectF(bounds),
                Matrix.ScaleToFit.CENTER);
    }

    public Target getTarget() {
        return target;
    }

    private int getZones() {
        return model.getZoneCount();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.save();
        for (int faceIndex = 0; faceIndex < model.facePositions.length; faceIndex++) {
            setMatrixForTargetFace(canvas, faceIndex);
            for (ZoneBase zone : zonesToDraw) {
                zone.drawFill(canvas);
            }
        }
        for (int faceIndex = 0; faceIndex < model.facePositions.length; faceIndex++) {
            setMatrixForTargetFace(canvas, faceIndex);
            for (ZoneBase zone : zonesToDraw) {
                zone.drawStroke(canvas);
            }
            onPostDraw(canvas, faceIndex);
        }
        canvas.restore();
    }

    private void setMatrixForTargetFace(@NonNull Canvas canvas, int faceIndex) {
        canvas.setMatrix(getTargetFaceMatrix(faceIndex));
    }

    protected Matrix getTargetFaceMatrix(int faceIndex) {
        Matrix m = drawMatrices.get(faceIndex);
        m.set(matrix);
        m.preConcat(targetFaceMatrices.get(faceIndex));
        return m;
    }

    @NonNull
    private Matrix calculateTargetFaceMatrix(int index) {
        Coordinate pos = model.facePositions[index % model.facePositions.length];
        RectF fullRect = new RectF(-1f, -1f, 1f, 1f);
        final RectF spotRectIn11 = new RectF(pos.x - model.faceRadius, pos.y - model.faceRadius,
                pos.x + model.faceRadius, pos.y + model.faceRadius);
        Matrix matrix = new Matrix();
        matrix.setRectToRect(fullRect, new RectF(spotRectIn11), Matrix.ScaleToFit.CENTER);
        return matrix;
    }

    protected void onPostDraw(Canvas canvas, int faceIndex) {
        if (model.getDecorator() != null) {
            model.getDecorator().drawDecoration(canvas);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TargetDrawable) {
            TargetDrawable t = (TargetDrawable) o;
            return t.target.id == target.id;
        }
        return false;
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    public void setAlpha(int arg0) {
    }

    @Override
    public void setColorFilter(ColorFilter arg0) {
    }

    public RectF getBoundsF(int index, Rect rect) {
        Coordinate pos = model.facePositions[index];
        RectF bounds = new RectF();
        bounds.left = rect.left + (500f + pos.x * 500f - model.faceRadius * 500f) * 0.5f;
        bounds.top = rect.top + (500f + pos.y * 500f - model.faceRadius * 500f) * 0.5f;
        bounds.right = rect.left + (500f + pos.x * 500f + model.faceRadius * 500f) * 0.5f;
        bounds.bottom = rect.top + (500f + pos.y * 500f + model.faceRadius * 500f) * 0.5f;
        return bounds;
    }
}
