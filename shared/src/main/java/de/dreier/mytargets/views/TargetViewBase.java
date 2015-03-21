package de.dreier.mytargets.views;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import de.dreier.mytargets.models.Coordinate;
import de.dreier.mytargets.models.Passe;
import de.dreier.mytargets.models.Round;
import de.dreier.mytargets.models.Shot;
import de.dreier.mytargets.utils.OnTargetSetListener;
import de.dreier.mytargets.utils.PasseDrawer;
import de.dreier.mytargets.utils.Target;

/**
 * Created by Florian on 18.03.2015.
 */
public abstract class TargetViewBase extends View implements View.OnTouchListener {
    protected PasseDrawer mPasseDrawer;
    protected int currentArrow = 0;
    protected int lastSetArrow = -1;
    protected Passe mPasse;
    protected Round roundInfo;
    protected int mCurSelecting = -1;
    protected int contentWidth;
    protected int contentHeight;
    protected OnTargetSetListener setListener = null;
    protected float mCurAnimationProgress;
    protected boolean mModeEasy = true;
    protected float density;
    protected int mZoneCount;
    protected float mOutFromX;
    protected float mOutFromY;

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
        mPasse = new Passe(roundInfo.ppp);
        mPasseDrawer.setPasse(mPasse);
        invalidate();
    }

    public void setRoundInfo(Round r) {
        roundInfo = r;
        mZoneCount = Target.target_rounds[r.target].length;
        mPasseDrawer = new PasseDrawer(this, density, roundInfo.target);
        reset();
    }

    protected abstract Coordinate initAnimationPositions(int i);

    public void saveState(Bundle b) {
        b.putSerializable("passe", mPasse);
        b.putSerializable("roundInfo", roundInfo);
        b.putInt("currentArrow", currentArrow);
        b.putInt("lastSetArrow", lastSetArrow);
    }

    public void restoreState(Bundle b) {
        mPasse = (Passe) b.getSerializable("passe");
        currentArrow = b.getInt("currentArrow");
        lastSetArrow = b.getInt("lastSetArrow");
        roundInfo = (Round) b.getSerializable("roundInfo");
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
            mPasseDrawer.cancel();
        }

        float x = motionEvent.getX();
        float y = motionEvent.getY();

        if (selectPreviousShots(motionEvent, x, y)) {
            return true;
        }

        Shot shot = getShotFromPos(x, y);
        if (shot == null) {
            return true;
        }

        // Make 3er Spot 9 appear as one
        if (shot.zone == 1 && roundInfo.target == 3 && roundInfo.compound) {
            shot.zone = 2;
        }

        // If a valid selection was made save it in the passe
        if (currentArrow < roundInfo.ppp &&
                (mPasse.shot[currentArrow].zone != shot.zone || !mModeEasy)) {
            mPasse.shot[currentArrow].zone = shot.zone;
            mPasse.shot[currentArrow].x = shot.x;
            mPasse.shot[currentArrow].y = shot.y;
            mPasseDrawer.setSelection(currentArrow, initAnimationPositions(currentArrow),
                    mModeEasy ? PasseDrawer.MAX_CIRCLE_SIZE : 0);
            invalidate();
        }

        // If finger is released go to next shoot
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            //go to next page
            if (currentArrow == lastSetArrow + 1) {
                lastSetArrow++;
            }

            animateSelectCircle(lastSetArrow + 1);

            if (lastSetArrow + 1 >= roundInfo.ppp && setListener != null) {
                mPasse.id = setListener.onTargetSet(new Passe(mPasse), false);
            }

            return true;
        }
        return true;
    }

    protected void animateSelectCircle(final int i) {
        if (i > -1 && i < roundInfo.ppp && mPasse.shot[i].zone >= -1) {
            mPasseDrawer.animateToSelection(i, initAnimationPositions(i), mModeEasy ? PasseDrawer.MAX_CIRCLE_SIZE : 0);
        } else {
            mPasseDrawer.animateToSelection(PasseDrawer.NO_SELECTION, null, mModeEasy ? PasseDrawer.MAX_CIRCLE_SIZE : 0);
        }
        currentArrow = i;
    }

    protected abstract Shot getShotFromPos(float x, float y);

    protected abstract boolean selectPreviousShots(MotionEvent motionEvent, float x, float y);
}
