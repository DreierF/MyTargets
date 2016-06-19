/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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
import de.dreier.mytargets.models.EShowMode;
import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.shared.models.ArrowNumber;
import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.targets.SelectableZone;
import de.dreier.mytargets.shared.utils.PasseDrawer;
import de.dreier.mytargets.shared.views.TargetViewBase;
import icepick.Icepick;
import icepick.State;

public class TargetView extends TargetViewBase {
    private static final int ZOOM_FACTOR = 2;
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
    private RectF orgRect;
    private List<ArrowNumber> arrowNumbers = new ArrayList<>();
    private TextPaint textPaint;
    private Paint borderPaint;
    private ValueAnimator animator;
    private float inputModeProgress = 0;
    private ValueAnimator inputAnimator;

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
        this.passe = passe;
        passeDrawer.setSelection(currentArrow, null, PasseDrawer.MAX_CIRCLE_SIZE);
        passeDrawer.setPasse(passe);
        if (passe.getId() != -1) {
            switchMode(!passe.exact, true);
        }
        animateFromZoomSpot();
        invalidate();
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
        invalidate();
    }

    public void setOldShoots(ArrayList<Passe> oldOnes) {
        oldPasses = oldOnes;
        invalidate();
    }

    @Override
    public void setRoundTemplate(RoundTemplate r) {
        super.setRoundTemplate(r);
        switchMode(SettingsManager.getInputMode(), false);
        initSpotBounds();
    }

    private void initSpotBounds() {
        Rect rect = new Rect(0, 0, 500, 500);
        if (targetModel.getFaceCount() > 1) {
            spotRects = new RectF[targetModel.getFaceCount()];
            for (int i = 0; i < targetModel.getFaceCount(); i++) {
                spotRects[i] = targetDrawable.getBoundsF(i, rect);
            }
        } else {
            spotRects = new RectF[1];
            spotRects[0] = new RectF(rect);
        }
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
        if (currentArrow > -1 && currentArrow < round.arrowsPerPasse) {
            curZone = passe.shot[currentArrow].zone;
        } else {
            curZone = -2;
        }

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

        // Draw right indicator
        drawRightSelectorBar(canvas);

        // Draw all points of this passe at the bottom
        passeDrawer.draw(canvas);
    }

    private void drawZoomedInTarget(Canvas canvas) {
        float px = passe.shot[currentArrow].x;
        float py = passe.shot[currentArrow].y;
        int radius2 = (int) (radius * ZOOM_FACTOR);
        int x = (int) ((midX - orgMidX) * ZOOM_FACTOR + orgMidX - px * (orgRadius + 30 * density));
        int y = (int) ((midY - orgMidY) * ZOOM_FACTOR + orgMidY - py * (orgRadius + 30 * density) -
                60 * density);
        drawTarget(canvas, x, y, radius2);
    }

    private void drawTarget(Canvas canvas, float x, float y, float radius) {
        // Erase background
        fillPaint.setColor(0xfffafafa);
        canvas.drawRect(0, 0, contentWidth, contentHeight, fillPaint);

        // Draw actual target face
        targetDrawable.setBounds((int) (x - radius), (int) (y - radius), (int) (x + radius),
                (int) (y + radius));
        targetDrawable.draw(canvas);

        // Draw exact arrow position
        if (!zoneSelectionMode) {
            drawArrows(canvas);
        }
    }

    private void drawArrows(Canvas canvas) {
        for (int i = 0; i < passe.shot.length && i <= lastSetArrow + 1; i++) {
            Shot shot = passe.shot[i];
            if (shot.zone == Shot.NOTHING_SELECTED) {
                continue;
            }
            if (i == currentArrow) {
                targetDrawable.drawFocusedArrow(canvas, shot);
                continue;
            }
            targetDrawable.drawArrow(canvas, shot, false);
        }

        if (showMode != EShowMode.END) {
            //noinspection Convert2streamapi
            for (Passe p : oldPasses) {
                if (shouldShowPasse(p)) {
                    targetDrawable.drawArrows(canvas, p, true);
                }
            }
        }

        int spots = targetModel.getFaceCount();
        for (int i = 0; i < spots; i++) {
            Midpoint m = getMidpoint(i);
            if (m.count >= 2) {
                targetDrawable.drawArrowAvg(canvas, m.sumX / m.count,
                        m.sumY / m.count, i);
            }
        }
    }

    private boolean shouldShowPasse(Passe p) {
        return p.getId() != passe
                .getId() && (showMode == EShowMode.TRAINING || p.roundId == passe.roundId);
    }

    private Midpoint getMidpoint(int spot) {
        Midpoint m = new Midpoint();
        int spots = targetModel.getFaceCount();
        for (int i = spot; i < passe.shot.length && i <= lastSetArrow + 1; i += spots) {
            Shot shot = passe.shot[i];
            if (shot.zone != Shot.NOTHING_SELECTED && i != currentArrow) {
                m.add(shot);
            }
        }

        if (showMode == EShowMode.END) {
            return m;
        }

        return Stream.of(oldPasses)
                .filter(this::shouldShowPasse)
                .map(Passe::shotList)
                .flatMap(Stream::of)
                .filter(s -> s.index % spots == spot)
                .reduce(m, Midpoint::add);
    }

    @Override
    protected Coordinate initAnimationPositions(int i) {
        Coordinate coordinate = new Coordinate();
        if (zoneSelectionMode) {
            coordinate.x = contentWidth - 100 * density;
            int index = selectableZones.indexOf(new SelectableZone(passe.shot[i].zone, null, "", 0));
            int indicatorHeight = contentHeight / selectableZones.size();
            coordinate.y = indicatorHeight * index + indicatorHeight / 2.0f;
        } else {
            coordinate.x = orgMidX + orgRadius * passe.shot[i].x;
            coordinate.y = orgMidY + orgRadius * passe.shot[i].y;
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
        orgRect = new RectF(
                orgMidX - orgRadius,
                orgMidY - orgRadius,
                orgMidX + orgRadius,
                orgMidY + orgRadius);
        RectF rect = new RectF();
        rect.left = 30 * density;
        rect.right = availableWidth - 30 * density;
        rect.top = 10 * density;
        rect.bottom = orgMidY - orgRadius - 10 * density;
        passeDrawer.animateToRect(rect);
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
        int arrow = passeDrawer.getPressedPosition(x, y);
        if (arrow != -1 && currentArrow != arrow) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (longPressTimer != null) {
                    passeDrawer.setPressed(-1);
                    longPressTimer.cancel();
                    longPressTimer = null;
                    super.onArrowChanged(arrow);
                }
            } else if (passeDrawer.getPressed() != arrow) {
                // If new item gets selected cancel old timer and start new one
                passeDrawer.setPressed(arrow);
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
            passeDrawer.setPressed(-1);
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
                        currentArrow < round.arrowsPerPasse ? new AccelerateInterpolator() :
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
        if (targetModel.getFaceCount() > 1 && currentArrow < round.arrowsPerPasse && radius > 0 &&
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
                    spotRect.left = orgRect.left + spotRect.left * scale;
                    spotRect.top = orgRect.top + spotRect.top * scale;
                    spotRect.right = orgRect.left + spotRect.right * scale;
                    spotRect.bottom = orgRect.top + spotRect.bottom * scale;

                    float zoomFactor = orgRadius * 2.0f / spotRect.width();
                    radius = (int) (orgRadius * zoomFactor);
                    midX = (int) (radius + orgMidX + (orgRect.left - spotRect
                            .centerX()) * zoomFactor);
                    midY = (int) (radius + orgMidY + (orgRect.top - spotRect
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
    protected void onArrowChanged(int i) {
        if (arrowNumbers.isEmpty() ||
                currentArrow < round.arrowsPerPasse && passe.shot[currentArrow].arrow != null) {
            super.onArrowChanged(i);
        } else {
            List<String> numbersLeft = Stream.of(arrowNumbers).map(an -> an.number)
                    .collect(Collectors.toList());
            for (Shot s : passe.shot) {
                numbersLeft.remove(s.arrow);
            }
            if (numbersLeft.size() == 0 || currentArrow >= round.arrowsPerPasse) {
                super.onArrowChanged(i);
                return;
            } else if (numbersLeft.size() == 1) {
                passe.shot[currentArrow].arrow = numbersLeft.get(0);
                super.onArrowChanged(i);
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
                if (currentArrow < passe.shot.length) {
                    passe.shot[currentArrow].arrow = numbersLeft.get(position);
                }
                dialog.dismiss();
                super.onArrowChanged(i);
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
        final int pressed = passeDrawer.getPressed();
        if (pressed == -1) {
            return;
        }
        longPressTimer = null;
        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
        onArrowChanged(round.arrowsPerPasse);

        new MaterialDialog.Builder(getContext())
                .title(R.string.comment)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("", passe.shot[pressed].comment, (dialog, input) -> {
                    passe.shot[pressed].comment = input.toString();
                    if (lastSetArrow + 1 >= round.arrowsPerPasse && setListener != null) {
                        setListener.onTargetSet(new Passe(passe), false);
                    }
                })
                .negativeText(android.R.string.cancel)
                .dismissListener(dialog -> {
                    passeDrawer.setPressed(-1);
                    invalidate();
                })
                .show();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        // Cancel animation
        if (inputModeTransitioning || zoomTransitioning) {
            passeDrawer.cancel();
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

    @Override
    public Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
    }

    private class Midpoint {
        float count = 0;
        float sumX = 0;
        float sumY = 0;

        public Midpoint add(Shot s) {
            sumX += s.x;
            sumY += s.y;
            count++;
            return this;
        }
    }
}
