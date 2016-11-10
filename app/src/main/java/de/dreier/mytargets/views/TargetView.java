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
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.dreier.mytargets.R;
import de.dreier.mytargets.interfaces.OnEndUpdatedListener;
import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.models.EShowMode;
import de.dreier.mytargets.shared.analysis.aggregation.EAggregationStrategy;
import de.dreier.mytargets.shared.models.ArrowNumber;
import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.SelectableZone;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.utils.EndRenderer;
import de.dreier.mytargets.shared.views.TargetViewBase;
import icepick.Icepick;
import icepick.State;

public class TargetView extends TargetViewBase {
    private final Handler h = new Handler();
    @State
    ArrayList<Passe> oldPasses;
    private boolean inputModeTransitioning = false;
    private boolean zoomTransitioning = false;
    private float radius, midX, midY;
    private Paint fillPaint;
    private EShowMode showMode = EShowMode.END;
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

    public void setPasse(Passe passe) {
        currentArrow = passe.shot.length;
        lastSetArrow = passe.shot.length;
        this.end = passe;
        endRenderer.setSelection(currentArrow, null, EndRenderer.MAX_CIRCLE_SIZE);
        endRenderer.setShots(passe.shotList());
        if (passe.getId() != -1) {
            switchMode(!passe.exact, true);
        }
        end = passe;
        animateFromZoomSpot();
        notifyTargetShotsChanged();
    }

    @Override
    public void reset() {
        super.reset();
        inputModeTransitioning = false;
        zoomTransitioning = false;
    }

    public void setArrowNumbers(@NonNull List<ArrowNumber> arrowNumbers) {
        this.arrowNumbers = arrowNumbers;
    }

    public void setShowMode(EShowMode showMode) {
        this.showMode = showMode;
        SettingsManager.setShowMode(showMode);
        notifyTargetOldShotsChanged();
    }

    public void setOldShoots(ArrayList<Passe> oldOnes) {
        oldPasses = oldOnes;
        notifyTargetOldShotsChanged();
    }

    @Override
    public void setRoundTemplate(RoundTemplate r) {
        super.setRoundTemplate(r);
        switchMode(SettingsManager.getInputMode(), false);
        initSpotBounds();
    }

    private void initSpotBounds() {
        Rect rect = new Rect(0, 0, 1000, 1000);
        targetDrawable.setBounds(rect);
        if (targetModel.getFaceCount() > 1) {
            spotRects = new RectF[targetModel.getFaceCount()];
            for (int i = 0; i < targetModel.getFaceCount(); i++) {
                spotRects[i] = targetDrawable.getBoundsF(i, rect);
            }
        } else {
            spotRects = new RectF[1];
            spotRects[0] = new RectF(rect);
        }
        targetDrawable.setMatrix(new Matrix());
    }

    private void init() {
        // Set up a default TextPaint object
        density = getResources().getDisplayMetrics().density;
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

        if (!isInEditMode()) {
            showMode = SettingsManager.getShowMode();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int curZone;
        if (currentArrow > -1 && currentArrow < round.arrowsPerEnd) {
            curZone = end.shot[currentArrow].zone;
        } else {
            curZone = -2;
        }

        drawBackground(canvas);

        // Draw target
        if (zoomTransitioning) {
            drawTarget(canvas, outFromX + (midX - outFromX) * curAnimationProgress,
                    outFromY + (midY - outFromY) * curAnimationProgress,
                    oldRadius + (radius - oldRadius) * curAnimationProgress);
        } else {
            if (!zoneSelectionMode && curZone >= -1) {
                drawZoomedInTarget(canvas);
            } else {
                drawTarget(canvas, midX, midY, radius);
            }
        }

        // Draw exact arrow position
        if (!zoneSelectionMode) {
            drawArrows(canvas);
        }

        // Draw right indicator
        drawRightSelectorBar(canvas);

        // Draw all points of this end at the bottom
        endRenderer.draw(canvas);
    }

    private void drawZoomedInTarget(Canvas canvas) {
        float px = end.shot[currentArrow].x;
        float py = end.shot[currentArrow].y;
        int radius2 = (int) (radius * targetZoomFactor);
        int x = (int) ((midX - orgMidX) * targetZoomFactor + orgMidX
                - px * (targetZoomFactor - 1) * orgRadius - px * 30 * density);
        int y = (int) ((midY - orgMidY) * targetZoomFactor + orgMidY
                - py * (targetZoomFactor - 1) * orgRadius - py * 30 * density - 60 * density);
        drawTarget(canvas, x, y, radius2);
    }

    private void drawBackground(Canvas canvas) {
        // Erase background
        fillPaint.setColor(0xfffafafa);
        canvas.drawRect(0, 0, contentWidth, contentHeight, fillPaint);
    }

    private void drawTarget(Canvas canvas, float x, float y, float radius) {
        canvas.save();
        // Draw actual target face
        targetDrawable.setBounds((int) (x - radius), (int) (y - radius), (int) (x + radius),
                (int) (y + radius));
        targetDrawable.draw(canvas);
        canvas.restore();
    }

    private void drawArrows(Canvas canvas) {
        canvas.save();

        // TODO remove for loop
        for (int i = 0; i < end.shot.length && i <= lastSetArrow + 1; i++) {
            Shot shot = end.shot[i];
            if (shot.zone == Shot.NOTHING_SELECTED) {
                continue;
            }
            if (shot.index == currentArrow) {
                targetDrawable.drawFocusedArrow(canvas, shot);
                break;
            }
        }
        canvas.restore();
    }

    private void notifyTargetOldShotsChanged() {
        if (showMode != EShowMode.END && !zoneSelectionMode) {
            final List<Shot> transparentShots = Stream.of(oldPasses)
                    .filter(this::shouldShowEnd)
                    .flatMap(p -> Stream.of(p.shotList()))
                    .collect(Collectors.toList());
            targetDrawable.setTransparentShots(transparentShots);
        } else {
            targetDrawable.setTransparentShots(Collections.emptyList());
        }
        invalidate();
        triggerUpdate();
    }

    private boolean shouldShowEnd(Passe p) {
        return p.getId() != end.getId() &&
                (showMode == EShowMode.TRAINING || p.roundId == end.roundId) && p.exact;
    }

    protected void notifyTargetShotsChanged() {
        List<Shot> shots = new ArrayList<>();
        for (int i = 0; i < end.shot.length && i <= lastSetArrow + 1; i++) {
            Shot shot = end.shot[i];
            if (shot.zone != Shot.NOTHING_SELECTED && shot.index != currentArrow) {
                shots.add(shot);
            }
        }
        targetDrawable.setShots(shots);
        super.notifyTargetShotsChanged();
        triggerUpdate();
    }

    @Override
    protected Coordinate initAnimationPositions(int i) {
        Coordinate coordinate = new Coordinate();
        if (zoneSelectionMode) {
            coordinate.x = contentWidth - 100 * density;
            int indicatorHeight = contentHeight / selectableZones.size();
            int index = getSelectableZoneIndexFromShot(end.shot[i]);
            coordinate.y = indicatorHeight * index + indicatorHeight / 2.0f;
        } else {
            coordinate.x = orgMidX + orgRadius * end.shot[i].x;
            coordinate.y = orgMidY + orgRadius * end.shot[i].y;
        }
        return coordinate;
    }

    @Override
    protected void calcSizes() {
        int availableWidth = zoneSelectionMode ? (int) (contentWidth - 60 * density) : contentWidth;
        float radH = (contentHeight - 10 * density) / 2.45f;
        float radW = (availableWidth - (zoneSelectionMode ? 70 : 20) * density) * 0.5f;
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

    public void switchMode(boolean mode, boolean animate) {
        if (mode != zoneSelectionMode) {
            // TODO make sure selected arrow indicator transforms as well
            zoneSelectionMode = mode;
            if (animate) {
                animateMode();
            }
            if (zoneSelectionMode) {
                animateFromZoomSpot();
            } else {
                animateToZoomSpot();
            }
            SettingsManager.setInputMode(zoneSelectionMode);
            targetDrawable.drawArrowsEnabled(!zoneSelectionMode);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Shot getShotFromPos(float x, float y) {
        // Create Shot object
        Shot s = new Shot(currentArrow);
        if (zoneSelectionMode) {
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
                    super.onArrowChanged(arrow);
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
        if (targetModel.getFaceCount() > 1) {
            if (!spotFocused) {
                zoomTransitioning = false;
                animateToZoomSpot();
            } else {
                cancelPendingAnimations();
                animator = ValueAnimator.ofFloat(0, 1);
                animator.setInterpolator(
                        currentArrow < round.arrowsPerEnd ? new AccelerateInterpolator() :
                                new AccelerateDecelerateInterpolator());
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
        if (targetModel.getFaceCount() > 1 && currentArrow < round.arrowsPerEnd && radius > 0 &&
                !spotFocused && !zoneSelectionMode && !zoomTransitioning) {
            cancelPendingAnimations();
            animator = ValueAnimator.ofFloat(0, 1);
            animator.setInterpolator(
                    currentArrow == 0 ? new AccelerateDecelerateInterpolator() :
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

                    RectF spotRect = new RectF(spotRects[currentArrow % spotRects.length]);
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
    protected void onArrowChanged(int index) {
        if (arrowNumbers.isEmpty() ||
                currentArrow < round.arrowsPerEnd && end.shot[currentArrow].arrow != null) {
            super.onArrowChanged(index);
        } else {
            List<String> numbersLeft = Stream.of(arrowNumbers).map(an -> an.number)
                    .collect(Collectors.toList());
            for (Shot s : end.shot) {
                numbersLeft.remove(s.arrow);
            }
            if (numbersLeft.size() == 0 || currentArrow >= round.arrowsPerEnd) {
                super.onArrowChanged(index);
                return;
            } else if (numbersLeft.size() == 1) {
                end.shot[currentArrow].arrow = numbersLeft.get(0);
                super.onArrowChanged(index);
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
                if (currentArrow < end.shot.length) {
                    end.shot[currentArrow].arrow = numbersLeft.get(position);
                }
                dialog.dismiss();
                super.onArrowChanged(index);
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
        if (zoneSelectionMode || inputModeTransitioning) {
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
        float percent = zoneSelectionMode ? 1 : 0;
        if (inputModeTransitioning) {
            percent = zoneSelectionMode ? inputModeProgress : 1 - inputModeProgress;
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
        onArrowChanged(round.arrowsPerEnd);

        new MaterialDialog.Builder(getContext())
                .title(R.string.comment)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("", end.shot[pressed].comment, (dialog, input) -> {
                    end.shot[pressed].comment = input.toString();
                    if (lastSetArrow + 1 >= round.arrowsPerEnd && setListener != null) {
                        setListener.onTargetSet(new Passe(end), false);
                    }
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

    public boolean getInputMode() {
        return zoneSelectionMode;
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

    public void setArrowDiameter(Dimension arrowDiameter) {
        this.arrowDiameter = arrowDiameter;
        targetDrawable
                .setArrowDiameter(arrowDiameter, SettingsManager.getInputArrowDiameterScale());
    }

    public void reloadSettings() {
        this.targetZoomFactor = SettingsManager.getInputTargetZoom();
        targetDrawable
                .setArrowDiameter(arrowDiameter, SettingsManager.getInputArrowDiameterScale());
    }

    private void triggerUpdate() {
        if (updateListener != null && oldPasses != null) {
            updateListener.onEndUpdated(end, oldPasses);
        }
    }

    public void setAggregationStrategy(EAggregationStrategy aggregationStrategy) {
        targetDrawable.setAggregationStrategy(aggregationStrategy);
    }
}
