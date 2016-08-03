package de.dreier.mytargets.shared.views;

import android.content.Context;
import android.graphics.Rect;
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
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.SelectableZone;
import de.dreier.mytargets.shared.targets.TargetDrawable;
import de.dreier.mytargets.shared.targets.TargetModelBase;
import de.dreier.mytargets.shared.targets.WAFull;
import de.dreier.mytargets.shared.utils.OnTargetSetListener;
import de.dreier.mytargets.shared.utils.ParcelsBundler;
import de.dreier.mytargets.shared.utils.ScoresDrawer;
import icepick.Icepick;
import icepick.State;

public abstract class TargetViewBase extends View implements View.OnTouchListener {
    private final TargetAccessibilityTouchHelper touchHelper = new TargetAccessibilityTouchHelper(
            this);
    private final List<VirtualView> virtualViews = new ArrayList<>();
    @State
    protected int currentArrow = 0;
    @State
    protected int lastSetArrow = -1;
    @State(ParcelsBundler.class)
    protected ScoresDrawer scoresDrawer = new ScoresDrawer();
    @State(ParcelsBundler.class)
    protected Passe end;
    @State(ParcelsBundler.class)
    protected RoundTemplate round;
    protected int contentWidth;
    protected int contentHeight;
    protected OnTargetSetListener setListener = null;
    protected float curAnimationProgress;
    protected boolean zoneSelectionMode = true;
    protected float density;
    protected float outFromX;
    protected float outFromY;
    protected TargetDrawable targetDrawable;
    protected TargetModelBase targetModel;
    protected List<SelectableZone> selectableZones;
    private Target target;

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
            end = new Passe(3);
            end.shot[0].zone = 0;
            end.shot[0].x = 0.01f;
            end.shot[0].y = 0.05f;
            target = new Target(WAFull.ID, 0);
            targetModel = target.getModel();
            targetDrawable = target.getDrawable();
            scoresDrawer.init(this, density, target);
            scoresDrawer.setShots(end.shotList());
            currentArrow = 1;
            updateSelectableZones();
        }
    }

    public void reset() {
        currentArrow = 0;
        lastSetArrow = -1;
        end = new Passe(round.arrowsPerEnd);
        scoresDrawer.setShots(end.shotList());
        updateSelectableZones();
        animateToZoomSpot();
        invalidate();
    }

    public void setRoundId(long roundId) {
        end.roundId = roundId;
    }

    protected void setRoundTemplate(RoundTemplate r) {
        round = r;
        target = r.target;
        targetModel = r.target.getModel();
        targetDrawable = r.target.getDrawable();
        scoresDrawer.init(this, density, r.target);
        updateSelectableZones();
        reset();
    }

    private void updateSelectableZones() {
        selectableZones = target.getSelectableZoneList(currentArrow);
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
    }

    protected int getSelectableZoneIndexFromShot(Shot shot) {
        return selectableZones.indexOf(new SelectableZone(shot.zone, null, "", 0));
    }

    protected abstract void calcSizes();

    public void setOnTargetSetListener(OnTargetSetListener listener) {
        setListener = listener;
    }

    @NonNull
    protected abstract Rect getSelectableZonePosition(int i);

    public boolean onTouch(View view, MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();

        boolean currentlySelecting = currentArrow < round.arrowsPerEnd && end.shot[currentArrow].zone != Shot.NOTHING_SELECTED;
        if (selectPreviousShots(motionEvent, x, y) && !currentlySelecting) {
            return true;
        }

        Shot shot = getShotFromPos(x, y);
        if (shot == null) {
            return true;
        }

        // If a valid selection was made save it in the end
        if (currentArrow < round.arrowsPerEnd &&
                (end.shot[currentArrow].zone != shot.zone || !zoneSelectionMode)) {
            end.shot[currentArrow].zone = shot.zone;
            end.shot[currentArrow].x = shot.x;
            end.shot[currentArrow].y = shot.y;
            scoresDrawer.setSelection(currentArrow, initAnimationPositions(currentArrow),
                    zoneSelectionMode ? ScoresDrawer.MAX_CIRCLE_SIZE : 0);
            invalidate();
        }

        // If finger is released go to next shoot
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            //go to next page
            if (currentArrow == lastSetArrow + 1) {
                lastSetArrow++;
            }

            onArrowChanged(lastSetArrow + 1);
            return true;
        }
        return true;
    }

    protected void onArrowChanged(final int i) {
        animateCircle(i);

        if (targetModel.dependsOnArrowIndex()) {
            updateSelectableZones();
        }

        animateFromZoomSpot();

        if (lastSetArrow + 1 >= round.arrowsPerEnd && setListener != null) {
            end.exact = !zoneSelectionMode;
            end.setId(setListener.onTargetSet(new Passe(end), false));
        }
    }

    private void animateCircle(int i) {
        Coordinate pos = null;
        int nextSel = i;
        if (i > -1 && i < round.arrowsPerEnd && end.shot[i].zone > Shot.NOTHING_SELECTED) {
            pos = initAnimationPositions(i);
        } else {
            nextSel = ScoresDrawer.NO_SELECTION;
        }
        int initialSize = zoneSelectionMode ? ScoresDrawer.MAX_CIRCLE_SIZE : 0;
        scoresDrawer.animateToSelection(nextSel, pos, initialSize);
        currentArrow = i;
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
        scoresDrawer.init(this, density, target);
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
