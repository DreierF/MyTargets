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

package de.dreier.mytargets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;

import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.utils.Circle;
import de.dreier.mytargets.shared.utils.EndRenderer;
import de.dreier.mytargets.shared.views.TargetViewBase;


public class TargetSelectView extends TargetViewBase {

    private int radius;
    private int chinHeight;
    private double circleRadius;
    private Circle circle;
    private float chinBound;

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
            circle.draw(canvas, coordinate.x, coordinate.y, selectableZones.get(i).index,
                    i == curZone ? 23 : 17, false, getCurrentShotIndex(), null);
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
        endRect.left = radius - 35 * density;
        endRect.right = radius + 35 * density;
        endRect.top = radius / 2;
        endRect.bottom = radius;
        return endRect;
    }

    @NonNull
    @Override
    protected Rect getSelectableZonePosition(int i) {
        PointF coordinate = getCircularCoordinates(i);
        final int rad = i == getCurrentlySelectedZone() ? 23 : 17;
        final Rect rect = new Rect();
        rect.left = (int) (coordinate.x - rad);
        rect.top = (int) (coordinate.y - rad);
        rect.right = (int) (coordinate.x + rad);
        rect.bottom = (int) (coordinate.y + rad);
        return rect;
    }

    @Override
    protected Shot getShotFromPos(float x, float y) {
        int zones = selectableZones.size();
        Shot s = new Shot(getCurrentShotIndex());

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
            s.scoringRing = selectableZones.get(index).index;
        }

        if (s.scoringRing == Shot.NOTHING_SELECTED) {
            // When nothing is selected do nothing
            return null;
        }
        return s;
    }

    @Override
    protected boolean selectPreviousShots(MotionEvent motionEvent, float x, float y) {
        return false;
    }
}
