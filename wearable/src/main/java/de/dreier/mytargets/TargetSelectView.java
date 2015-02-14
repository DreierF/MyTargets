package de.dreier.mytargets;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Region;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import de.dreier.mytargets.models.OnTargetSetListener;
import de.dreier.mytargets.models.Passe;
import de.dreier.mytargets.models.Round;
import de.dreier.mytargets.models.Target;

public class TargetSelectView extends View implements View.OnTouchListener {

    private int contentWidth, contentHeight;
    private int radius;

    private TextPaint mTextPaint;
    private Paint thinBlackBorder, thinWhiteBorder, drawColorP, circleColorP;

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
    private int mCurPressed = -1;
    private Paint grayBackground;
    private boolean mModeEasy = true;
    private int[] target;
    private Passe mPasse;
    private Round roundInfo;
    private int chinHeight;
    private double circRadius;
    private boolean twoRows;

    public void reset() {
        currentArrow = 0;
        lastSetArrow = -1;
        mCurSelecting = -1;
        for (int i = 0; i < roundInfo.ppp; i++) {
            mPasse.zones[i] = -2;
        }
        invalidate();
    }

    public void setRoundInfo(Round r) {
        roundInfo = r;
        mZoneCount = Target.target_rounds[r.target].length;
        mPasse = new Passe(r.ppp);
        twoRows = roundInfo.ppp > 3;
        reset();
    }

    public void setMode(boolean mode) {
        if (mode != mModeEasy) {
            mModeEasy = mode;
        }
    }

    public void setChinHeight(int chinHeight) {
        this.chinHeight = chinHeight;
    }

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

    private void init() {
        // Set up a default TextPaint object
        density = getResources().getDisplayMetrics().density;
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(22 * density);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        thinBlackBorder = new Paint();
        thinBlackBorder.setColor(0xFF1C1C1B);
        thinBlackBorder.setAntiAlias(true);
        thinBlackBorder.setStyle(Paint.Style.STROKE);

        thinWhiteBorder = new Paint();
        thinWhiteBorder.setColor(0xFFEEEEEE);
        thinWhiteBorder.setAntiAlias(true);
        thinWhiteBorder.setStyle(Paint.Style.STROKE);

        drawColorP = new Paint();
        drawColorP.setAntiAlias(true);

        grayBackground = new Paint();
        grayBackground.setColor(0xFFDDDDDD);
        grayBackground.setAntiAlias(true);

        circleColorP = new Paint();
        circleColorP.setAntiAlias(true);
        circleColorP.setStrokeWidth(2 * density);

        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        target = Target.target_rounds[roundInfo.target];
        int curZone;
        if (currentArrow < roundInfo.ppp)
            curZone = mPasse.zones[currentArrow];
        else
            curZone = -2;

        if (mModeEasy) {
            drawEasyMode(canvas);
        } else {
            // Draw target
            drawTarget(canvas, radius, radius, radius);

            // Draw zoomed in target
            if (!mModeEasy && curZone >= -1) {
                canvas.save(Canvas.CLIP_SAVE_FLAG);
                float px = mPasse.points[currentArrow][0];
                float py = mPasse.points[currentArrow][1];
                int append = (px < 0 && py < 0) ? 1 : 0;
                canvas.clipRect(append * radius, 0,
                        radius * (append + 1), radius, Region.Op.REPLACE);
                int x = (int) (radius + radius * (append - 2 * px - 0.5));
                int y = (int) (radius + radius * (-2 * py - 0.5));
                drawTarget(canvas, x, y, radius * 2);
                canvas.restore();
            }

            // Draw selection for currentArrow
            if (curZone >= -1 && mCurSelecting == -1 && mModeEasy) {
                float circleY;
                if (curZone == -1) {
                    circleY = radius + radius;
                } else {
                    circleY = radius + radius * (curZone + 1) / (float) mZoneCount;
                }
                drawCircle(canvas, radius + radius + 27 * density, circleY, curZone, 17, 22);
                if (curZone > -1)
                    canvas.drawLine(radius, circleY, radius + radius + 10 * density, circleY, circleColorP);
            }
        }

        // Draw animation
        if (mCurSelecting > -1) {
            // Draw outgoing object
            if (mOutZone >= -1) {
                drawCircle(canvas, mOutFromX + (mOutToX - mOutFromX) * mCurAnimationProgress,
                        mOutFromY + (mOutToY - mOutFromY) * mCurAnimationProgress, mOutZone,
                        (int) (17 - (17 - 10) * mCurAnimationProgress),
                        (int) (22 - (22 - 13) * mCurAnimationProgress));
            }
            // Draw incoming object
            if (mInZone >= -1) {
                drawCircle(canvas, mInFromX + (mInToX - mInFromX) * mCurAnimationProgress,
                        mInFromY + (mInToY - mInFromY) * mCurAnimationProgress, mInZone,
                        (int) (10 + (17 - 10) * mCurAnimationProgress),
                        (int) (22 - (22 - 13) * mCurAnimationProgress));
            }
        }
    }

    class Midpoint {
        float count = 0;
        float sumX = 0;
        float sumY = 0;
    }

    private void drawEasyMode(Canvas canvas) {
        int curZone;
        if (currentArrow < roundInfo.ppp)
            curZone = mPasse.zones[currentArrow];
        else
            curZone = -2;

        // Erase background
        drawColorP.setColor(0xffffffff);
        canvas.drawRect(0, 0, contentWidth, contentHeight, drawColorP);

        // Draw all possible points in a circular
        for (int i = -1; i < mZoneCount; i++) {
            float[] coord = getCircularCoords(i);
            drawCircle(canvas, coord[0], coord[1], i,
                    i == curZone ? 23 : 17,
                    i == curZone ? 29 : 22);
        }

        // Draw all points of this passe in the center
        drawSelectedPointsInCenter(canvas);
    }

    private void drawSelectedPointsInCenter(Canvas canvas) {
        // Draw touch feedback if arrow is pressed
        if (mCurPressed != -1) {
            float x = radius + (mCurPressed - 1) * 25 * density;
            float y = radius + (mCurPressed < 3 ? -1 : 1) * (twoRows ? 30 * density : 20 * density);
            canvas.drawRect(x, y, x + 25 * density, y + 25 * density, grayBackground);
        }

        // Draw separator line if there are more then 3 shots
        if (twoRows) {
            canvas.drawLine(radius - 30 * density,
                    radius, radius + 30 * density, radius, thinBlackBorder);
        }

        // Draw the points
        for (int i = 0; i <= lastSetArrow && i < roundInfo.ppp; i++) {
            float newX = radius + ((i % 3) - 1) * 25 * density;
            float newY = radius + (i < 3 ? -1 : 1) * (twoRows ? 30 * density : 20 * density);
            if (currentArrow != i && mCurSelecting != i) {
                drawCircle(canvas, newX, newY, mPasse.zones[i], 10, 13);
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

    private void drawTarget(Canvas canvas, int x, int y, int radius) {
        drawColorP.setColor(0xffeeeeee);
        canvas.drawRect(0, 0, contentWidth, contentHeight, drawColorP);
        for (int i = mZoneCount; i > 0; i--) {
            // Select colors to draw with
            drawColorP.setColor(getZoneColor(i - 1));

            // Draw a ring mit separator line
            float rad = (radius * i) / (float) mZoneCount;
            canvas.drawCircle(x, y, rad, drawColorP);
            canvas.drawCircle(x, y, rad, Target.target_rounds[roundInfo.target][i - 1] == 3 ? thinWhiteBorder : thinBlackBorder);
        }

        // Draw exact arrow position
        Midpoint m = new Midpoint();
        drawPasseShots(canvas, x, y, radius, m, mPasse, false);

        if (m.count >= 2) {
            drawColorP.setColor(Color.RED);
            canvas.drawCircle(x + (m.sumX / m.count) * radius, y + (m.sumY / m.count) * radius, 3 * density, drawColorP);
        }
    }

    private void drawPasseShots(Canvas canvas, int x, int y, int radius, Midpoint m, Passe p, boolean old) {
        for (int i = 0; !old ? (i <= lastSetArrow + 1 && i < roundInfo.ppp && p.zones[i] != -2) : i < p.points.length; i++) {

            // For yellow and white background use black font color
            int colorInd = i == mZoneCount || p.zones[i] < 0 ? 0 : target[p.zones[i]];
            drawColorP.setColor(colorInd == 0 || colorInd == 4 ? Color.BLACK : Color.WHITE);
            float selX = p.points[i][0];
            float selY = p.points[i][1];
            if (i != currentArrow || old) {
                m.sumX += selX;
                m.sumY += selY;
                m.count++;
            }

            // Draw arrow position
            float xp = x + selX * radius;
            float yp = y + selY * radius;
            if (i == currentArrow && !old) { // As + if it is currently selected
                drawColorP.setStrokeWidth(density);
                canvas.drawLine(xp, yp - 4 * density, xp, yp + 4 * density, drawColorP);
                canvas.drawLine(xp - 4 * density, yp, xp + 4 * density, yp, drawColorP);
                drawColorP.setStrokeWidth(0);
            } else { // otherwise as dot
                canvas.drawCircle(xp, yp, 3 * density, drawColorP);
            }
        }
    }

    private void initAnimationPositions() {
        //Calculate positions of outgoing object
        if (currentArrow < roundInfo.ppp && mPasse.zones[currentArrow] >= -1) {
            mOutZone = mPasse.zones[currentArrow];
            mOutToX = radius + ((currentArrow % 3) - 1) * 25 * density;
            mOutToY = radius + (currentArrow < 3 ? -1 : 1) * (twoRows ? 30 * density : 20 * density);
            if (mModeEasy) {
                float[] coord = getCircularCoords(mOutZone);
                mOutFromX = coord[0];
                mOutFromY = coord[1];
            } else {
                mOutFromX = radius + radius * mPasse.points[currentArrow][0];
                mOutFromY = radius + radius * mPasse.points[currentArrow][1];
            }
        } else {
            mOutZone = -2;
        }

        // Calculate positions for incoming object
        if (mCurSelecting < roundInfo.ppp && mPasse.zones[mCurSelecting] >= -1) {
            mInZone = mPasse.zones[mCurSelecting];
            float[] coord = getCircularCoords(mInZone);
            mInFromX = coord[0];
            mInFromY = coord[1];
            if (mModeEasy) {
                mInToX = radius + ((mCurSelecting % 3) - 1) * 25 * density;
                mInToY = radius + (mCurSelecting < 3 ? -1 : 1) * (twoRows ? 30 * density : 20 * density);
            } else {
                mInToX = radius + radius * mPasse.points[mCurSelecting][0];
                mInToY = radius + radius * mPasse.points[mCurSelecting][1];
            }
        } else {
            mInZone = -2;
        }
    }

    private int getZoneColor(int zone) {
        final int[] target = Target.target_rounds[roundInfo.target];
        final int curZone;
        if (currentArrow < roundInfo.ppp)
            curZone = mPasse.zones[currentArrow];
        else
            curZone = -2;

        return (zone == curZone || !mModeEasy ? Target.highlightColor : Target.grayColor)[target[zone]];
    }

    private void drawCircle(Canvas can, float x, float y, int zone, int rad, int font_size) {
        int colorInd;
        if (zone > -1) {
            colorInd = Target.target_rounds[roundInfo.target][zone];
        } else {
            colorInd = 3;
        }
        circleColorP.setStyle(Paint.Style.FILL_AND_STROKE);
        circleColorP.setColor(Target.rectColor[colorInd]);
        can.drawCircle(x, y, rad * density, circleColorP);
        circleColorP.setStyle(Paint.Style.STROKE);
        circleColorP.setColor(Target.circleStrokeColor[colorInd]);
        can.drawCircle(x, y, rad * density, circleColorP);
        mTextPaint.setColor(colorInd == 0 || colorInd == 4 ? Color.BLACK : Color.WHITE);
        mTextPaint.setTextSize(font_size * density);
        can.drawText(Target.getStringByZone(roundInfo.target, zone, roundInfo.compound), x, y + 7 * density, mTextPaint);
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

        if (mModeEasy) {
            float perception_rad = radius - 40 * density;
            // Select current arrow
            if (xDiff * xDiff + yDiff * yDiff > perception_rad * perception_rad) {
                double degree1 = Math.toDegrees(Math.atan2(-yDiff, xDiff)) - (180.0 / (double) rings);
                if (degree1 < 0)
                    degree1 += 360.0;
                zone = (int) ((rings + 1) * ((360.0 - degree1) / 360.0));
            }

            // Handle selection of already saved shoots
            if (x > radius - 25 * density && x < radius + 25 * density &&
                    y > radius - (twoRows ? 30 * density : 20 * density) &&
                    y < radius + (twoRows ? 30 * density : 0)) {
                int arrow = (int) ((x - radius - 25 * density) / 25 * density);
                if (arrow < roundInfo.ppp && mPasse.zones[arrow] >= -1 && arrow != currentArrow) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        mCurPressed = -1;
                        animateSelectCircle(arrow);
                    } else {
                        mCurPressed = arrow;
                    }
                    invalidate();
                    return true;
                }
            }
        } else {
            zone = (int) (Math.sqrt(xDiff * xDiff + yDiff * yDiff) * rings / (float) radius);
        }

        if (zone == -2)
            return true;

        // Correct points_zone
        if (zone >= rings)
            zone = -1;

        // If a valid selection was made save it in the passe
        if (currentArrow < roundInfo.ppp && mPasse.zones[currentArrow] != zone) {
            mPasse.zones[currentArrow] = zone;
            mPasse.points[currentArrow][0] = (float) (xDiff / radius);
            mPasse.points[currentArrow][1] = (float) (yDiff / radius);
            invalidate();
        }

        // If finger is released go to next shoot
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            //go to next page
            if (currentArrow == lastSetArrow + 1)
                lastSetArrow++;

            animateSelectCircle(lastSetArrow + 1);

            if (lastSetArrow + 1 >= roundInfo.ppp && setListener != null) {
                setListener.onTargetSet(new Passe(mPasse));
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
