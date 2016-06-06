package de.dreier.mytargets.shared.views;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.TargetDrawable;
import de.dreier.mytargets.shared.targets.TargetModelBase;
import de.dreier.mytargets.shared.targets.TargetModelBase.SelectableZone;
import de.dreier.mytargets.shared.utils.OnTargetSetListener;
import de.dreier.mytargets.shared.utils.ParcelsBundler;
import de.dreier.mytargets.shared.utils.PasseDrawer;
import icepick.Icepick;
import icepick.State;

public abstract class TargetViewBase extends View implements View.OnTouchListener {
    @State
    protected int currentArrow = 0;
    @State
    protected int lastSetArrow = -1;
    @State(ParcelsBundler.class)
    protected PasseDrawer passeDrawer = new PasseDrawer();
    @State(ParcelsBundler.class)
    protected Passe passe;
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
    protected Target target;
    protected TargetModelBase targetModel;
    protected List<SelectableZone> selectableZones;

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
        if (isInEditMode()) {
            round = new RoundTemplate();
            round.arrowsPerPasse = 3;
            passe = new Passe(3);
            passeDrawer.setPasse(passe);
        }
    }

    public void reset() {
        currentArrow = 0;
        lastSetArrow = -1;
        passe = new Passe(round.arrowsPerPasse);
        passeDrawer.setPasse(passe);
        selectableZones = target.getSelectableZoneList(currentArrow);
        animateToZoomSpot();
        invalidate();
    }

    public void setRoundId(long roundId) {
        passe.roundId = roundId;
    }

    public void setRoundTemplate(RoundTemplate r) {
        round = r;
        target = r.target;
        targetModel = r.target.getModel();
        targetDrawable = r.target.getDrawable();
        passeDrawer.init(this, density, r.target);
        selectableZones = target.getSelectableZoneList(currentArrow);
        reset();
    }

    protected abstract Coordinate initAnimationPositions(int i);

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
                (passe.shot[currentArrow].zone != shot.zone || !zoneSelectionMode)) {
            passe.shot[currentArrow].zone = shot.zone;
            passe.shot[currentArrow].x = shot.x;
            passe.shot[currentArrow].y = shot.y;
            passeDrawer.setSelection(currentArrow, initAnimationPositions(currentArrow),
                    zoneSelectionMode ? PasseDrawer.MAX_CIRCLE_SIZE : 0);
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
            selectableZones = target.getSelectableZoneList(currentArrow);
        }

        animateFromZoomSpot();

        if (lastSetArrow + 1 >= round.arrowsPerPasse && setListener != null) {
            passe.exact = !zoneSelectionMode;
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
        int initialSize = zoneSelectionMode ? PasseDrawer.MAX_CIRCLE_SIZE : 0;
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

    @Override public Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
        passeDrawer.init(this, density, target);
    }
}
