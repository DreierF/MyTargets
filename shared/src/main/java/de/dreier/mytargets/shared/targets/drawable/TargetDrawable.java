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
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.models.TargetModelBase;
import de.dreier.mytargets.shared.targets.zone.ZoneBase;

public class TargetDrawable extends Drawable {

    public static final RectF SRC_RECT = new RectF(-1, -1, 1, 1);
    protected final Target target;
    final TargetModelBase model;
    private final List<ZoneBase> zonesToDraw;
    private final ArrayList<Matrix> targetFaceMatrices;
    private final ArrayList<Matrix> drawMatrices;
    private Matrix matrix = new Matrix();
    private float zoom = 1;
    private float px;
    private float py;
    private Matrix spotMatrix = new Matrix();
    private float xOffset;
    private float yOffset;

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
        setBoundsRespectingStroke(new RectF(left, top, right, bottom));
    }

    @Override
    public void setBounds(@NonNull Rect bounds) {
        super.setBounds(bounds);
        setBoundsRespectingStroke(new RectF(bounds));
    }

    private void setBoundsRespectingStroke(RectF bounds) {
        RectF srcRectWithStroke = new RectF(SRC_RECT);
        final ZoneBase outerZone = model.getZone(model.getZoneCount() - 1);
        final float inset = -outerZone.getStrokeWidth() * 0.5f;
        srcRectWithStroke.inset(inset, inset);
        matrix.setRectToRect(srcRectWithStroke,
                bounds,
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
        m.set(getPreCalculatedFaceMatrix(faceIndex));
        m.postConcat(spotMatrix);
        m.postTranslate(-px - 1, -py - 1);
        m.postScale(zoom, zoom);
        m.postTranslate(zoom + px, zoom + py);
        m.postConcat(matrix);
        m.postTranslate(xOffset, yOffset);
        return m;
    }

    public Matrix getPreCalculatedFaceMatrix(int faceIndex) {
        return targetFaceMatrices.get(faceIndex);
    }

    @NonNull
    private Matrix calculateTargetFaceMatrix(int index) {
        PointF pos = model.facePositions[index % model.facePositions.length];
        Matrix matrix = new Matrix();
        matrix.setRectToRect(new RectF(-1f, -1f, 1f, 1f),
                new RectF(pos.x - model.faceRadius, pos.y - model.faceRadius,
                        pos.x + model.faceRadius, pos.y + model.faceRadius),
                Matrix.ScaleToFit.FILL);
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

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public void setMid(float px, float py) {
        this.px = px;
        this.py = py;
    }

    public void setOffset(float x, float y) {
        xOffset = x;
        yOffset = y;
    }

    public Matrix getSpotMatrix() {
        return spotMatrix;
    }

    public void setSpotMatrix(Matrix spotMatrix) {
        this.spotMatrix = spotMatrix;
    }
}
