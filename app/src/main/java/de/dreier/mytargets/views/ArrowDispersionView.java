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

package de.dreier.mytargets.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.models.ArrowStatistic;
import de.dreier.mytargets.shared.targets.drawable.TargetImpactAggregationDrawable;

public class ArrowDispersionView extends View implements View.OnTouchListener {
    private static final float ZOOM_FACTOR = 3;
    private final float density;
    private int contentWidth;
    private int contentHeight;
    private float orgRadius, orgMidX, orgMidY;
    private TargetImpactAggregationDrawable target;
    private float zoomInX = -1, zoomInY = -1;

    public ArrowDispersionView(Context context) {
        this(context, null);
    }

    public ArrowDispersionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrowDispersionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnTouchListener(this);
        density = getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        contentWidth = getWidth();
        contentHeight = getHeight();
        calcSizes();
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();

        // Handle via target
        zoomInX = (x - orgMidX) / (orgRadius - 30 * density);
        zoomInY = (y - orgMidY) / (orgRadius - 30 * density);
        invalidate();

        // If finger is released go to next shoot
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            zoomInX = -1;
            zoomInY = -1;
            invalidate();
            return true;
        }
        return true;
    }

    public void setShots(ArrowStatistic statistic) {
        this.target = statistic.target.getImpactAggregationDrawable();
        // TODO merge all these setters into a single setArrowStatistic(stats)
        this.target.setAggregationStrategy(SettingsManager.getAggregationStrategy());
        this.target.setShots(statistic.shots);
        this.target.setArrowDiameter(statistic.arrowDiameter, SettingsManager.getInputArrowDiameterScale());
        this.target.setCallback(this);
        invalidate();
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        super.invalidateDrawable(drawable);
        post(this::invalidate);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (target == null) {
            return;
        }
        if (zoomInX != -1 || zoomInY != -1) {
            drawZoomedInTarget(canvas);
        } else {
            drawTarget(canvas, orgMidX, orgMidY, orgRadius);
        }
    }

    private void drawZoomedInTarget(Canvas canvas) {
        float px = zoomInX;
        float py = zoomInY;
        int radius2 = (int) (orgRadius * ZOOM_FACTOR);
        int x = (int) (orgMidX - px * (orgRadius + 30 * density));
        int y = (int) (orgMidY - py * (orgRadius + 30 * density) - 60 * density);
        drawTarget(canvas, x, y, radius2);
    }

    private void drawTarget(Canvas canvas, float x, float y, float radius) {
        // Draw actual target face
        target.setBounds((int) (x - radius), (int) (y - radius), (int) (x + radius),
                (int) (y + radius));
        target.draw(canvas);
    }

    private void calcSizes() {
        float radH = (contentHeight - 20 * density) * 0.5f;
        float radW = (contentWidth - 20 * density) * 0.5f;
        orgRadius = (int) (Math.min(radW, radH));
        orgMidX = contentWidth / 2;
        orgMidY = contentHeight / 2;
    }
}
