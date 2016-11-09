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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.Target;

@Parcel
public class EndRenderer {
    public static final int NO_SELECTION = -1;
    public static final int MAX_CIRCLE_SIZE = 17;
    private static final int MIN_PADDING = 2;

    List<Shot> shotList;
    int pressed = NO_SELECTION;
    int selected = NO_SELECTION;
    Coordinate selectedPosition;
    int selectedRadius;

    // Animation
    Coordinate[] oldCoordinate;
    float currentAnimationProgress = -1;
    private transient Circle circle;
    private transient View parent;
    private transient RectF rect;
    private transient int radius;
    private transient Paint grayBackground;
    private transient float density;
    private transient int shotsPerRow;
    private transient float rowHeight;
    private transient float columnWidth;
    private transient ValueAnimator selectionAnimator;
    private transient int oldRadius;
    private transient int oldSelected;
    private transient int oldSelectedRadius;

    public void init(View parent, float density, Target target) {
        this.parent = parent;
        this.density = density;
        circle = new Circle(density, target);
        grayBackground = new Paint();
        grayBackground.setColor(0xFFDDDDDD);
        grayBackground.setAntiAlias(true);
    }

    private void setRect(RectF rect) {
        this.rect = rect;
        if (shotList == null) {
            return;
        }
        radius = MAX_CIRCLE_SIZE + MIN_PADDING;
        int neededRows;
        int maxRows;
        do {
            neededRows = (int) Math.ceil((radius * 2 * density * shotList.size()) / rect.width());
            maxRows = (int) Math.floor(rect.height() / (radius * 2 * density));
            radius--;
        } while (neededRows > maxRows);
        radius -= MIN_PADDING;
        int numRows = Math.max(neededRows, 1);
        shotsPerRow = (int) Math.ceil(shotList.size() / numRows);
        rowHeight = rect.height() / numRows;
        columnWidth = rect.width() / shotsPerRow;
    }

    public void setShots(List<Shot> shots) {
        boolean calcLayout = rect != null && shotList == null;
        shotList = new ArrayList<>(shots);
        oldCoordinate = new Coordinate[shotList.size()];
        Collections.sort(shotList);
        if (calcLayout) {
            setRect(rect);
        }
    }

    public void draw(Canvas canvas) {
        if (rect == null) {
            return;
        }

        // Draw all points of this end into the given rect
        for (int i = 0; i < shotList.size(); i++) {
            Shot shot = shotList.get(i);
            if (shot.zone == Shot.NOTHING_SELECTED) {
                break;
            }

            int radius = getRadius(shot);
            Coordinate coordinate = getAnimatedPosition(i, shot);
            if (radius > 0) {
                // Draw touch feedback if arrow is pressed
                if (pressed == shot.index) {
                    canvas.drawRect(coordinate.x - radius, coordinate.y - radius,
                            coordinate.x + radius, coordinate.y + radius, grayBackground);
                }

                // Draw circle
                circle.draw(canvas, coordinate.x, coordinate.y, shot.zone, radius,
                        !TextUtils.isEmpty(shot.comment) && i != selected, shot.index, shot.arrow);
            }
        }
    }

    @NonNull
    private Coordinate getPosition(int i, Shot shot) {
        if (selected == shot.index) {
            return new Coordinate(selectedPosition.x, selectedPosition.y);
        } else {
            Coordinate coordinate = new Coordinate();
            float column = i % shotsPerRow + 0.5f;
            coordinate.x = rect.left + column * columnWidth;
            float row = (float) (Math.ceil(i / shotsPerRow) + 0.5);
            coordinate.y = rect.top + row * rowHeight;
            return coordinate;
        }
    }

    private Coordinate getAnimatedPosition(int i, Shot shot) {
        Coordinate coordinate = getPosition(i, shot);
        if (currentAnimationProgress != -1 && oldCoordinate[shot.index] != null) {
            float oldX = oldCoordinate[shot.index].x;
            float oldY = oldCoordinate[shot.index].y;
            coordinate.x = oldX + (coordinate.x - oldX) * currentAnimationProgress;
            coordinate.y = oldY + (coordinate.y - oldY) * currentAnimationProgress;
        }
        return coordinate;
    }

    private int getRadius(Shot shot) {
        int rad = radius;
        int oRad = oldRadius;
        if (selected == shot.index) {
            rad = selectedRadius;
        } else if (oldSelected == shot.index) {
            oRad = oldSelectedRadius;
        }
        if (currentAnimationProgress != -1) {
            return (int) (oRad + (rad - oRad) * currentAnimationProgress);
        } else {
            return rad;
        }
    }

    public void animateToSelection(int selectedShot, Coordinate c, int radius) {
        saveCoordinates();
        setSelection(selectedShot, c, radius);
        Collections.sort(shotList);
        animate();
    }

    public void setSelection(int selectedShot, Coordinate c, int radius) {
        // Cancel eventually currently running animation
        cancel();

        selected = selectedShot;
        selectedPosition = c;
        selectedRadius = radius;
    }

    private void animate() {
        // Cancel eventually currently running animation
        cancel();

        selectionAnimator = ValueAnimator.ofFloat(0, 1);
        selectionAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        selectionAnimator.addUpdateListener(valueAnimator -> {
            currentAnimationProgress = (Float) valueAnimator.getAnimatedValue();
            parent.invalidate();
        });
        selectionAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                selectionAnimator = null;
                currentAnimationProgress = -1;
                parent.invalidate();
            }
        });
        selectionAnimator.setDuration(300);
        selectionAnimator.start();
    }

    public void animateToRect(RectF newRect) {
        if (rect == null || shotList == null || shotsPerRow == 0) {
            setRect(newRect);
            return;
        }
        saveCoordinates();
        setRect(newRect);
        animate();
    }

    public void cancel() {
        if (selectionAnimator != null) {
            selectionAnimator.cancel();
        }
    }

    private void saveCoordinates() {
        oldSelectedRadius = selectedRadius;
        oldRadius = radius;
        oldSelected = selected;
        for (int i = 0; i < shotList.size(); i++) {
            oldCoordinate[shotList.get(i).index] = getPosition(i, shotList.get(i));
        }
    }

    public int getPressedPosition(float x, float y) {
        if (rect.contains(x, y)) {
            int col = (int) Math.floor((x - rect.left) / columnWidth);
            int row = (int) Math.floor((y - rect.top) / rowHeight);
            final int arrow = row * shotsPerRow + col;
            if (arrow < shotList.size() && shotList.get(arrow).zone != Shot.NOTHING_SELECTED) {
                return shotList.get(arrow).index == selected ? -1 : shotList.get(arrow).index;
            }
        }
        return -1;
    }

    public int getPressed() {
        return pressed;
    }

    public void setPressed(int pressed) {
        this.pressed = pressed;
        parent.invalidate();
    }
}