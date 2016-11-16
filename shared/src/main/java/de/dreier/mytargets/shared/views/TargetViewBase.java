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

package de.dreier.mytargets.shared.views;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.ExploreByTouchHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.SelectableZone;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.drawable.TargetImpactAggregationDrawable;
import de.dreier.mytargets.shared.targets.models.TargetModelBase;
import de.dreier.mytargets.shared.targets.models.WAFull;
import de.dreier.mytargets.shared.utils.EndRenderer;
import de.dreier.mytargets.shared.utils.ParcelsBundler;
import icepick.Icepick;
import icepick.State;

public abstract class TargetViewBase extends View implements View.OnTouchListener {
    private final TargetAccessibilityTouchHelper touchHelper = new TargetAccessibilityTouchHelper(
            this);
    private final List<VirtualView> virtualViews = new ArrayList<>();
    @State
    protected int currentShotIndex = 0;
    @State
    protected int lastSetArrow = -1;
    @State(ParcelsBundler.class)
    protected EndRenderer endRenderer = new EndRenderer();
    @State(ParcelsBundler.class)
    protected List<Shot> shots;
    @State(ParcelsBundler.class)
    protected RoundTemplate round;
    protected int contentWidth;
    protected int contentHeight;
    protected OnEndFinishedListener setListener = null;
    protected float curAnimationProgress;
    protected boolean zoneSelectionMode = true;
    protected float density;
    protected float outFromX;
    protected float outFromY;
    // TODO don't expose as public
    public TargetImpactAggregationDrawable targetDrawable;
    protected TargetModelBase targetModel;
    protected List<SelectableZone> selectableZones;
    private Target target;
    private Drawable.Callback invalidateCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(@NonNull Drawable drawable) {
            invalidate();
        }

        @Override
        public void scheduleDrawable(@NonNull Drawable drawable, @NonNull Runnable runnable, long l) {

        }

        @Override
        public void unscheduleDrawable(@NonNull Drawable drawable, @NonNull Runnable runnable) {

        }
    };

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
        ViewCompat.setAccessibilityDelegate(this, touchHelper);
        if (isInEditMode()) {
            round = new RoundTemplate();
            round.arrowsPerEnd = 3;
            shots = new ArrayList<>(3);
            shots.get(0).zone = 0;
            shots.get(0).x = 0.01f;
            shots.get(0).y = 0.05f;
            target = new Target(WAFull.ID, 0);
            targetModel = target.getModel();
            targetDrawable = target.getImpactAggregationDrawable();
            endRenderer.init(this, density, target);
            endRenderer.setShots(shots);
            setCurrentShotIndex(1);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        targetDrawable.setCallback(null);
        targetDrawable.cleanup();
    }

    public void reset() {
        animateToZoomSpot();
        notifyTargetShotsChanged();
    }

    protected void setRoundTemplate(RoundTemplate r) {
        round = r;
        target = r.target;
        targetModel = r.target.getModel();
        targetDrawable = r.target.getImpactAggregationDrawable();
        targetDrawable.setCallback(invalidateCallback);
        endRenderer.init(this, density, r.target);
        updateSelectableZones();
    }

    protected void updateSelectableZones() {
        selectableZones = target.getSelectableZoneList(getCurrentShotIndex());
        updateVirtualViews();
    }

    private void updateVirtualViews() {
        virtualViews.clear();
        if (zoneSelectionMode) {
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

    public boolean dispatchHoverEvent(MotionEvent event) {
        return touchHelper.dispatchHoverEvent(event) || super.dispatchHoverEvent(event);
    }

    protected abstract Coordinate initAnimationPositions(int i);

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        contentWidth = getWidth();
        contentHeight = getHeight();
        calcSizes();
        updateVirtualViews();
        invalidate();
    }

    protected int getSelectableZoneIndexFromShot(Shot shot) {
        int i = 0;
        for (SelectableZone selectableZone : selectableZones) {
            if (shot.zone == selectableZone.index) {
                return i;
            }
            i++;
        }
        return -1;
    }

    protected abstract void calcSizes();

    public void setOnTargetSetListener(OnEndFinishedListener listener) {
        setListener = listener;
    }

    @NonNull
    protected abstract Rect getSelectableZonePosition(int i);

    public boolean onTouch(View view, MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();

        boolean currentlySelecting = getCurrentShotIndex() < round.arrowsPerEnd && shots.get(
                getCurrentShotIndex()).zone != Shot.NOTHING_SELECTED;
        if (selectPreviousShots(motionEvent, x, y) && !currentlySelecting) {
            return true;
        }

        Shot shot = getShotFromPos(x, y);
        if (shot == null) {
            return true;
        }

        // If a valid selection was made save it in the end
        if (getCurrentShotIndex() < round.arrowsPerEnd &&
                (shots.get(getCurrentShotIndex()).zone != shot.zone || !zoneSelectionMode)) {
            shots.get(getCurrentShotIndex()).zone = shot.zone;
            shots.get(getCurrentShotIndex()).x = shot.x;
            shots.get(getCurrentShotIndex()).y = shot.y;
            endRenderer.setSelection(
                    getCurrentShotIndex(), initAnimationPositions(getCurrentShotIndex()),
                    zoneSelectionMode ? EndRenderer.MAX_CIRCLE_SIZE : 0);
            invalidate();
        }

        // If finger is released go to next shoot
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            //go to next page
            if (getCurrentShotIndex() == lastSetArrow + 1) {
                lastSetArrow++;
            }

            onArrowChanged(lastSetArrow + 1);
            return true;
        }
        return true;
    }

    protected void onArrowChanged(final int i) {
        animateCircle(i);
        animateFromZoomSpot();
        notifyTargetShotsChanged();
    }

    protected void notifyTargetShotsChanged() {
        if (lastSetArrow + 1 >= round.arrowsPerEnd && setListener != null) {
            setListener.onEndFinished(shots, false);
        }
        invalidate();
    }

    private void animateCircle(int i) {
        Coordinate pos = null;
        int nextSel = i;
        if (i > -1 && i < round.arrowsPerEnd && shots.get(i).zone > Shot.NOTHING_SELECTED) {
            pos = initAnimationPositions(i);
        } else {
            nextSel = EndRenderer.NO_SELECTION;
        }
        int initialSize = zoneSelectionMode ? EndRenderer.MAX_CIRCLE_SIZE : 0;
        endRenderer.animateToSelection(nextSel, pos, initialSize);
        setCurrentShotIndex(i);
    }

    protected void animateFromZoomSpot() {
        // Extension point for sub-classes making use of spots
    }

    protected void animateToZoomSpot() {
        // Extension point for sub-classes making use of spots
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

    @Override
    public Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
        endRenderer.init(this, density, target);
    }

    protected int getCurrentShotIndex() {
        return currentShotIndex;
    }

    protected void setCurrentShotIndex(int currentArrow) {
        this.currentShotIndex = currentArrow;
        if (targetModel.dependsOnArrowIndex()) {
            updateSelectableZones();
        }
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

    public interface OnEndFinishedListener {
        void onEndFinished(List<Shot> shotList, boolean remote);
    }
}
