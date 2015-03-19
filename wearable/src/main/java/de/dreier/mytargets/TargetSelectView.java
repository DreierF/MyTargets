package de.dreier.mytargets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;

import de.dreier.mytargets.models.Shot;
import de.dreier.mytargets.models.Target;
import de.dreier.mytargets.views.TargetViewBase;

public class TargetSelectView extends TargetViewBase {

    private int radius;

    private Paint drawColorP;

    private float mInFromX, mInFromY;
    private float mInToX, mInToY;
    private float mOutFromX, mOutFromY;
    private float mOutToX, mOutToY;
    private int mInZone, mOutZone;
    private int chinHeight;
    private double circRadius;
    private boolean twoRows;

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
        if (currentArrow < roundInfo.ppp) {
            curZone = mPasse.shot[currentArrow].zone;
        } else {
            curZone = -2;
        }

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
        twoRows = roundInfo.ppp > 3;
        for (int i = 0; i <= lastSetArrow && i < roundInfo.ppp; i++) {
            float newX = radius + ((i % 3) - 1) * 25 * density;
            float newY = radius - 25 * density +
                    (i < 3 ? -1 : 1) * (twoRows ? 15 * density : -5 * density);
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

    @Override
    protected void initAnimationPositions() {
        twoRows = roundInfo.ppp > 3;

        //Calculate positions of outgoing object
        if (currentArrow < roundInfo.ppp && mPasse.shot[currentArrow].zone >= -1) {
            mOutZone = mPasse.shot[currentArrow].zone;
            mOutToX = radius + ((currentArrow % 3) - 1) * 25 * density;
            mOutToY = radius - 25 * density +
                    (currentArrow < 3 ? -1 : 1) * (twoRows ? 15 * density : -5 * density);
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
            mInToY = radius - 25 * density +
                    (mCurSelecting < 3 ? -1 : 1) * (twoRows ? 15 * density : -5 * density);
        } else {
            mInZone = -2;
        }
    }

    @Override
    protected void calcSizes() {
        radius = (int) (contentWidth / 2.0);
        circRadius = radius - 25 * density;
    }

    @Override
    protected Shot getShotFromPos(float x, float y) {
        int rings = Target.target_rounds[roundInfo.target].length;
        Shot s = new Shot();

        double xDiff = x - radius;
        double yDiff = y - radius;

        float perception_rad = radius - 50 * density;
        // Select current arrow
        if (xDiff * xDiff + yDiff * yDiff > perception_rad * perception_rad) {
            double degree1 = Math.toDegrees(Math.atan2(-yDiff, xDiff)) - (180.0 / (double) rings);
            if (degree1 < 0) {
                degree1 += 360.0;
            }
            s.zone = (int) ((rings + 1) * ((360.0 - degree1) / 360.0));
        }

        if (s.zone == Shot.NOTHING_SELECTED) {
            // When nothing is selected do nothing
            return null;
        } else if (s.zone >= rings) {
            // Correct points_zone
            s.zone = Shot.MISS;
        }
        s.x = Target.zoneToX(roundInfo.target, s.zone);
        s.y = 0f;
        return s;
    }

    @Override
    protected boolean selectPreviousShots(MotionEvent motionEvent, float x, float y) {
        return false;
    }
}
