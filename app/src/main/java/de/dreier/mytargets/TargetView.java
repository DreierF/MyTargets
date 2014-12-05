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

import de.dreier.mytargets.TargetOpenHelper.Passe;

public class TargetView extends View implements View.OnTouchListener {

    private int radius, midX, midY;
    private int contentWidth, contentHeight;

    private TextPaint mTextPaint;
    private Paint thinBlackBorder, thinWhiteBorder, drawColorP, rectColorP, circleColorP;

    private int currentArrow = 0, lastSetArrow = -1;

    private OnTargetSetListener setListener = null;
    private float density;
    private float resultX1, resultX2, spacePerResult;
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
    private int oldRadius;
    private float oldSpacePerResult, oldResultX1;
    private TargetOpenHelper.Round roundInfo;

    public void reset() {
        currentArrow = 0;
        lastSetArrow = -1;
        mCurSelecting = -1;
        for (int i = 0; i < roundInfo.ppp; i++) {
            mPasse.zones[i] = -2;
        }
        invalidate();
    }

    public void setRoundInfo(TargetOpenHelper.Round r) {
        roundInfo = r;
        mZoneCount = Target.target_rounds[r.target].length;
        mPasse = new Passe(r.ppp);
        reset();
    }

    public void setZones(Passe passe) {
        currentArrow = passe.zones.length;
        lastSetArrow = passe.zones.length;
        mPasse = passe;
        invalidate();
    }

    public void switchMode(boolean mode, boolean animate) {
        if (mode != mModeEasy) {
            mModeEasy = mode;
            if (animate)
                animateMode();
        }
    }

    public interface OnTargetSetListener {
        public void OnTargetSet(Passe passe);
    }

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

        rectColorP = new Paint();
        rectColorP.setAntiAlias(true);

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

        // Draw target with highlighted zone
        if (mCurSelecting == -2) {
            drawTarget(canvas, (int) (mOutFromX + (midX - mOutFromX) * mCurAnimationProgress),
                    (int) (mOutFromY + (midY - mOutFromY) * mCurAnimationProgress),
                    (int) (oldRadius + (radius - oldRadius) * mCurAnimationProgress));
        } else {
            drawTarget(canvas, midX, midY, radius);
        }

        // Draw right indicator
        drawRightSelectorBar(canvas);

        // Draw zoomed in target
        if (!mModeEasy && curZone >= -1) {
            canvas.save(Canvas.CLIP_SAVE_FLAG);
            float px = mPasse.points[currentArrow][0];
            float py = mPasse.points[currentArrow][1];
            int append = (px < 0 && py < 0) ? 1 : 0;
            canvas.clipRect(midX - radius + append * radius, 0,
                    midX - radius + radius * (append + 1), midY, Region.Op.REPLACE);
            int x = (int) (midX + radius * (append - 2 * px - 0.5));
            int y = (int) (midY + radius * (-2 * py - 0.5));
            drawTarget(canvas, x, y, radius * 2);
            canvas.restore();
        }

        // Draw selection for currentArrow
        if (curZone >= -1 && mCurSelecting == -1 && mModeEasy) {
            float circleY;
            if (curZone == -1) {
                circleY = midY + radius;
            } else {
                circleY = midY + radius * (curZone + 1) / (float) mZoneCount;
            }
            drawCircle(canvas, midX + radius + 27 * density, circleY, curZone);
            if (curZone > -1)
                canvas.drawLine(midX, circleY, midX + radius + 10 * density, circleY, circleColorP);
        }

        // Draw touch feedback if arrow is pressed
        if (mCurPressed != -1) {
            canvas.drawRect(midX + spacePerResult * mCurPressed + resultX1, midY + radius * 1.3f - 20 * density,
                    spacePerResult * (mCurPressed + 1) + resultX1, midY + radius * 1.3f + 20 * density, grayBackground);
        }

        // Draw all points of this passe at the bottom
        for (int i = 0; i <= lastSetArrow && i < roundInfo.ppp; i++) {
            float newX = spacePerResult * i + resultX1 + (spacePerResult / 2.0f);
            float newY = midY + radius * 1.3f;
            if (currentArrow != i && mCurSelecting != i) {
                if (mCurSelecting == -2) {
                    float oldX = oldSpacePerResult * i + oldResultX1 + (oldSpacePerResult / 2.0f);
                    float oldY = mOutFromY + oldRadius * 1.3f;
                    drawCircle(canvas, oldX + (newX - oldX) * mCurAnimationProgress,
                            oldY + (newY - oldY) * mCurAnimationProgress, mPasse.zones[i]);
                } else {
                    drawCircle(canvas, newX, newY, mPasse.zones[i]);
                }
            }
        }

        // Draw animation
        if (mCurSelecting > -1) {
            // Draw outgoing object
            if (mOutZone >= -1) {
                drawCircle(canvas, mOutFromX + (mOutToX - mOutFromX) * mCurAnimationProgress, mOutFromY + (mOutToY - mOutFromY) * mCurAnimationProgress, mOutZone);
                if (mOutZone > -1 && mModeEasy) {
                    int colorInd = mOutZone > -1 ? Target.target_rounds[roundInfo.target][mOutZone] : 3;
                    int color = Target.circleStrokeColor[colorInd];
                    circleColorP.setColor(animateColor(color, color & 0xFFFFFF));
                    canvas.drawLine(midX, mOutFromY, midX + radius + 10 * density, mOutFromY, circleColorP);
                }
            }
            // Draw incoming object
            if (mInZone >= -1) {
                drawCircle(canvas, mInFromX + (mInToX - mInFromX) * mCurAnimationProgress, mInFromY + (mInToY - mInFromY) * mCurAnimationProgress, mInZone);
                if (mInZone > -1 && mModeEasy) {
                    int colorInd = mInZone > -1 ? Target.target_rounds[roundInfo.target][mInZone] : 3;
                    int color = Target.circleStrokeColor[colorInd];
                    circleColorP.setColor(animateColor(color & 0xFFFFFF, color));
                    canvas.drawLine(midX, mInToY, midX + radius + 10 * density, mInToY, circleColorP);
                }
            }
        }
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
        if (!mModeEasy) {
            float count = 0;
            float sumX = 0, sumY = 0;
            for (int i = 0; i <= lastSetArrow + 1 && i < roundInfo.ppp && mPasse.zones[i] != -2; i++) {

                // For yellow and white background use black font color
                int colorInd = i == mZoneCount || mPasse.zones[i] < 0 ? 0 : target[mPasse.zones[i]];
                drawColorP.setColor(colorInd == 0 || colorInd == 4 ? Color.BLACK : Color.WHITE);
                float selX = mPasse.points[i][0];
                float selY = mPasse.points[i][1];
                if (i != currentArrow) {
                    sumX += selX;
                    sumY += selY;
                    count++;
                }

                // Draw arrow position
                float xp = x + selX * radius;
                float yp = y + selY * radius;
                if (i == currentArrow) { // As + if it is currently selected
                    drawColorP.setStrokeWidth(density);
                    canvas.drawLine(xp, yp - 4 * density, xp, yp + 4 * density, drawColorP);
                    canvas.drawLine(xp - 4 * density, yp, xp + 4 * density, yp, drawColorP);
                    drawColorP.setStrokeWidth(0);
                } else { // otherwise as dot
                    canvas.drawCircle(xp, yp, 3 * density, drawColorP);
                }
            }
            if (lastSetArrow != -1 && count >= 2) {
                drawColorP.setColor(Color.RED);
                canvas.drawCircle(x + (sumX / count) * radius, y + (sumY / count) * radius, 3 * density, drawColorP);
            }
        }
    }

    private void drawRightSelectorBar(Canvas canvas) {
        if (mModeEasy || mCurSelecting == -2) {
            for (int i = 0; i <= mZoneCount; i++) {
                float perc = 1;
                if (mCurSelecting == -2) {
                    perc = mModeEasy ? mCurAnimationProgress : 1 - mCurAnimationProgress;
                }
                int X1 = (int) (contentWidth - 60 * perc * density);
                int X2 = (int) (X1 + 40 * density);
                int Y1 = contentHeight * i / (mZoneCount + 1);
                int Y2 = contentHeight * (i + 1) / (mZoneCount + 1);

                int colorInd = 0;
                // For all rectangles except mistake draw background
                if (i != mZoneCount) {
                    colorInd = target[i];
                    rectColorP.setColor(Target.rectColor[colorInd]);
                    canvas.drawRect(X1, Y1, X2, Y2, rectColorP);
                    canvas.drawRect(X1, Y1, X2, Y2, Target.target_rounds[roundInfo.target][i] == 3 ? thinWhiteBorder : thinBlackBorder);
                } else {
                    canvas.drawRect(X1, Y1, X2, Y2, thinBlackBorder);
                }

                // For yellow and white background use black font color
                mTextPaint.setColor(colorInd == 0 || colorInd == 4 ? Color.BLACK : Color.WHITE);
                canvas.drawText(Target.getStringByZone(roundInfo.target, i, roundInfo.compound), X1 + (X2 - X1) / 2, Y1 + (Y2 - Y1) / 2 + 10 * density, mTextPaint);
            }
        }
    }

    private void initAnimationPositions() {
        //Calculate positions of outgoing object
        if (currentArrow < roundInfo.ppp && mPasse.zones[currentArrow] >= -1) {
            mOutZone = mPasse.zones[currentArrow];
            mOutToX = spacePerResult * currentArrow + resultX1 + (spacePerResult / 2.0f);
            mOutToY = midY + radius * 1.3f;
            if (mModeEasy) {
                mOutFromX = midX + radius + 27 * density;
                if (mOutZone == -1) {
                    mOutFromY = midY + radius;
                } else {
                    mOutFromY = midY + radius * (mOutZone + 1) / (float) mZoneCount;
                }
            } else {
                mOutFromX = midX + radius * mPasse.points[currentArrow][0];
                mOutFromY = midY + radius * mPasse.points[currentArrow][1];
            }
        } else {
            mOutZone = -2;
        }

        // Calculate positions for incoming object
        if (mCurSelecting < roundInfo.ppp && mPasse.zones[mCurSelecting] >= -1) {
            mInZone = mPasse.zones[mCurSelecting];
            mInFromX = spacePerResult * mCurSelecting + resultX1 + (spacePerResult / 2.0f);
            mInFromY = midY + radius * 1.3f;
            if (mModeEasy) {
                mInToX = midX + radius + 27 * density;
                if (mInZone == -1) {
                    mInToY = midY + radius;
                } else {
                    mInToY = midY + radius * (mInZone + 1) / (float) mZoneCount;
                }
            } else {
                mInToX = midX + radius * mPasse.points[mCurSelecting][0];
                mInToY = midY + radius * mPasse.points[mCurSelecting][1];
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

        if ((mCurSelecting > -1 && zone == mInZone && mModeEasy) || (!mModeEasy && mCurSelecting == -2)) {
            return animateColor(Target.grayColor[target[zone]], Target.highlightColor[target[zone]]);
        } else if ((mCurSelecting > -1 && zone == mOutZone && mModeEasy) || (mModeEasy && mCurSelecting == -2)) {
            return animateColor(Target.highlightColor[target[zone]], Target.grayColor[target[zone]]);
        } else {
            return (zone == curZone || !mModeEasy ? Target.highlightColor : Target.grayColor)[target[zone]];
        }
    }

    public int animateColor(int from, int to) {
        final int fa = (from >> 24) & 0xFF;
        final int fr = (from >> 16) & 0xFF;
        final int fg = (from >> 8) & 0xFF;
        final int fb = (from) & 0xFF;
        final int da = ((to >> 24) & 0xFF) - fa;
        final int dr = ((to >> 16) & 0xFF) - fr;
        final int dg = ((to >> 8) & 0xFF) - fg;
        final int db = ((to) & 0xFF) - fb;
        final int ra = (int) (fa + da * mCurAnimationProgress);
        final int rr = (int) (fr + dr * mCurAnimationProgress);
        final int rg = (int) (fg + dg * mCurAnimationProgress);
        final int rb = (int) (fb + db * mCurAnimationProgress);
        return (ra << 24) | (rr << 16) | (rg << 8) | rb;
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
        roundInfo = (TargetOpenHelper.Round) b.getSerializable("roundInfo");
    }

    private void drawCircle(Canvas can, float x, float y, int zone) {
        int colorInd;
        if (zone > -1) {
            colorInd = Target.target_rounds[roundInfo.target][zone];
        } else {
            colorInd = 3;
        }
        circleColorP.setStyle(Paint.Style.FILL_AND_STROKE);
        circleColorP.setColor(Target.rectColor[colorInd]);
        can.drawCircle(x, y, 17 * density, circleColorP);
        circleColorP.setStyle(Paint.Style.STROKE);
        circleColorP.setColor(Target.circleStrokeColor[colorInd]);
        can.drawCircle(x, y, 17 * density, circleColorP);
        mTextPaint.setColor(colorInd == 0 || colorInd == 4 ? Color.BLACK : Color.WHITE);
        can.drawText(Target.getStringByZone(roundInfo.target, zone, roundInfo.compound), x, y + 7 * density, mTextPaint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        contentWidth = getWidth();
        contentHeight = getHeight();

        calcSizes();
    }

    private void calcSizes() {
        float radH = (contentHeight - 10 * density) / 2.45f;
        float radW = (contentWidth - (mModeEasy ? 70 : 20) * density) * (mModeEasy ? 1 : 0.5f);
        radius = (int) (Math.min(radW, radH));
        midX = mModeEasy ? 0 : contentWidth / 2;
        midY = radius + (int) (10 * density);
        resultX1 = 30 * density;
        resultX2 = contentWidth - (mModeEasy ? 80 : 30) * density;
        spacePerResult = (resultX2 - resultX1) / roundInfo.ppp;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();

        if (mCurSelecting != -1)
            return true;

        // Handle selection of already saved shoots
        if (x > resultX1 && x < resultX2 && y > midY + radius * 1.3f - 20 * density && y < midY + radius * 1.3f + 20 * density) {
            int arrow = (int) ((x - resultX1) / spacePerResult);
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

        int zone, ringe = Target.target_rounds[roundInfo.target].length;
        // Handle selection via right indicator bar
        if (x > midX + radius + 30 * density && mModeEasy) {
            zone = (int) (y * (ringe + 1) / (float) contentHeight);
        } else { // Handle via target
            double xDiff = x - midX;
            double yDiff = y - midY;
            zone = (int) (Math.sqrt(xDiff * xDiff + yDiff * yDiff) * ringe / (float) radius);
        }

        // Correct points_zone
        if (zone < -1 || zone >= ringe)
            zone = -1;

        // If a valid selection was made save it in the passe
        if (currentArrow < roundInfo.ppp && (mPasse.zones[currentArrow] != zone || !mModeEasy)) {
            mPasse.zones[currentArrow] = zone;
            mPasse.points[currentArrow][0] = (x - midX) / radius;
            mPasse.points[currentArrow][1] = (y - midY) / radius;
            invalidate();
        }

        // If finger is released go to next shoot
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            //go to next page
            if (currentArrow == lastSetArrow + 1)
                lastSetArrow++;

            animateSelectCircle(lastSetArrow + 1);

            if (lastSetArrow + 1 >= roundInfo.ppp && setListener != null) {
                setListener.OnTargetSet(new Passe(mPasse));
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

    private void animateMode() {
        mCurSelecting = -2;
        mCurAnimationProgress = 0;
        oldRadius = radius;
        oldResultX1 = resultX1;
        oldSpacePerResult = spacePerResult;
        mOutFromX = midX;
        mOutFromY = midY;
        calcSizes();

        final ValueAnimator moveAnimator = ValueAnimator.ofFloat(0, 1);
        moveAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        moveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCurAnimationProgress = (Float) valueAnimator.getAnimatedValue();
                if (mCurAnimationProgress == 1.0f) {
                    moveAnimator.cancel();
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
}
