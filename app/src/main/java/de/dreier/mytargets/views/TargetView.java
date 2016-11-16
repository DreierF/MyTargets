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
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
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
import icepick.Icepick;

public class TargetView extends TargetViewBase {

    private final Handler h = new Handler();
    private boolean inputModeTransitioning = false;
    private boolean zoomTransitioning = false;
    private float radius, midX, midY;
    private Paint fillPaint;
    private Timer longPressTimer;
    private float oldRadius;
    private RectF[] spotRects;
    private float orgRadius, orgMidX, orgMidY;
    private boolean spotFocused = false;
    private RectF targetRect;
    private List<ArrowNumber> arrowNumbers = new ArrayList<>();
    private TextPaint textPaint;
    private Paint borderPaint;
    private ValueAnimator animator;
    private float inputModeProgress = 0;
    private ValueAnimator inputAnimator;
    private Dimension arrowDiameter;
    private float targetZoomFactor;
    private OnEndUpdatedListener updateListener;

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
        if (end.getId() != 0) {
            setInputMethod(end.exact ? EInputMethod.PLOTTING : EInputMethod.KEYBOARD, true);
        } else {
            animateFromZoomSpot();
        }
        notifyTargetShotsChanged();
    }

    public void setArrow(Dimension diameter, List<ArrowNumber> numbers) {
        this.arrowNumbers = numbers;
        this.arrowDiameter = diameter;
        targetDrawable.setArrowDiameter(diameter, SettingsManager.getInputArrowDiameterScale());
    }

    public void setAggregationStrategy(EAggregationStrategy aggregationStrategy) {
        SettingsManager.setAggregationStrategy(aggregationStrategy);
        targetDrawable.setAggregationStrategy(aggregationStrategy);
    }

    @Override
    public void setTarget(Target t) {
        super.setTarget(t);
        initSpotBounds();
    }

    private void initSpotBounds() {
        Rect rect = new Rect(0, 0, 1000, 1000);
        targetDrawable.setBounds(rect);
        if (target.getModel().getFaceCount() > 1) {
            spotRects = new RectF[target.getModel().getFaceCount()];
            for (int i = 0; i < target.getModel().getFaceCount(); i++) {
                spotRects[i] = targetDrawable.getBoundsF(i, rect);
            }
        } else {
            spotRects = new RectF[1];
            spotRects[0] = new RectF(rect);
        }
        targetDrawable.setMatrix(new Matrix());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw target
        if (zoomTransitioning) {
            drawTarget(canvas, outFromX + (midX - outFromX) * curAnimationProgress,
                    outFromY + (midY - outFromY) * curAnimationProgress,
                    oldRadius + (radius - oldRadius) * curAnimationProgress);
        } else {
            if (inputMethod == EInputMethod.PLOTTING && isCurrentlySelecting()) {
                drawZoomedInTarget(canvas);
            } else {
                drawTarget(canvas, midX, midY, radius);
            }
        }

        // Draw exact arrow position
        if (inputMethod == EInputMethod.PLOTTING) {
            drawFocusedArrow(canvas);
        }

        // Draw right indicator
        drawRightSelectorBar(canvas);

        // Draw all points of this end at the top
        endRenderer.draw(canvas);
    }

    private void drawZoomedInTarget(Canvas canvas) {
        float px = shots.get(getCurrentShotIndex()).x;
        float py = shots.get(getCurrentShotIndex()).y;
        int radius2 = (int) (radius * targetZoomFactor);
        int x = (int) ((midX - orgMidX) * targetZoomFactor + orgMidX
                - px * (targetZoomFactor - 1) * orgRadius - px * 30 * density);
        int y = (int) ((midY - orgMidY) * targetZoomFactor + orgMidY
                - py * (targetZoomFactor - 1) * orgRadius - py * 30 * density - 60 * density);
        drawTarget(canvas, x, y, radius2);
    }

    private void drawTarget(Canvas canvas, float x, float y, float radius) {
        canvas.save();
        // Draw actual target face
        targetDrawable.setBounds((int) (x - radius), (int) (y - radius), (int) (x + radius),
                (int) (y + radius));
        targetDrawable.draw(canvas);
        canvas.restore();
    }

    private void drawFocusedArrow(Canvas canvas) {
        canvas.save();
        if (isCurrentlySelecting()) {
            Shot shot = shots.get(getCurrentShotIndex());
            targetDrawable.drawFocusedArrow(canvas, shot);
        }
        canvas.restore();
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
        triggerUpdate();
    }

    @Override
    protected Coordinate initAnimationPositions(int i) {
        Coordinate coordinate = new Coordinate();
        if (inputMethod == EInputMethod.KEYBOARD) {
            coordinate.x = contentWidth - 100 * density;
            int indicatorHeight = contentHeight / selectableZones.size();
            int index = getSelectableZoneIndexFromShot(shots.get(i));
            coordinate.y = indicatorHeight * index + indicatorHeight / 2.0f;
        } else {
            coordinate.x = orgMidX + orgRadius * shots.get(i).x;
            coordinate.y = orgMidY + orgRadius * shots.get(i).y;
        }
        return coordinate;
    }

    @Override
    protected void calcSizes() {
        int availableWidth = inputMethod == EInputMethod.KEYBOARD ? (int) (contentWidth - 60 * density) : contentWidth;
        float radH = (contentHeight - 10 * density) / 2.45f;
        float radW = (availableWidth - (inputMethod == EInputMethod.KEYBOARD ? 70 : 20) * density) * 0.5f;
        orgRadius = (int) (Math.min(radW, radH));
        orgMidX = availableWidth / 2;
        orgMidY = contentHeight - orgRadius - 10 * density;
        targetRect = new RectF(
                orgMidX - orgRadius,
                orgMidY - orgRadius,
                orgMidX + orgRadius,
                orgMidY + orgRadius);
        RectF rect = new RectF();
        rect.left = 30 * density;
        rect.right = availableWidth - 30 * density;
        rect.top = 10 * density;
        rect.bottom = orgMidY - orgRadius - 10 * density;
        endRenderer.animateToRect(rect);
        animateToZoomSpot();
    }

    public void setInputMethod(EInputMethod mode, boolean animate) {
        if (mode != inputMethod) {
            // TODO make sure selected arrow indicator transforms as well
            inputMethod = mode;
            if (animate) {
                animateMode();
            }
            if (inputMethod == EInputMethod.KEYBOARD) {
                animateFromZoomSpot();
            } else {
                animateToZoomSpot();
            }
            SettingsManager.setInputMethod(inputMethod);
            targetDrawable.drawArrowsEnabled(inputMethod == EInputMethod.PLOTTING);
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
            if (x > contentWidth - 60 * density) {
                int i = (int) (y * selectableZones.size() / (float) contentHeight);
                i = Math.min(Math.max(0, i), selectableZones.size() - 1);
                s.zone = selectableZones.get(i).index;
            } else {
                return null;
            }
        } else { // Handle via target
            s.x = (x - orgMidX) / (orgRadius - 30 * density);
            s.y = (y - orgMidY) / (orgRadius - 30 * density);
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

    private void animateMode() {
        cancelPendingInputAnimations();
        inputAnimator = ValueAnimator.ofFloat(0, 1);
        inputAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        inputAnimator.addUpdateListener(valueAnimator -> {
            inputModeProgress = (Float) valueAnimator.getAnimatedValue();
            invalidate();
        });
        inputAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                inputModeTransitioning = true;
                inputModeProgress = 0;
                initAnimation();
                calcSizes();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                onAnimationEnd(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                inputModeTransitioning = false;
                invalidate();
            }
        });
        inputAnimator.setDuration(300);
        inputAnimator.start();
    }

    private void initAnimation() {
        curAnimationProgress = 0;
        outFromX = midX;
        outFromY = midY;
        oldRadius = radius;
    }

    @Override
    protected void animateFromZoomSpot() {
        if (target.getModel().getFaceCount() > 1) {
            if (!spotFocused) {
                zoomTransitioning = false;
                animateToZoomSpot();
            } else {
                cancelPendingAnimations();
                animator = ValueAnimator.ofFloat(0, 1);
                animator.setInterpolator(
                        getCurrentShotIndex() != EndRenderer.NO_SELECTION
                                ? new AccelerateInterpolator()
                                : new AccelerateDecelerateInterpolator());
                animator.addUpdateListener(valueAnimator -> {
                    curAnimationProgress = (Float) valueAnimator.getAnimatedValue();
                    invalidate();
                });
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        zoomTransitioning = true;
                        initAnimation();
                        radius = orgRadius;
                        midX = orgMidX;
                        midY = orgMidY;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        onAnimationEnd(animation);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        spotFocused = false;
                        zoomTransitioning = false;
                        animateToZoomSpot();
                        invalidate();
                    }
                });
                animator.setDuration(200);
                animator.start();
            }
        }
    }

    @Override
    protected void animateToZoomSpot() {
        if (!spotFocused && !zoomTransitioning) {
            radius = orgRadius;
            midX = orgMidX;
            midY = orgMidY;
        }
        if (target.getModel()
                .getFaceCount() > 1 && getCurrentShotIndex() != EndRenderer.NO_SELECTION && radius > 0 &&
                !spotFocused && inputMethod == EInputMethod.PLOTTING && !zoomTransitioning) {
            cancelPendingAnimations();
            animator = ValueAnimator.ofFloat(0, 1);
            animator.setInterpolator(
                    getCurrentShotIndex() == 0 ? new AccelerateDecelerateInterpolator() :
                            new DecelerateInterpolator());
            animator.addUpdateListener(valueAnimator -> {
                curAnimationProgress = (Float) valueAnimator.getAnimatedValue();
                invalidate();
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    zoomTransitioning = true;
                    initAnimation();

                    RectF spotRect = new RectF(spotRects[getCurrentShotIndex() % spotRects.length]);
                    float scale = orgRadius / 250;
                    spotRect.left = targetRect.left + spotRect.left * scale;
                    spotRect.top = targetRect.top + spotRect.top * scale;
                    spotRect.right = targetRect.left + spotRect.right * scale;
                    spotRect.bottom = targetRect.top + spotRect.bottom * scale;

                    float zoomFactor = orgRadius * 2.0f / spotRect.width();
                    radius = (int) (orgRadius * zoomFactor);
                    midX = (int) (radius + orgMidX + (targetRect.left - spotRect
                            .centerX()) * zoomFactor);
                    midY = (int) (radius + orgMidY + (targetRect.top - spotRect
                            .centerY()) * zoomFactor);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    onAnimationEnd(animation);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    zoomTransitioning = false;
                    spotFocused = true;
                    invalidate();
                }
            });
            /*if (currentArrow == 0) {
                animator.setStartDelay(500);
            }*/
            animator.setDuration(200);
            animator.start();
        }
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
    private void drawRightSelectorBar(Canvas canvas) {
        if (inputMethod == EInputMethod.KEYBOARD || inputModeTransitioning) {
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
    }

    @Override
    @NonNull
    protected Rect getSelectableZonePosition(int i) {
        float percent = inputMethod == EInputMethod.KEYBOARD ? 1 : 0;
        if (inputModeTransitioning) {
            percent = inputMethod == EInputMethod.KEYBOARD ? inputModeProgress : 1 - inputModeProgress;
        }
        final Rect rect = new Rect();
        rect.left = (int) (contentWidth - 60 * percent * density);
        rect.right = (int) (rect.left + 40 * density);
        rect.top = (int) (contentHeight * i / selectableZones.size() + density);
        rect.bottom = (int) (contentHeight * (i + 1) / selectableZones.size() - density);
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
        if (inputModeTransitioning || zoomTransitioning) {
            endRenderer.cancel();
        }

        return super.onTouch(view, motionEvent);
    }

    public EInputMethod getInputMode() {
        return inputMethod;
    }

    private void cancelPendingAnimations() {
        if (animator != null) {
            ValueAnimator tmp = animator;
            animator = null;
            tmp.cancel();
        }
    }

    private void cancelPendingInputAnimations() {
        if (inputAnimator != null) {
            ValueAnimator tmp = inputAnimator;
            inputAnimator = null;
            tmp.cancel();
        }
    }

    public void setUpdateListener(OnEndUpdatedListener updateListener) {
        this.updateListener = updateListener;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
        triggerUpdate();
    }

    public void reloadSettings() {
        this.targetZoomFactor = SettingsManager.getInputTargetZoom();
        targetDrawable
                .setArrowDiameter(arrowDiameter, SettingsManager.getInputArrowDiameterScale());
    }

    private void triggerUpdate() {
        if (updateListener != null) {
            updateListener.onEndUpdated(shots);
        }
    }

    public void setTransparentShots(Stream<Shot> shotStream) {
        targetDrawable.setTransparentShots(shotStream);
    }

    public interface OnEndUpdatedListener {
        void onEndUpdated(List<Shot> shots);
    }
}
