package de.dreier.mytargets;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import de.dreier.mytargets.models.Circle;
import de.dreier.mytargets.models.OnTargetSetListener;
import de.dreier.mytargets.models.Passe;
import de.dreier.mytargets.models.Round;
import de.dreier.mytargets.models.Target;

public class TargetSelectView extends View implements View.OnTouchListener {

    private int contentWidth, contentHeight;
    private int radius;

    private Paint drawColorP;

    private int currentArrow = 0, lastSetArrow = -1;

    private OnTargetSetListener setListener = null;
    private float density;
    private float mCurAnimationProgress;
    private int mCurSelecting = -1;
    private float mInFromX, mInFromY;
    private float mInToX, mInToY;
    private float mOutFromX, mOutFromY;
    private float mOutToX, mOutToY;
    private int mInZone, mOutZone;
    private int mZoneCount;
    private Passe mPasse;
    private Round roundInfo;
    private int chinHeight;
    private double circRadius;
    private boolean twoRows;
    private Circle circle;

    public TargetSelectView(Context context) {
        super(context);
        init();
    }

    public TargetSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TargetSelectView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void reset() {
        currentArrow = 0;
        lastSetArrow = -1;
        mCurSelecting = -1;
        for (int i = 0; i < roundInfo.ppp; i++) {
            mPasse.shot[i].zone = -2;
        }
        invalidate();
    }

    public void setRoundInfo(Round r) {
        roundInfo = r;
        mZoneCount = Target.target_rounds[r.target].length;
        mPasse = new Passe(r.ppp);
        twoRows = roundInfo.ppp > 3;
        circle = new Circle(density, roundInfo.target);
        reset();
    }

    public void setChinHeight(int chinHeight) {
        this.chinHeight = chinHeight;
    }

    private void init() {
        density = getResources().getDisplayMetrics().density;

        drawColorP = new Paint();
        drawColorP.setAntiAlias(true);

        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int curZone;
        if (currentArrow < roundInfo.ppp)
            curZone = mPasse.shot[currentArrow].zone;
        else
            curZone = -2;

        // Erase background
        drawColorP.setColor(0xffffffff);
        canvas.drawRect(0, 0, contentWidth, contentHeight, drawColorP);

        // Draw all possible points in a circular
        for (int i = -1; i < mZoneCount; i++) {
            float[] coord = getCircularCoords(i);
            circle.draw(canvas, coord[0], coord[1], i, i == curZone ? 23 : 17, false);
        }

        // Draw all points of this passe in the center
        drawSelectedPointsInCenter(canvas);

        // Draw animation
        if (mCurSelecting > -1) {
            // Draw outgoing object
            if (mOutZone >= -1) {
                circle.draw(canvas, mOutFromX + (mOutToX - mOutFromX) * mCurAnimationProgress,
                        mOutFromY + (mOutToY - mOutFromY) * mCurAnimationProgress, mOutZone,
                        (int) (17 - (17 - 10) * mCurAnimationProgress), false);
            }
            // Draw incoming object
            if (mInZone >= -1) {
                circle.draw(canvas, mInFromX + (mInToX - mInFromX) * mCurAnimationProgress,
                        mInFromY + (mInToY - mInFromY) * mCurAnimationProgress, mInZone,
                        (int) (10 + (17 - 10) * mCurAnimationProgress), false);
            }
        }
    }

    private void drawSelectedPointsInCenter(Canvas canvas) {
        // Draw the points
        for (int i = 0; i <= lastSetArrow && i < roundInfo.ppp; i++) {
            float newX = radius + ((i % 3) - 1) * 25 * density;
            float newY = radius - 25 * density + (i < 3 ? -1 : 1) * (twoRows ? 15 * density : -5 * density);
            if (currentArrow != i && mCurSelecting != i) {
                circle.draw(canvas, newX, newY, mPasse.shot[i].zone, 10, false);
            }
        }
    }

    private float[] getCircularCoords(int zone) {
        double degree = Math.toRadians(zone * 360.0 / (double) (mZoneCount + 1));
        float[] coord = new float[2];
        coord[0] = (float) (radius + (Math.cos(degree) * circRadius));
        coord[1] = (float) (radius + (Math.sin(degree) * circRadius));
        float bound = contentHeight - (chinHeight + 15) * density;
        if (coord[1] > bound) {
            coord[1] = bound;
        }
        return coord;
    }

    private void initAnimationPositions() {
        //Calculate positions of outgoing object
        if (currentArrow < roundInfo.ppp && mPasse.shot[currentArrow].zone >= -1) {
            mOutZone = mPasse.shot[currentArrow].zone;
            mOutToX = radius + ((currentArrow % 3) - 1) * 25 * density;
            mOutToY = radius - 25 * density + (currentArrow < 3 ? -1 : 1) * (twoRows ? 15 * density : -5 * density);
            float[] coord = getCircularCoords(mOutZone);
            mOutFromX = coord[0];
            mOutFromY = coord[1];
        } else {
            mOutZone = -2;
        }

        // Calculate positions for incoming object
        if (mCurSelecting < roundInfo.ppp && mPasse.shot[mCurSelecting].zone >= -1) {
            mInZone = mPasse.shot[mCurSelecting].zone;
            float[] coord = getCircularCoords(mInZone);
            mInFromX = coord[0];
            mInFromY = coord[1];
            mInToX = radius + ((mCurSelecting % 3) - 1) * 25 * density;
            mInToY = radius - 25 * density + (mCurSelecting < 3 ? -1 : 1) * (twoRows ? 15 * density : -5 * density);
        } else {
            mInZone = -2;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        contentWidth = getWidth();
        contentHeight = getHeight();
        radius = (int) (contentWidth / 2.0);
        circRadius = radius - 25 * density;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();

        double xDiff = x - radius;
        double yDiff = y - radius;

        if (mCurSelecting != -1)
            return true;

        int zone = -2, rings = Target.target_rounds[roundInfo.target].length;

        float perception_rad = radius - 50 * density;
        // Select current arrow
        if (xDiff * xDiff + yDiff * yDiff > perception_rad * perception_rad) {
            double degree1 = Math.toDegrees(Math.atan2(-yDiff, xDiff)) - (180.0 / (double) rings);
            if (degree1 < 0)
                degree1 += 360.0;
            zone = (int) ((rings + 1) * ((360.0 - degree1) / 360.0));
        }

        if (zone == -2)
            return true;

        // Correct points_zone
        if (zone >= rings)
            zone = -1;

        // Make 3er Spot 9 appear as one
        if (zone == 1 && roundInfo.target == 3 && roundInfo.compound) {
            zone = 2;
        }

        // If a valid selection was made save it in the passe
        if (currentArrow < roundInfo.ppp && mPasse.shot[currentArrow].zone != zone) {
            mPasse.shot[currentArrow].zone = zone;
            mPasse.shot[currentArrow].x = Target.zoneToX(roundInfo.target, zone);
            mPasse.shot[currentArrow].y = 0f;
            invalidate();
        }

        // If finger is released go to next shoot
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            //go to next page
            if (currentArrow == lastSetArrow + 1)
                lastSetArrow++;

            animateSelectCircle(lastSetArrow + 1);

            if (lastSetArrow + 1 >= roundInfo.ppp && setListener != null) {
                setListener.onTargetSet(new Passe(mPasse), false);
            }

            return true;
        }
        return true;
    }

    private void animateSelectCircle(final int i) {
        mCurSelecting = i;
        mCurAnimationProgress = 0;
        initAnimationPositions();

        final ValueAnimator moveAnimator = ValueAnimator.ofFloat(0, 1);
        moveAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        moveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCurAnimationProgress = (Float) valueAnimator.getAnimatedValue();
                if (mCurAnimationProgress == 1.0f) {
                    moveAnimator.cancel();
                    currentArrow = i;
                    mCurSelecting = -1;
                }
                invalidate();
            }
        });
        moveAnimator.setDuration(300);
        moveAnimator.start();
    }

    public void setOnTargetSetListener(OnTargetSetListener listener) {
        setListener = listener;
    }

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
}
