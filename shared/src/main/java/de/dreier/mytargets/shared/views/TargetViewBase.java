package de.dreier.mytargets.shared.views;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.parceler.Parcel;
import org.parceler.Parcels;

import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.target.TargetDrawable;
import de.dreier.mytargets.shared.utils.OnTargetSetListener;
import de.dreier.mytargets.shared.utils.PasseDrawer;

public abstract class TargetViewBase extends View implements View.OnTouchListener {
    protected PasseDrawer passeDrawer;
    protected int currentArrow = 0;
    protected int lastSetArrow = -1;
    protected Passe passe;
    protected RoundTemplate round;
    protected int mCurSelecting = -1;
    protected int contentWidth;
    protected int contentHeight;
    protected OnTargetSetListener setListener = null;
    protected float curAnimationProgress;
    protected boolean mZoneSelectionMode = true;
    protected float density;
    protected int mZoneCount;
    protected float outFromX;
    protected float outFromY;
    protected TargetDrawable target;

    public TargetViewBase(Context context) {
        super(context);
        setOnTouchListener(this);
    }

    protected TargetViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    protected TargetViewBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
    }

    public void reset() {
        currentArrow = 0;
        lastSetArrow = -1;
        mCurSelecting = -1;
        target = round.target.getDrawable();
        passe = new Passe(round.arrowsPerPasse);
        passeDrawer.setPasse(passe);
        animateToZoomSpot();
        invalidate();
    }

    public void setRoundTemplate(RoundTemplate r) {
        round = r;
        mZoneCount = r.target.getDrawable().getZones();
        passeDrawer = new PasseDrawer(this, density, round.target.getDrawable());
        reset();
    }

    protected abstract Coordinate initAnimationPositions(int i);

    @Override
    public Parcelable onSaveInstanceState() {
        SavedState ss = new SavedState();
        ss.superState = super.onSaveInstanceState();
        ss.currentArrow = this.currentArrow;
        ss.lastSetArrow = this.lastSetArrow;
        ss.passe = this.passe;
        ss.round = this.round;
        ss.passeDrawer = new Bundle();
        passeDrawer.saveState(ss.passeDrawer);
        return Parcels.wrap(ss);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = Parcels.unwrap(state);
        super.onRestoreInstanceState(ss.superState);
        this.currentArrow = ss.currentArrow;
        this.lastSetArrow = ss.lastSetArrow;
        this.passe = ss.passe;
        this.round = ss.round;
        this.passeDrawer.restoreState(ss.passeDrawer);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        contentWidth = getWidth();
        contentHeight = getHeight();
        calcSizes();
    }

    protected abstract void calcSizes();

    public void setOnTargetSetListener(OnTargetSetListener listener) {
        setListener = listener;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        // Cancel animation
        if (mCurSelecting != -1) {
            passeDrawer.cancel();
        }

        float x = motionEvent.getX();
        float y = motionEvent.getY();

        boolean currentlySelecting = currentArrow < round.arrowsPerPasse && passe.shot[currentArrow].zone != Shot.NOTHING_SELECTED;
        if (selectPreviousShots(motionEvent, x, y) && !currentlySelecting) {
            return true;
        }

        Shot shot = getShotFromPos(x, y);
        if (shot == null) {
            return true;
        }

        // If a valid selection was made save it in the passe
        if (currentArrow < round.arrowsPerPasse &&
                (passe.shot[currentArrow].zone != shot.zone || !mZoneSelectionMode)) {
            passe.shot[currentArrow].zone = shot.zone;
            passe.shot[currentArrow].x = shot.x;
            passe.shot[currentArrow].y = shot.y;
            passeDrawer.setSelection(currentArrow, initAnimationPositions(currentArrow),
                    mZoneSelectionMode ? PasseDrawer.MAX_CIRCLE_SIZE : 0);
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
        animateFromZoomSpot();

        if (lastSetArrow + 1 >= round.arrowsPerPasse && setListener != null) {
            passe.setId(setListener.onTargetSet(new Passe(passe), false));
        }
    }

    private void animateCircle(int i) {
        Coordinate pos = null;
        int nextSel = i;
        if (i > -1 && i < round.arrowsPerPasse && passe.shot[i].zone > Shot.NOTHING_SELECTED) {
            pos = initAnimationPositions(i);
        } else {
            nextSel = PasseDrawer.NO_SELECTION;
        }
        int initialSize = mZoneSelectionMode ? PasseDrawer.MAX_CIRCLE_SIZE : 0;
        passeDrawer.animateToSelection(nextSel, pos, initialSize);
        currentArrow = i;
    }

    protected void animateFromZoomSpot() {
    }

    protected void animateToZoomSpot() {
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

    @Parcel
    public static class SavedState {
        public Parcelable superState;
        public Bundle passeDrawer;
        public int currentArrow;
        public int lastSetArrow;
        public Passe passe;
        public RoundTemplate round;
    }
}
