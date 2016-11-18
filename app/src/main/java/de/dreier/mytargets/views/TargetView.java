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
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
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
import de.dreier.mytargets.shared.models.ArrowNumber;
import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.SelectableZone;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.utils.EndRenderer;
import de.dreier.mytargets.shared.views.TargetViewBase;

import static de.dreier.mytargets.views.TargetView.EKeyboardType.LEFT;
import static de.dreier.mytargets.views.TargetView.EKeyboardType.RIGHT;

//TODO add ui tests for TargetView
public class TargetView extends TargetViewBase {

    private static final int TARGET_PADDING_DP = 10;
    private static final int KEYBOARD_OUTER_PADDING_DP = 20;
    private static final int KEYBOARD_WIDTH_DP = 40;
    private static final int KEYBOARD_TOTAL_WIDTH_DP = KEYBOARD_WIDTH_DP + KEYBOARD_OUTER_PADDING_DP;
    private static final int POINTER_OFFSET_Y_DP = -60;
    private static final int MIN_END_RECT_HEIGHT_DP = 80;
    private static final int KEYBOARD_INNER_PADDING_DP = 40;
    private final Handler h = new Handler();
    private Paint fillPaint;
    private Timer longPressTimer;
    private Matrix[] spotMatrices;
    private List<ArrowNumber> arrowNumbers = new ArrayList<>();
    private TextPaint textPaint;
    private Paint borderPaint;
    private AnimatorSet animator;
    private Dimension arrowDiameter;
    private float targetZoomFactor;
    private OnEndUpdatedListener updateListener;
    private Matrix fullMatrix;
    private float[] pt = new float[2];
    private Matrix fullExtendedMatrix;
    private Matrix fullExtendedMatrixInverse;
    private EAggregationStrategy aggregationStrategy = EAggregationStrategy.NONE;

    /**
     * Percentage of the keyboard that is currently supposed to be shown. (0..1)
     */
    private float keyboardVisibility = 0;

    /**
     * Left-handed or Right-handed mode
     */
    private EKeyboardType keyboardType;

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
    public void setEnd(Passe end) {
        shots = end.shots;
        setCurrentShotIndex(getNextShotIndex(-1));
        endRenderer.setShots(shots);
        endRenderer.setSelection(getCurrentShotIndex(), null, EndRenderer.MAX_CIRCLE_SIZE);
        EInputMethod inputMethod;
        if (end.getId() != 0) {
            inputMethod = end.exact ? EInputMethod.PLOTTING : EInputMethod.KEYBOARD;
        } else {
            inputMethod = SettingsManager.getInputMethod();
        }
        setInputMethod(inputMethod, false);
        animateToNewState();
        notifyTargetShotsChanged();
    }

    public void setArrow(Dimension diameter, List<ArrowNumber> numbers) {
        this.arrowNumbers = numbers;
        this.arrowDiameter = diameter;
        targetDrawable.setArrowDiameter(diameter, SettingsManager.getInputArrowDiameterScale());
    }

    public void setAggregationStrategy(EAggregationStrategy aggregationStrategy) {
        SettingsManager.setAggregationStrategy(aggregationStrategy);
        this.aggregationStrategy = aggregationStrategy;
        if (inputMethod == EInputMethod.KEYBOARD) {
            targetDrawable.setAggregationStrategy(EAggregationStrategy.NONE);
        } else {
            targetDrawable.setAggregationStrategy(aggregationStrategy);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw target
        if (inputMethod == EInputMethod.PLOTTING && isCurrentlySelecting()) {
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
        targetDrawable.setZoom(targetZoomFactor); //TODO zoom only if shot is set
        targetDrawable.setMid(shot.x, shot.y); // TODO set shot instead
        targetDrawable.setOffset(0, POINTER_OFFSET_Y_DP * density);

        targetDrawable.draw(canvas);

        // Draw exact arrow position
        targetDrawable.drawFocusedArrow(canvas, shot);
    }

    // Draw actual target face
    private void drawTarget(Canvas canvas) {
        targetDrawable.setMatrix(fullMatrix);
        targetDrawable.setOffset(0, 0);
        targetDrawable.setZoom(1);
        targetDrawable.setMid(0, 0);
        if (animator == null) {
            if (getCurrentShotIndex() == EndRenderer.NO_SELECTION || inputMethod == EInputMethod.KEYBOARD) {
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
        if (inputMethod == EInputMethod.KEYBOARD) {
            if (keyboardType == LEFT) {
                coordinate.x = (KEYBOARD_TOTAL_WIDTH_DP + KEYBOARD_INNER_PADDING_DP) * density;
            } else {
                coordinate.x = contentWidth - (KEYBOARD_TOTAL_WIDTH_DP + KEYBOARD_INNER_PADDING_DP) * density;
            }
            int indicatorHeight = contentHeight / selectableZones.size();
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
    protected void calcSizes() {
        RectF targetRect = new RectF(0, MIN_END_RECT_HEIGHT_DP * density,
                contentWidth, contentHeight);
        targetRect.inset(TARGET_PADDING_DP * density, TARGET_PADDING_DP * density);
        if (inputMethod == EInputMethod.KEYBOARD) {
            if (keyboardType == LEFT) {
                targetRect.left += KEYBOARD_TOTAL_WIDTH_DP * density;
            } else {
                targetRect.right -= KEYBOARD_TOTAL_WIDTH_DP * density;
            }
        }
        if (targetRect.height() > targetRect.width()) {
            targetRect.top = targetRect.bottom - targetRect.width();
        }
        final RectF srcRect = new RectF(-1f, -1f, 1f, 1f);

        fullMatrix = new Matrix();
        fullMatrix.setRectToRect(srcRect,
                targetRect, Matrix.ScaleToFit.CENTER);

        RectF targetRectExt = new RectF(targetRect);
        targetRectExt.inset(30 * density, 30 * density);
        fullExtendedMatrix = new Matrix();
        fullExtendedMatrix.setRectToRect(srcRect,
                targetRectExt, Matrix.ScaleToFit.CENTER);
        fullExtendedMatrixInverse = new Matrix();
        fullExtendedMatrix.invert(fullExtendedMatrixInverse);

        RectF endRect = new RectF(targetRect);
        endRect.top = 0;
        endRect.bottom = targetRect.top;
        endRect.inset(20 * density, TARGET_PADDING_DP * density);
        endRenderer.animateToRect(endRect);
    }

    public void setInputMethod(EInputMethod mode, boolean animate) {
        if (mode != inputMethod) {
            // TODO make sure selected arrow indicator transforms as well
            inputMethod = mode;
            targetDrawable.drawArrowsEnabled(inputMethod == EInputMethod.PLOTTING);
            targetDrawable.setAggregationStrategy(inputMethod == EInputMethod.PLOTTING
                    ? aggregationStrategy
                    : EAggregationStrategy.NONE);
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
        if (inputMethod == EInputMethod.KEYBOARD) {
            if ((keyboardType == LEFT && x < KEYBOARD_TOTAL_WIDTH_DP * density) ||
                    (keyboardType == RIGHT && x > contentWidth - KEYBOARD_TOTAL_WIDTH_DP * density)) {
                int i = (int) (y * selectableZones.size() / (float) contentHeight);
                i = Math.min(Math.max(0, i), selectableZones.size() - 1);
                s.zone = selectableZones.get(i).index;
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
        int arrow = endRenderer.getPressedPosition(x, y);
        if (arrow != -1) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (longPressTimer != null) {
                    endRenderer.setPressed(-1);
                    longPressTimer.cancel();
                    longPressTimer = null;
                    super.onArrowChanged();
                }
            } else if (endRenderer.getPressed() != arrow) {
                // If new item gets selected cancel old timer and start new one
                endRenderer.setPressed(arrow);
                if (longPressTimer != null) {
                    longPressTimer.cancel();
                }
                longPressTimer = new Timer();
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
            endRenderer.setPressed(-1);
        }
        return false;
    }

    @Override
    protected void animateToNewState() {
        if (spotMatrices == null) {
            return;
        }
        // Get current matrix
        Matrix oldMatrix = targetDrawable.getSpotMatrix();
        float[] oldValues = new float[9];
        oldMatrix.getValues(oldValues);

        // Intermediate matrix
        Matrix midMatrix = new Matrix();
        float[] midValues = new float[9];
        midMatrix.getValues(midValues);

        // Get new matrix
        Matrix newMatrix;
        if (inputMethod == EInputMethod.PLOTTING && getCurrentShotIndex() != EndRenderer.NO_SELECTION) {
            newMatrix = spotMatrices[getCurrentShotIndex() % target.getModel().getFaceCount()];
        } else {
            newMatrix = new Matrix();
        }
        float[] newValues = new float[9];
        newMatrix.getValues(newValues);

        cancelPendingAnimations();
        calcSizes();

        List<ValueAnimator> valueAnimators = new ArrayList<>(9);
        List<Animator> setList = new ArrayList<>(10);
        float newVisibility = inputMethod == EInputMethod.KEYBOARD ? 1 : 0;
        ValueAnimator inputAnimator = ValueAnimator.ofFloat(keyboardVisibility, newVisibility);
        inputAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        inputAnimator.addUpdateListener(valueAnimator -> {
            keyboardVisibility = (Float) valueAnimator.getAnimatedValue();
            for (int i = 0; i < 9; i++) {
                midValues[i] = (float) valueAnimators.get(i).getAnimatedValue();
            }
            midMatrix.setValues(midValues);
            targetDrawable.setSpotMatrix(midMatrix);
            invalidate();
        });
        setList.add(inputAnimator);

        for (int i = 0; i < 9; i++) {
            final ValueAnimator animator = ValueAnimator.ofFloat(oldValues[i], newValues[i]);
            valueAnimators.add(animator);
            setList.add(animator);
        }
        animator = new AnimatorSet();
        animator.playTogether(setList);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                onAnimationEnd(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animator = null;
                invalidate();
            }
        });
        animator.setDuration(300);
        animator.start();
    }

    @Override
    protected void onArrowChanged() {
        if (arrowNumbers.isEmpty() || getCurrentShotIndex() != EndRenderer.NO_SELECTION
                && shots.get(getCurrentShotIndex()).arrow != null) {
            super.onArrowChanged();
        } else {
            List<String> numbersLeft = Stream.of(arrowNumbers).map(an -> an.number)
                    .collect(Collectors.toList());
            for (Shot s : shots) {
                numbersLeft.remove(s.arrow);
            }
            if (numbersLeft.size() == 0 || getCurrentShotIndex() == EndRenderer.NO_SELECTION) {
                super.onArrowChanged();
                return;
            } else if (numbersLeft.size() == 1) {
                shots.get(getCurrentShotIndex()).arrow = numbersLeft.get(0);
                super.onArrowChanged();
                return;
            }

            // Prepare grid view
            GridView gridView = new GridView(getContext());

            // Set grid view to alertDialog
            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setView(gridView)
                    .setCancelable(false)
                    .setTitle(R.string.arrow_numbers).create();
            gridView.setAdapter(
                    new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,
                            numbersLeft));
            int cols = Math.min(5, numbersLeft.size());
            gridView.setNumColumns(cols);
            gridView.setOnItemClickListener((parent, view, position, id) ->
            {
                if (getCurrentShotIndex() < shots.size()) {
                    shots.get(getCurrentShotIndex()).arrow = numbersLeft.get(position);
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
        rect.top = (int) (contentHeight * i / selectableZones.size() + density);
        rect.bottom = (int) (contentHeight * (i + 1) / selectableZones.size() - density);
        rect.left = 0;
        rect.right = (int) (KEYBOARD_WIDTH_DP * density);
        int left;
        if (keyboardType == EKeyboardType.RIGHT) {
            left = (int) (contentWidth - KEYBOARD_TOTAL_WIDTH_DP * density);
            left += KEYBOARD_TOTAL_WIDTH_DP * (1 - keyboardVisibility) * density;
        } else {
            left = (int) (KEYBOARD_OUTER_PADDING_DP * density);
            left -= KEYBOARD_TOTAL_WIDTH_DP * (1 - keyboardVisibility) * density;
        }
        rect.offsetTo(left, rect.top);
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
            endRenderer.cancel();
        }

        return super.onTouch(view, motionEvent);
    }

    public EInputMethod getInputMode() {
        return inputMethod;
    }

    private void cancelPendingAnimations() {
        if (animator != null) {
            AnimatorSet tmp = animator;
            animator = null;
            tmp.cancel();
        }
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
