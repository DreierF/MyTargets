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

package de.dreier.mytargets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;

import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.utils.Circle;
import de.dreier.mytargets.shared.utils.EndRenderer;
import de.dreier.mytargets.shared.views.TargetViewBase;


public class TargetSelectView extends TargetViewBase {

    public static final int RADIUS_SELECTED = 23;
    public static final int RADIUS_UNSELECTED = 17;
    private int radius;
    private int chinHeight;
    private double circleRadius;
    private Circle circle;
    private float chinBound;
    private boolean ambientMode = false;

    public TargetSelectView(Context context) {
        super(context);
    }

    public TargetSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TargetSelectView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    void setChinHeight(int chinHeight) {
        this.chinHeight = chinHeight;
    }

    @Override
    public void setTarget(Target target) {
        super.setTarget(target);
        circle = new Circle(density, target);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw all possible points in a circular
        int curZone = getCurrentlySelectedZone();
        for (int i = 0; i < selectableZones.size(); i++) {
            PointF coordinate = getCircularCoordinates(i);
            if (i != curZone) {
                circle.draw(canvas, coordinate.x, coordinate.y, selectableZones.get(i).index,
                        17, getCurrentShotIndex(), null, ambientMode);
            }
        }

        // Draw all points of this end in the center
        endRenderer.draw(canvas);
    }

    private int getCurrentlySelectedZone() {
        if (getCurrentShotIndex() != EndRenderer.NO_SELECTION) {
            return shots.get(getCurrentShotIndex()).scoringRing;
        } else {
            return Shot.NOTHING_SELECTED;
        }
    }

    private PointF getCircularCoordinates(int zone) {
        double degree = Math.toRadians(zone * 360.0 / (double) selectableZones.size());
        PointF coordinate = new PointF();
        coordinate.x = (float) (radius + (Math.cos(degree) * circleRadius));
        coordinate.y = (float) (radius + (Math.sin(degree) * circleRadius));
        if (coordinate.y > chinBound) {
            coordinate.y = chinBound;
        }
        return coordinate;
    }

    @Override
    protected PointF initAnimationPositions(int i) {
        return getCircularCoordinates(getSelectableZoneIndexFromShot(shots.get(i)));
    }

    @Override
    protected void updateLayoutBounds(int width, int height) {
        radius = (int) (width / 2.0);
        chinBound = height - (chinHeight + 15) * density;
        circleRadius = radius - 25 * density;
    }

    @Override
    protected RectF getEndRect() {
        RectF endRect = new RectF();
        endRect.left = radius - 45 * density;
        endRect.right = radius + 45 * density;
        endRect.top = radius / 2;
        endRect.bottom = radius;
        return endRect;
    }

    @NonNull
    @Override
    protected Rect getSelectableZonePosition(int i) {
        PointF coordinate = getCircularCoordinates(i);
        final int rad = i == getCurrentlySelectedZone() ? RADIUS_SELECTED : RADIUS_UNSELECTED;
        final Rect rect = new Rect();
        rect.left = (int) (coordinate.x - rad);
        rect.top = (int) (coordinate.y - rad);
        rect.right = (int) (coordinate.x + rad);
        rect.bottom = (int) (coordinate.y + rad);
        return rect;
    }

    @Override
    protected boolean updateShotToPosition(Shot shot, float x, float y) {
        int zones = selectableZones.size();

        double xDiff = x - radius;
        double yDiff = y - radius;

        float perception_rad = radius - 50 * density;
        // Select current arrow
        if (xDiff * xDiff + yDiff * yDiff > perception_rad * perception_rad) {
            double degree = Math.toDegrees(Math.atan2(-yDiff, xDiff)) - (180.0 / (double) zones);
            if (degree < 0) {
                degree += 360.0;
            }
            int index = (int) (zones * ((360.0 - degree) / 360.0));
            shot.scoringRing = selectableZones.get(index).index;
            return true;
        }

        return false;
    }

    @Override
    protected boolean selectPreviousShots(MotionEvent motionEvent, float x, float y) {
        return false;
    }

    @Override
    protected int getSelectedShotCircleRadius() {
        return RADIUS_SELECTED;
    }

    public void setAmbientMode(boolean ambientMode) {
        this.ambientMode = ambientMode;
        endRenderer.setAmbientMode(ambientMode);
        invalidate();
    }
}
