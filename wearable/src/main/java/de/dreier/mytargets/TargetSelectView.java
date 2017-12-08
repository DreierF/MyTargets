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
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
    @NonNull
    private Paint backspaceBackground = new Paint();

    public TargetSelectView(Context context) {
        super(context);
        init();
    }

    public TargetSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TargetSelectView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        backspaceSymbol.setTint(0xFFFFFFFF);
        backspaceBackground
                .setColor(ContextCompat
                        .getColor(getContext(), R.color.md_wear_green_active_ui_element));
        backspaceBackground.setAntiAlias(true);
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
    protected void onDraw(@NonNull Canvas canvas) {
        // Draw all possible points in a circular
        int curZone = getCurrentlySelectedZone();
        for (int i = 0; i < selectableZones.size(); i++) {
            PointF coordinate = getCircularCoordinates(i);
            if (i != curZone) {
                circle.draw(canvas, coordinate.x, coordinate.y, selectableZones.get(i).index,
                        17, getCurrentShotIndex(), null, ambientMode);
            }
        }

        if (!ambientMode) {
            canvas.drawCircle(radius, radius + 30 * density, 20 * density, backspaceBackground);
            drawBackspaceButton(canvas);
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

    @NonNull
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

    @NonNull
    @Override
    protected PointF getShotCoordinates(@NonNull Shot shot) {
        return getCircularCoordinates(getSelectableZoneIndexFromShot(shot));
    }

    @Override
    protected void updateLayoutBounds(int width, int height) {
        radius = (int) (width / 2.0);
        chinBound = height - (chinHeight + 15) * density;
        circleRadius = radius - 25 * density;
    }

    @NonNull
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
    protected Rect getBackspaceButtonBounds() {
        Rect backspaceBounds = new Rect();
        backspaceBounds.left = (int) (radius - 20 * density);
        backspaceBounds.right = (int) (radius + 20 * density);
        backspaceBounds.top = (int) (radius + 10 * density);
        backspaceBounds.bottom = (int) (radius + 50 * density);
        return backspaceBounds;
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
    protected boolean updateShotToPosition(@NonNull Shot shot, float x, float y) {
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
