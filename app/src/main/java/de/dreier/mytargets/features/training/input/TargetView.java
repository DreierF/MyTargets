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

package de.dreier.mytargets.features.training.input;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.GridView;


import de.dreier.mytargets.shared.streamwrapper.Stream;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.shared.analysis.aggregation.EAggregationStrategy;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.SelectableZone;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.targets.drawable.TargetDrawable;
import de.dreier.mytargets.shared.utils.EndRenderer;
import de.dreier.mytargets.shared.utils.MatrixEvaluator;
import de.dreier.mytargets.shared.views.TargetViewBase;

import static de.dreier.mytargets.features.training.input.TargetView.EKeyboardType.LEFT;
import static de.dreier.mytargets.shared.views.TargetViewBase.EInputMethod.KEYBOARD;
import static de.dreier.mytargets.shared.views.TargetViewBase.EInputMethod.PLOTTING;

public class TargetView extends TargetViewBase {

    /**
     * This property is passed to ObjectAnimator when animating the spot matrix of TargetView
     */
    private static final Property<TargetView, Matrix> ANIMATED_SPOT_TRANSFORM_PROPERTY = new Property<TargetView, Matrix>(
            Matrix.class, "animatedSpotTransform") {

        @Override
        public void set(@NonNull TargetView targetView, Matrix matrix) {
            targetView.targetDrawable.setSpotMatrix(matrix);
            targetView.invalidate();
        }

        @Override
        public Matrix get(@NonNull TargetView targetView) {
            return targetView.targetDrawable.getSpotMatrix();
        }
    };

    /**
     * This property is passed to ObjectAnimator when animating the full matrix of TargetView
     */
    @Nullable
    private static final Property<TargetView, Matrix> ANIMATED_FULL_TRANSFORM_PROPERTY = new Property<TargetView, Matrix>(
            Matrix.class, "animatedFullTransform") {

        @Override
        public void set(@NonNull TargetView targetView, Matrix matrix) {
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
    private static final int KEYBOARD_TOTAL_WIDTH_DP =
            KEYBOARD_WIDTH_DP + KEYBOARD_OUTER_PADDING_DP;
    private static final int POINTER_OFFSET_Y_DP = -60;
    private static final int MIN_END_RECT_HEIGHT_DP = 80;
    private static final int KEYBOARD_INNER_PADDING_DP = 40;
    private Matrix[] spotMatrices;
    private boolean arrowNumbering;
    private Dimension arrowDiameter;
    private int maxArrowNumber;
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
    @NonNull
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
    private RectF keyboardRect;
    private RectF targetRect;

    @NonNull
    private FingerSlipDetector slipDetector = new FingerSlipDetector();

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
    public void setEnd(@NonNull End end) {
        EInputMethod inputMethod;
        if (!end.isEmpty()) {
            inputMethod = end.getExact() ? PLOTTING : KEYBOARD;
        } else {
            inputMethod = SettingsManager.INSTANCE.getInputMethod();
        }
        setInputMethod(inputMethod);
        super.setEnd(end);
    }

    public void setArrow(@NonNull Dimension diameter, boolean numbers, int maxArrowNumber) {
        this.arrowNumbering = numbers;
        this.arrowDiameter = diameter;
        this.maxArrowNumber = maxArrowNumber;
        targetDrawable.setArrowDiameter(diameter, SettingsManager.INSTANCE.getInputArrowDiameterScale());
    }

    public void setAggregationStrategy(EAggregationStrategy aggregationStrategy) {
        this.aggregationStrategy = aggregationStrategy;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        // Draw target
        if (inputMethod == PLOTTING && isCurrentlySelecting()) {
            drawZoomedInTarget(canvas);
        } else {
            drawTarget(canvas);
        }

        drawBackspaceButton(canvas);

        // Draw right indicator
        if (keyboardVisibility > 0) {
            drawKeyboard(canvas);
        }

        // Draw all points of this end at the top
        endRenderer.draw(canvas);
    }

    private void drawZoomedInTarget(@NonNull Canvas canvas) {
        Shot shot = shots.get(getCurrentShotIndex());

        targetDrawable.setMatrix(fullExtendedMatrix);
        targetDrawable.setSpotMatrix(
                spotMatrices[getCurrentShotIndex() % target.getModel().getFaceCount()]);
        targetDrawable.setZoom(targetZoomFactor);
        targetDrawable.setFocusedArrow(shot);
        targetDrawable.setOffset(0, POINTER_OFFSET_Y_DP * density);

        targetDrawable.draw(canvas);
    }

    // Draw actual target face
    private void drawTarget(@NonNull Canvas canvas) {
        targetDrawable.setOffset(0, 0);
        targetDrawable.setZoom(1);
        targetDrawable.setFocusedArrow(null);
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
            if (shot.getScoringRing() != Shot.Companion.getNOTHING_SELECTED() && shot.getIndex() != getCurrentShotIndex()) {
                displayedShots.add(shot);
            }
        }
        targetDrawable.setShots(displayedShots);
        super.notifyTargetShotsChanged();
        if (updateListener != null) {
            updateListener.onEndUpdated(shots);
        }
    }

    @NonNull
    @Override
    protected PointF getShotCoordinates(@NonNull Shot shot) {
        PointF coordinate = new PointF();
        if (inputMethod == KEYBOARD) {
            coordinate.x = keyboardRect.left;
            if (keyboardType == LEFT) {
                coordinate.x += (KEYBOARD_WIDTH_DP + KEYBOARD_INNER_PADDING_DP) * density;
            } else {
                coordinate.x -= KEYBOARD_INNER_PADDING_DP * density;
            }
            float indicatorHeight = keyboardRect.height() / selectableZones.size();
            int index = getSelectableZoneIndexFromShot(shot);
            coordinate.y = keyboardRect.top + indicatorHeight * index + indicatorHeight / 2.0f;
        } else {
            pt[0] = shot.getX();
            pt[1] = shot.getY();
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
        if (keyboardType == LEFT) {
            keyboardRect.left = KEYBOARD_OUTER_PADDING_DP * density;
        } else {
            keyboardRect.left = width - KEYBOARD_TOTAL_WIDTH_DP * density;
        }
        keyboardRect.right = keyboardRect.left + KEYBOARD_WIDTH_DP * density;
        keyboardRect.top = height / (selectableZones.size() + 1);
        keyboardRect.bottom = height;
    }

    @NonNull
    @Override
    protected Rect getBackspaceButtonBounds() {
        Rect backspaceButtonBounds = new Rect();
        backspaceButtonBounds.top = 0;
        backspaceButtonBounds.bottom = (int) (keyboardRect.top - density);
        backspaceButtonBounds.left = (int) keyboardRect.left;
        backspaceButtonBounds.right = (int) keyboardRect.right;
        return backspaceButtonBounds;
    }

    @NonNull
    @Override
    protected RectF getEndRect() {
        RectF endRect = new RectF(targetRect);
        endRect.top = 0;
        endRect.bottom = targetRect.top;
        if (keyboardType == LEFT) {
            endRect.left = keyboardRect.right;
        } else {
            endRect.right = keyboardRect.left;
        }
        endRect.inset(20 * density, TARGET_PADDING_DP * density);
        return endRect;
    }

    public void setInputMethod(EInputMethod mode) {
        if (mode != inputMethod) {
            inputMethod = mode;
            targetDrawable.drawArrowsEnabled(inputMethod == PLOTTING);
            targetDrawable.setAggregationStrategy(inputMethod == PLOTTING
                    ? aggregationStrategy : EAggregationStrategy.NONE);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean updateShotToPosition(@NonNull Shot s, float x, float y) {
        if (inputMethod == KEYBOARD) {
            if (keyboardRect.contains(x, y)) {
                int index = (int) ((y - keyboardRect.top) * selectableZones.size() /
                        keyboardRect.height());
                index = Math.min(Math.max(0, index), selectableZones.size() - 1);
                s.setScoringRing(selectableZones.get(index).index);
            } else {
                return false;
            }
        } else {
            pt[0] = x;
            pt[1] = y;
            fullExtendedMatrixInverse.mapPoints(pt);
            s.setX(pt[0]);
            s.setY(pt[1]);
            s.setScoringRing(targetDrawable.getZoneFromPoint(s.getX(), s.getY()));
            slipDetector.addShot(s.getX(), s.getY());
        }
        return true;
    }

    @Override
    protected boolean selectPreviousShots(@NonNull MotionEvent motionEvent, float x, float y) {
        // Handle selection of already saved shoots
        int shotIndex = endRenderer.getPressedPosition(x, y);
        endRenderer.setPressed(shotIndex);
        if (shotIndex != EndRenderer.NO_SELECTION) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                setCurrentShotIndex(shotIndex);
                animateToNewState();
            }
            return true;
        }
        return false;
    }

    @Override
    protected void collectAnimations(@NonNull List<Animator> animations) {
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
    protected void onShotSelectionFinished() {
        // Replace shot position with final position from slip detector
        PointF position = slipDetector.getFinalPosition();
        Shot shot = shots.get(getCurrentShotIndex());
        if (position != null) {
            shot.setX(position.x);
            shot.setY(position.y);
            shot.setScoringRing(targetDrawable.getZoneFromPoint(shot.getX(), shot.getY()));
            slipDetector.reset();
        }

        if (!arrowNumbering) {
            super.onShotSelectionFinished();
        } else {

            // Prepare grid view
            GridView gridView = new GridView(getContext());

            // Set grid view to alertDialog
            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setView(gridView)
                    .setCancelable(false)
                    .setTitle(R.string.arrow_numbers)
                    .create();

            List<String> numbers = Stream.rangeClosed(1, maxArrowNumber)
                    .map(String::valueOf)
                    .toList();
            gridView.setAdapter(
                    new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, numbers));
            gridView.setNumColumns(4);
            gridView.setOnItemClickListener((parent, view, pos, id) -> {
                if (getCurrentShotIndex() < shots.size()) {
                    shot.setArrowNumber(numbers.get(pos));
                }
                dialog.dismiss();
                setOnTouchListener(this);
                super.onShotSelectionFinished();
            });
            // Disable touch input while dialog is visible
            setOnTouchListener(null);
            dialog.show();
        }
    }

    /**
     * Draws a rect on the right that shows all possible points.
     *
     * @param canvas Canvas to draw on
     */
    private void drawKeyboard(@NonNull Canvas canvas) {
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
        rect.top = (int) (singleZoneHeight * i + density + keyboardRect.top);
        rect.bottom = (int) (singleZoneHeight * (i + 1) - density + keyboardRect.top);
        rect.left = (int) keyboardRect.left;
        rect.right = (int) keyboardRect.right;
        final int visibilityXOffset = (int) (KEYBOARD_TOTAL_WIDTH_DP * (1 - keyboardVisibility) *
                density);
        if (keyboardType == LEFT) {
            rect.offset(-visibilityXOffset, 0);
        } else {
            rect.offset(visibilityXOffset, 0);
        }
        return rect;
    }

    @Override
    public boolean onTouch(View view, @NonNull MotionEvent motionEvent) {
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
        this.targetZoomFactor = SettingsManager.INSTANCE.getInputTargetZoom();
        this.keyboardType = SettingsManager.INSTANCE.getInputKeyboardType();
        targetDrawable
                .setArrowDiameter(arrowDiameter, SettingsManager.INSTANCE.getInputArrowDiameterScale());
    }

    public void setTransparentShots(@NonNull Stream<Shot> shotStream) {
        targetDrawable.setTransparentShots(shotStream);
    }

    @Override
    protected int getSelectedShotCircleRadius() {
        return inputMethod == KEYBOARD ? EndRenderer.MAX_CIRCLE_SIZE : 0;
    }

    public enum EKeyboardType {
        LEFT, RIGHT
    }

    public interface OnEndUpdatedListener {
        void onEndUpdated(List<Shot> shots);
    }
}
