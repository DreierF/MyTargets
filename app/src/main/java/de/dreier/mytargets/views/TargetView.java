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

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.shared.analysis.aggregation.EAggregationStrategy;
import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.SelectableZone;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.targets.drawable.TargetDrawable;
import de.dreier.mytargets.shared.utils.EndRenderer;
import de.dreier.mytargets.shared.utils.MatrixEvaluator;
import de.dreier.mytargets.shared.views.TargetViewBase;

import static de.dreier.mytargets.shared.views.TargetViewBase.EInputMethod.KEYBOARD;
import static de.dreier.mytargets.shared.views.TargetViewBase.EInputMethod.PLOTTING;
import static de.dreier.mytargets.views.TargetView.EKeyboardType.LEFT;

public class TargetView extends TargetViewBase {

    /**
     * This property is passed to ObjectAnimator when animating the spot matrix of TargetView
     */
    private static final Property<TargetView, Matrix> ANIMATED_SPOT_TRANSFORM_PROPERTY = new Property<TargetView, Matrix>(
            Matrix.class, "animatedSpotTransform") {

        @Override
        public void set(TargetView targetView, Matrix matrix) {
            targetView.targetDrawable.setSpotMatrix(matrix);
            targetView.invalidate();
        }

        @Override
        public Matrix get(TargetView targetView) {
            return targetView.targetDrawable.getSpotMatrix();
        }
    };

    /**
     * This property is passed to ObjectAnimator when animating the full matrix of TargetView
     */
    private static final Property<TargetView, Matrix> ANIMATED_FULL_TRANSFORM_PROPERTY = new Property<TargetView, Matrix>(
            Matrix.class, "animatedFullTransform") {

        @Override
        public void set(TargetView targetView, Matrix matrix) {
            targetView.targetDrawable.setMatrix(matrix);
            targetView.invalidate();
        }

        @Override
        public Matrix get(TargetView targetView) {
            return null;
        }
    };

    private static final int TARGET_PADDING_DP = 10;
    private static final int KEYBOARD_OUTER_PADDING_DP = 20;
    private static final int KEYBOARD_WIDTH_DP = 40;
    private static final int KEYBOARD_TOTAL_WIDTH_DP = KEYBOARD_WIDTH_DP + KEYBOARD_OUTER_PADDING_DP;
    private static final int POINTER_OFFSET_Y_DP = -60;
    private static final int MIN_END_RECT_HEIGHT_DP = 80;
    private static final int KEYBOARD_INNER_PADDING_DP = 40;

    private Matrix[] spotMatrices;
    private boolean arrowNumbering;
    private Dimension arrowDiameter;
    private float targetZoomFactor;
    private OnEndUpdatedListener updateListener;
    /**
     * Matrix to translate the target face with -1..1 coordinate system
     * to the correct area on the screen.
     */
    private Matrix fullMatrix;
    /**
     * Matrix to translate the target face with -1..1 coordinate system
     * to the correct area on the screen when selecting a shot position.
     * This area is inset by 30dp to allow easier placement outside of the target face.
     */
    private Matrix fullExtendedMatrix;
    /**
     * Inverse of {@link #fullExtendedMatrix}.
     * Allows to map screen coordinates to -1..1 coordinate system.
     */
    private Matrix fullExtendedMatrixInverse;
    /**
     * Temporary point vector used to translate between different coordinate systems.
     */
    private float[] pt = new float[2];
    private EAggregationStrategy aggregationStrategy = EAggregationStrategy.NONE;
    /**
     * Left-handed or right-handed mode.
     */
    private EKeyboardType keyboardType;
    /**
     * Used to draw the keyboard buttons.
     */
    private Paint fillPaint;
    /**
     * Used to draw the keyboard button borders.
     */
    private Paint borderPaint;
    /**
     * Used to draw the keyboard button texts.
     */
    private TextPaint textPaint;
    /**
     * Percentage of the keyboard that is currently supposed to be shown. (0..1).
     */
    private float keyboardVisibility = 0;
    private Timer longPressTimer;
    private RectF keyboardRect;
    private RectF targetRect;

    public TargetView(Context context) {
        super(context);
        init();
    }

    public TargetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TargetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // Set up a default TextPaint object
        textPaint = new TextPaint();
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(22 * density);
        textPaint.setTextAlign(Paint.Align.CENTER);

        fillPaint = new Paint();
        fillPaint.setAntiAlias(true);

        borderPaint = new Paint();
        borderPaint.setColor(0xFF1C1C1B);
        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void setEnd(End end) {
        shots = end.getShots();
        setCurrentShotIndex(getNextShotIndex(-1));
        endRenderer.setShots(shots);
        endRenderer.setSelection(getCurrentShotIndex(), null, EndRenderer.MAX_CIRCLE_SIZE);
        EInputMethod inputMethod;
        if (end.getId() != null) {
            inputMethod = end.exact ? PLOTTING : KEYBOARD;
        } else {
            inputMethod = SettingsManager.getInputMethod();
        }
        setInputMethod(inputMethod, false);
        animateToNewState();
        notifyTargetShotsChanged();
    }

    public void setArrow(Dimension diameter, boolean numbers) {
        this.arrowNumbering = numbers;
        this.arrowDiameter = diameter;
        targetDrawable.setArrowDiameter(diameter, SettingsManager.getInputArrowDiameterScale());
    }

    public void setAggregationStrategy(EAggregationStrategy aggregationStrategy) {
        SettingsManager.setAggregationStrategy(aggregationStrategy);
        this.aggregationStrategy = aggregationStrategy;
        if (inputMethod == KEYBOARD) {
            targetDrawable.setAggregationStrategy(EAggregationStrategy.NONE);
        } else {
            targetDrawable.setAggregationStrategy(aggregationStrategy);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw target
        if (inputMethod == PLOTTING && isCurrentlySelecting()) {
            drawZoomedInTarget(canvas);
        } else {
            drawTarget(canvas);
        }

        // Draw right indicator
        if (keyboardVisibility > 0) {
            drawKeyboard(canvas);
        }

        // Draw all points of this end at the top
        endRenderer.draw(canvas);
    }

    private void drawZoomedInTarget(Canvas canvas) {
        Shot shot = shots.get(getCurrentShotIndex());

        targetDrawable.setMatrix(fullExtendedMatrix);
        targetDrawable.setSpotMatrix(
                spotMatrices[getCurrentShotIndex() % target.getModel().getFaceCount()]);
        targetDrawable.setZoom(targetZoomFactor);
        targetDrawable.setMid(shot.x, shot.y);
        targetDrawable.setOffset(0, POINTER_OFFSET_Y_DP * density);

        targetDrawable.draw(canvas);

        // Draw exact arrow position
        targetDrawable.drawFocusedArrow(canvas, shot);
    }

    // Draw actual target face
    private void drawTarget(Canvas canvas) {
        targetDrawable.setOffset(0, 0);
        targetDrawable.setZoom(1);
        targetDrawable.setMid(0, 0);
        if (animator == null) {
            targetDrawable.setMatrix(fullMatrix);
            if (getCurrentShotIndex() == EndRenderer.NO_SELECTION || inputMethod == KEYBOARD) {
                targetDrawable.setSpotMatrix(new Matrix());
            } else {
                targetDrawable.setSpotMatrix(
                        spotMatrices[getCurrentShotIndex() % target.getModel().getFaceCount()]);
            }
        }
        targetDrawable.draw(canvas);
    }

    protected void notifyTargetShotsChanged() {
        List<Shot> displayedShots = new ArrayList<>();
        for (Shot shot : shots) {
            if (shot.zone != Shot.NOTHING_SELECTED && shot.index != getCurrentShotIndex()) {
                displayedShots.add(shot);
            }
        }
        targetDrawable.setShots(displayedShots);
        super.notifyTargetShotsChanged();
        if (updateListener != null) {
            updateListener.onEndUpdated(shots);
        }
    }

    @Override
    protected Coordinate initAnimationPositions(int i) {
        Coordinate coordinate = new Coordinate();
        if (inputMethod == KEYBOARD) {
            coordinate.x = keyboardRect.left;
            if (keyboardType == LEFT) {
                coordinate.x += (KEYBOARD_WIDTH_DP + KEYBOARD_INNER_PADDING_DP) * density;
            } else {
                coordinate.x -= KEYBOARD_INNER_PADDING_DP * density;
            }
            float indicatorHeight = keyboardRect.height() / selectableZones.size();
            int index = getSelectableZoneIndexFromShot(shots.get(i));
            coordinate.y = indicatorHeight * index + indicatorHeight / 2.0f;
        } else {
            pt[0] = shots.get(i).x;
            pt[1] = shots.get(i).y;
            fullMatrix.mapPoints(pt);
            coordinate.x = pt[0];
            coordinate.y = pt[1];
        }
        return coordinate;
    }

    @Override
    public void setTarget(Target t) {
        super.setTarget(t);
        spotMatrices = new Matrix[target.getModel().getFaceCount()];
        for (int i = 0; i < target.getModel().getFaceCount(); i++) {
            spotMatrices[i] = new Matrix();
            targetDrawable.getPreCalculatedFaceMatrix(i).invert(spotMatrices[i]);
        }
    }

    @Override
    protected void updateLayoutBounds(int width, int height) {
        targetRect = new RectF(0, MIN_END_RECT_HEIGHT_DP * density, width, height);
        targetRect.inset(TARGET_PADDING_DP * density, TARGET_PADDING_DP * density);
        if (inputMethod == KEYBOARD) {
            if (keyboardType == LEFT) {
                targetRect.left += KEYBOARD_TOTAL_WIDTH_DP * density;
            } else {
                targetRect.right -= KEYBOARD_TOTAL_WIDTH_DP * density;
            }
        }
        if (targetRect.height() > targetRect.width()) {
            targetRect.top = targetRect.bottom - targetRect.width();
        }

        fullMatrix = new Matrix();
        fullMatrix.setRectToRect(TargetDrawable.SRC_RECT, targetRect, Matrix.ScaleToFit.CENTER);

        RectF targetRectExt = new RectF(targetRect);
        targetRectExt.inset(30 * density, 30 * density);
        fullExtendedMatrix = new Matrix();
        fullExtendedMatrix
                .setRectToRect(TargetDrawable.SRC_RECT, targetRectExt, Matrix.ScaleToFit.CENTER);
        fullExtendedMatrixInverse = new Matrix();
        fullExtendedMatrix.invert(fullExtendedMatrixInverse);

        keyboardRect = new RectF();
        keyboardRect.top = 0;
        keyboardRect.bottom = height;
        if (keyboardType == LEFT) {
            keyboardRect.left = KEYBOARD_OUTER_PADDING_DP * density;
        } else {
            keyboardRect.left = width - KEYBOARD_TOTAL_WIDTH_DP * density;
        }
        keyboardRect.right = keyboardRect.left + KEYBOARD_WIDTH_DP * density;
    }

    @Override
    protected RectF getEndRect() {
        RectF endRect = new RectF(targetRect);
        endRect.top = 0;
        endRect.bottom = targetRect.top;
        endRect.inset(20 * density, TARGET_PADDING_DP * density);
        return endRect;
    }

    public void setInputMethod(EInputMethod mode, boolean animate) {
        if (mode != inputMethod) {
            inputMethod = mode;
            targetDrawable.drawArrowsEnabled(inputMethod == PLOTTING);
            targetDrawable.setAggregationStrategy(inputMethod == PLOTTING
                    ? aggregationStrategy : EAggregationStrategy.NONE);
            if (animate) {
                animateToNewState();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Shot getShotFromPos(float x, float y) {
        // Create Shot object
        Shot s = new Shot(getCurrentShotIndex());
        if (inputMethod == KEYBOARD) {
            if (keyboardRect.contains(x, y)) {
                int index = (int) (y * selectableZones.size() / keyboardRect.height());
                index = Math.min(Math.max(0, index), selectableZones.size() - 1);
                s.zone = selectableZones.get(index).index;
            } else {
                return null;
            }
        } else { // Handle via target
            pt[0] = x;
            pt[1] = y;
            fullExtendedMatrixInverse.mapPoints(pt);
            s.x = pt[0];
            s.y = pt[1];
            s.zone = targetDrawable.getZoneFromPoint(s.x, s.y);
        }
        return s;
    }

    @Override
    protected boolean selectPreviousShots(MotionEvent motionEvent, float x, float y) {
        // Handle selection of already saved shoots
        int shotIndex = endRenderer.getPressedPosition(x, y);
        if (shotIndex != EndRenderer.NO_SELECTION) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (longPressTimer != null) {
                    endRenderer.setPressed(EndRenderer.NO_SELECTION);
                    longPressTimer.cancel();
                    longPressTimer = null;
                    setCurrentShotIndex(shotIndex);
                    animateToNewState();
                }
            } else if (endRenderer.getPressed() != shotIndex) {
                // If new item gets selected cancel old timer and start new one
                endRenderer.setPressed(shotIndex);
                if (longPressTimer != null) {
                    longPressTimer.cancel();
                }
                longPressTimer = new Timer();
                final Handler h = new Handler();
                longPressTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        h.post(TargetView.this::onLongPressArrow);
                    }
                }, 1500);
            }
            invalidate();
            return true;
        } else {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (longPressTimer != null) {
                    longPressTimer.cancel();
                    longPressTimer = null;
                }
            }
            endRenderer.setPressed(EndRenderer.NO_SELECTION);
        }
        return false;
    }

    @Override
    protected void collectAnimations(List<Animator> animations) {
        Matrix initFullMatrix = new Matrix(fullMatrix);
        updateLayout();
        Matrix endMatrix = getSpotEndMatrix();

        float newVisibility = inputMethod == KEYBOARD ? 1 : 0;
        ValueAnimator inputAnimator = ValueAnimator.ofFloat(keyboardVisibility, newVisibility);
        inputAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        inputAnimator.addUpdateListener(valueAnimator -> {
            keyboardVisibility = (Float) valueAnimator.getAnimatedValue();
            invalidate();
        });
        animations.add(inputAnimator);

        animations.add(ObjectAnimator.ofObject(this, ANIMATED_FULL_TRANSFORM_PROPERTY,
                new MatrixEvaluator(), initFullMatrix, fullMatrix));
        animations.add(ObjectAnimator.ofObject(this, ANIMATED_SPOT_TRANSFORM_PROPERTY,
                new MatrixEvaluator(), endMatrix));
        super.collectAnimations(animations);
    }

    private Matrix getSpotEndMatrix() {
        Matrix endMatrix;
        if ((getCurrentShotIndex() == EndRenderer.NO_SELECTION || inputMethod == KEYBOARD)) {
            endMatrix = new Matrix();
        } else {
            endMatrix = spotMatrices[getCurrentShotIndex() % target.getModel().getFaceCount()];
        }
        return endMatrix;
    }

    @Override
    protected void onArrowChanged() {
        if (!arrowNumbering || getCurrentShotIndex() != EndRenderer.NO_SELECTION
                && shots.get(getCurrentShotIndex()).arrow != null) {
            super.onArrowChanged();
        } else {
            List<String> numbers = Stream.rangeClosed(1, 12)
                    .map(String::valueOf)
                    .collect(Collectors.toList());

            // Prepare grid view
            GridView gridView = new GridView(getContext());

            // Set grid view to alertDialog
            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setView(gridView)
                    .setCancelable(false)
                    .setTitle(R.string.arrow_numbers)
                    .create();
            gridView.setAdapter(
                    new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, numbers));
            gridView.setNumColumns(4);
            gridView.setOnItemClickListener((parent, view, position, id) ->
            {
                if (getCurrentShotIndex() < shots.size()) {
                    shots.get(getCurrentShotIndex()).arrow = numbers.get(position);
                }
                dialog.dismiss();
                super.onArrowChanged();
            });
            dialog.show();
        }
    }

    /**
     * Draws a rect on the right that shows all possible points.
     *
     * @param canvas Canvas to draw on
     */
    private void drawKeyboard(Canvas canvas) {
        for (int i = 0; i < selectableZones.size(); i++) {
            SelectableZone zone = selectableZones.get(i);

            Rect rect = getSelectableZonePosition(i);

            fillPaint.setColor(zone.zone.getFillColor());
            canvas.drawRect(rect, fillPaint);

            borderPaint.setColor(zone.zone.getStrokeColor());
            canvas.drawRect(rect, borderPaint);

            // For yellow and white background use black font color
            textPaint.setColor(zone.zone.getTextColor());
            canvas.drawText(zone.text, rect.centerX(), rect.centerY() + 10 * density,
                    textPaint);
        }
    }

    @Override
    @NonNull
    protected Rect getSelectableZonePosition(int i) {
        final Rect rect = new Rect();
        final float singleZoneHeight = keyboardRect.height() / selectableZones.size();
        rect.top = (int) (singleZoneHeight * i + density);
        rect.bottom = (int) (singleZoneHeight * (i + 1) - density);
        rect.left = (int) keyboardRect.left;
        rect.right = (int) keyboardRect.right;
        final int visibilityXOffset = (int) (KEYBOARD_TOTAL_WIDTH_DP * (1 - keyboardVisibility) * density);
        if (keyboardType == LEFT) {
            rect.offset(-visibilityXOffset, 0);
        } else {
            rect.offset(visibilityXOffset, 0);
        }
        return rect;
    }

    private void onLongPressArrow() {
        longPressTimer = null;
        final int pressed = endRenderer.getPressed();
        if (pressed == -1) {
            return;
        }
        longPressTimer = null;
        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
        onArrowChanged();

        new MaterialDialog.Builder(getContext())
                .title(R.string.comment)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("", shots.get(pressed).comment, (dialog, input) -> {
                    shots.get(pressed).comment = input.toString();
                    notifyTargetShotsChanged();
                })
                .negativeText(android.R.string.cancel)
                .dismissListener(dialog -> {
                    endRenderer.setPressed(-1);
                    invalidate();
                })
                .show();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        // Cancel animation
        if (animator != null) {
            cancelPendingAnimations();
            return true;
        }

        return super.onTouch(view, motionEvent);
    }

    public EInputMethod getInputMode() {
        return inputMethod;
    }

    public void setUpdateListener(OnEndUpdatedListener updateListener) {
        this.updateListener = updateListener;
    }

    public void reloadSettings() {
        this.targetZoomFactor = SettingsManager.getInputTargetZoom();
        this.keyboardType = SettingsManager.getInputKeyboardType();
        targetDrawable
                .setArrowDiameter(arrowDiameter, SettingsManager.getInputArrowDiameterScale());
    }

    public void setTransparentShots(Stream<Shot> shotStream) {
        targetDrawable.setTransparentShots(shotStream);
    }

    public enum EKeyboardType {
        LEFT, RIGHT
    }

    public interface OnEndUpdatedListener {
        void onEndUpdated(List<Shot> shots);
    }
}
