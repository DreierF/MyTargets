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

package de.dreier.mytargets.shared.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.ExploreByTouchHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.models.SelectableZone;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.RoundTemplate;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.targets.drawable.TargetImpactAggregationDrawable;
import de.dreier.mytargets.shared.targets.models.WAFull;
import de.dreier.mytargets.shared.utils.EndRenderer;

public abstract class TargetViewBase extends View implements View.OnTouchListener {
    private final TargetAccessibilityTouchHelper touchHelper = new TargetAccessibilityTouchHelper(
            this);
    private final List<VirtualView> virtualViews = new ArrayList<>();
    /**
     * Zero-based index of the shot that is currently being changed.
     * If no shot is selected it is set to EndRenderer#NO_SELECTION.
     */
    protected int currentShotIndex;
    protected EndRenderer endRenderer = new EndRenderer();
    protected List<Shot> shots;
    protected RoundTemplate round;
    protected OnEndFinishedListener setListener = null;
    protected EInputMethod inputMethod = EInputMethod.KEYBOARD;
    protected float density;
    protected List<SelectableZone> selectableZones;
    protected Target target;
    protected TargetImpactAggregationDrawable targetDrawable;
    protected AnimatorSet animator;
    /**
     * The screen area reserved to show the already entered shots.
     */
    private RectF endRect;

    public TargetViewBase(Context context) {
        super(context);
        setOnTouchListener(this);
        initForDesigner();
    }

    protected TargetViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
        initForDesigner();
    }

    protected TargetViewBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
        initForDesigner();
    }

    private void initForDesigner() {
        density = getResources().getDisplayMetrics().density;
        ViewCompat.setAccessibilityDelegate(this, touchHelper);
        if (isInEditMode()) {
            shots = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                shots.add(new Shot(i));
            }
            shots.get(0).scoringRing = 0;
            shots.get(0).x = 0.01f;
            shots.get(0).y = 0.05f;
            target = new Target(WAFull.ID, 0);
            targetDrawable = target.getImpactAggregationDrawable();
            endRenderer.init(this, density, target);
            endRenderer.setShots(shots);
            setCurrentShotIndex(1);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (targetDrawable != null) {
            targetDrawable.setCallback(null);
            targetDrawable.cleanup();
        }
    }

    public void setTarget(Target t) {
        target = t;
        targetDrawable = target.getImpactAggregationDrawable();
        targetDrawable.setCallback(this);
        endRenderer.init(this, density, target);
        updateSelectableZones();
    }

    public void setEnd(End end) {
        shots = end.getShots();
        setCurrentShotIndex(getNextShotIndex(-1));
        endRenderer.setShots(shots);
        endRenderer.setSelection(getCurrentShotIndex(), null, EndRenderer.MAX_CIRCLE_SIZE);
        animateToNewState();
        notifyTargetShotsChanged();
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        super.invalidateDrawable(drawable);
        invalidate();
    }

    public boolean dispatchHoverEvent(MotionEvent event) {
        return touchHelper.dispatchHoverEvent(event) || super.dispatchHoverEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        updateLayout();
        animateToNewState();
        updateVirtualViews();
        invalidate();
    }

    protected void updateLayout() {
        updateLayoutBounds(getWidth(), getHeight());
        endRect = getEndRect();
    }

    protected abstract void updateLayoutBounds(int width, int height);

    protected abstract RectF getEndRect();

    private void updateVirtualViews() {
        virtualViews.clear();
        if (inputMethod == EInputMethod.KEYBOARD) {
            for (int i = 0; i < selectableZones.size(); i++) {
                VirtualView vv = new VirtualView();
                vv.id = i;
                vv.shot = false;
                vv.description = selectableZones.get(i).text;
                vv.rect = getSelectableZonePosition(i);
                virtualViews.add(vv);
            }
        }
    }

    protected int getSelectableZoneIndexFromShot(Shot shot) {
        int i = 0;
        for (SelectableZone selectableZone : selectableZones) {
            if (shot.scoringRing == selectableZone.index) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public void setOnTargetSetListener(OnEndFinishedListener listener) {
        setListener = listener;
    }

    @NonNull
    protected abstract Rect getSelectableZonePosition(int i);

    public boolean onTouch(View view, MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();

        if (!isCurrentlySelecting() && selectPreviousShots(motionEvent, x, y)) {
            return true;
        }

        Shot shot = getShotFromPos(x, y);
        if (shot == null) {
            return true;
        }

        // If a valid selection was made save it in the end
        if (getCurrentShotIndex() != EndRenderer.NO_SELECTION
                && (shots.get(getCurrentShotIndex()).scoringRing != shot.scoringRing
                || inputMethod == EInputMethod.PLOTTING)) {
            shots.get(getCurrentShotIndex()).scoringRing = shot.scoringRing;
            shots.get(getCurrentShotIndex()).x = shot.x;
            shots.get(getCurrentShotIndex()).y = shot.y;
            endRenderer.setSelection(
                    getCurrentShotIndex(), initAnimationPositions(getCurrentShotIndex()),
                    inputMethod == EInputMethod.KEYBOARD ? EndRenderer.MAX_CIRCLE_SIZE : 0);
            invalidate();
        }

        // If finger is released go to next shoot
        if (motionEvent.getAction() == MotionEvent.ACTION_UP && isCurrentlySelecting()) {
            onArrowChanged();
            return true;
        }
        return true;
    }

    protected boolean isCurrentlySelecting() {
        return getCurrentShotIndex() != EndRenderer.NO_SELECTION
                && shots.get(getCurrentShotIndex()).scoringRing != Shot.NOTHING_SELECTED;
    }

    protected void onArrowChanged() {
        setCurrentShotIndex(getNextShotIndex(currentShotIndex));
        animateToNewState();
        notifyTargetShotsChanged();
        notifyEndFinished();
    }

    /**
     * Returns the index of the next shot, after the given one, which does not have a zone set yet.
     *
     * @param currentShotIndex Index of the current shot.
     *                         Can also be set to -1, to start the search from the first shot.
     * @return Returns a valid index or EndRenderer.NO_SELECTION
     */
    protected int getNextShotIndex(int currentShotIndex) {
        int nextShotIndex = currentShotIndex + 1;
        while (nextShotIndex < shots.size() && shots
                .get(nextShotIndex).scoringRing != Shot.NOTHING_SELECTED) {
            nextShotIndex++;
        }
        if (nextShotIndex == shots.size()) {
            return EndRenderer.NO_SELECTION;
        }
        return nextShotIndex;
    }

    protected void notifyTargetShotsChanged() {
        invalidate();
    }

    protected void notifyEndFinished() {
        if (currentShotIndex == EndRenderer.NO_SELECTION && setListener != null) {
            setListener.onEndFinished(shots, false);
        }
    }

    protected Animator getCircleAnimation() {
        PointF pos = null;
        if (isCurrentlySelecting()) {
            pos = initAnimationPositions(getCurrentShotIndex());
        }
        int initialSize = inputMethod == EInputMethod.KEYBOARD ? EndRenderer.MAX_CIRCLE_SIZE : 0;
        return endRenderer
                .getAnimationToSelection(getCurrentShotIndex(), pos, initialSize, endRect);
    }

    protected abstract PointF initAnimationPositions(int i);

    protected void animateToNewState() {
        if (endRect == null) {
            return;
        }
        // Extension point for sub-classes making use of spots
        List<Animator> animations = new ArrayList<>();
        collectAnimations(animations);
        playAnimations(animations);
    }

    protected void collectAnimations(List<Animator> animations) {
        final Animator animation = getCircleAnimation();
        if (animation != null) {
            animations.add(animation);
        }
    }

    protected void playAnimations(List<Animator> setList) {
        cancelPendingAnimations();
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
                updateVirtualViews();
                invalidate();
            }
        });
        animator.setDuration(300);
        animator.start();
    }

    protected void cancelPendingAnimations() {
        if (animator != null) {
            AnimatorSet tmp = animator;
            animator = null;
            tmp.cancel();
        }
    }

    /**
     * Creates a {@link Shot} object given a position
     *
     * @param x X-Coordinate
     * @param y Y-Coordinate
     * @return Returns a fully populated {@link Shot} object or null if the position is not a valid shot
     */
    protected abstract Shot getShotFromPos(float x, float y);

    protected abstract boolean selectPreviousShots(MotionEvent motionEvent, float x, float y);

    protected int getCurrentShotIndex() {
        return currentShotIndex;
    }

    protected void setCurrentShotIndex(int currentArrow) {
        this.currentShotIndex = currentArrow;
        if (target.getModel().dependsOnArrowIndex()) {
            updateSelectableZones();
        }
    }

    protected void updateSelectableZones() {
        selectableZones = target.getSelectableZoneList(getCurrentShotIndex());
        if (virtualViews.size() > 0) {
            updateVirtualViews();
        }
    }

    public enum EInputMethod {
        KEYBOARD, PLOTTING
    }

    public interface OnEndFinishedListener {
        void onEndFinished(List<Shot> shotList, boolean remote);
    }

    private static class TargetAccessibilityTouchHelper extends ExploreByTouchHelper {

        private final TargetViewBase targetView;

        TargetAccessibilityTouchHelper(TargetViewBase targetView) {
            super(targetView);
            this.targetView = targetView;
        }

        @Override
        protected int getVirtualViewAt(float x, float y) {
            final VirtualView vw = findVirtualViewByPosition(x, y);
            if (vw == null) {
                return ExploreByTouchHelper.INVALID_ID;
            }
            return vw.id;
        }

        private VirtualView findVirtualViewByPosition(float x, float y) {
            for (VirtualView virtualView : targetView.virtualViews) {
                if (virtualView.rect.contains((int) x, (int) y)) {
                    return virtualView;
                }
            }
            return null;
        }

        @Override
        protected void getVisibleVirtualViews(List<Integer> virtualViewIds) {
            for (int i = 0; i < targetView.virtualViews.size(); i++) {
                virtualViewIds.add(targetView.virtualViews.get(i).id);
            }
        }

        @Override
        protected void onPopulateEventForVirtualView(int virtualViewId, AccessibilityEvent event) {
            final VirtualView vw = findVirtualViewById(virtualViewId);
            if (vw == null) {
                return;
            }
            event.getText().add(vw.description);
        }

        private VirtualView findVirtualViewById(int virtualViewId) {
            for (VirtualView virtualView : targetView.virtualViews) {
                if (virtualView.id == virtualViewId) {
                    return virtualView;
                }
            }
            return null;
        }

        @Override
        protected void onPopulateNodeForVirtualView(int virtualViewId, AccessibilityNodeInfoCompat node) {
            final VirtualView vw = findVirtualViewById(virtualViewId);
            if (vw == null) {
                return;
            }

            node.setText(vw.description);
            node.setContentDescription(vw.description);
            node.setClassName(targetView.getClass().getName());
            node.setBoundsInParent(vw.rect);
        }

        @Override
        protected boolean onPerformActionForVirtualView(int virtualViewId, int action, Bundle arguments) {
            return false;
        }
    }

    private class VirtualView {
        public int id;
        public boolean shot;
        public Rect rect;
        public String description;
    }
}
