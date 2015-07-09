/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.utils.PasseDrawer;
import de.dreier.mytargets.shared.views.TargetViewBase;
import de.dreier.mytargets.utils.TextInputDialog;

public class TargetView extends TargetViewBase {

    private static final float ZOOM_FACTOR = 2;
    private int radius, midX, midY;
    private TextPaint mTextPaint;
    private Paint thinBlackBorder, thinWhiteBorder, drawColorP, rectColorP, circleColorP;
    private boolean showAll = false;
    private ArrayList<Passe> mOldShots;
    private Timer longPressTimer;
    private Paint thinLine;
    private final Handler h = new Handler();
    private int oldRadius;
    private final RectF mZoomedRect = new RectF();
    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            final int pressed = mPasseDrawer.getPressed();
            if (pressed == -1) {
                return;
            }
            longPressTimer = null;
            Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
            animateSelectCircle(round.arrowsPerPasse);

            new TextInputDialog.Builder(getContext())
                    .setTitle(R.string.comment)
                    .setDefaultText(mPasse.shot[pressed].comment)
                    .setOnClickListener(new TextInputDialog.OnClickListener() {

                        @Override
                        public void onCancelClickListener() {
                            mPasseDrawer.setPressed(-1);
                            invalidate();
                        }

                        @Override
                        public void onOkClickListener(String input) {
                            mPasse.shot[pressed].comment = input;
                            if (lastSetArrow + 1 >= round.arrowsPerPasse && setListener != null) {
                                setListener.onTargetSet(new Passe(mPasse), false);
                            }
                            mPasseDrawer.setPressed(-1);
                            invalidate();
                        }
                    }).show();
        }
    };

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

    public boolean hasPointsSet() {
        return mPasse.shot[0].zone != -2;
    }

    public void setPasse(Passe passe) {
        currentArrow = passe.shot.length;
        lastSetArrow = passe.shot.length;
        mPasse = passe;
        mPasseDrawer.setSelection(currentArrow, null, PasseDrawer.MAX_CIRCLE_SIZE);
        mPasseDrawer.setPasse(passe);
        invalidate();
    }

    public void switchMode(boolean mode, boolean animate) {
        if (mode != mModeEasy) {
            mModeEasy = mode;
            if (animate) {
                animateMode();
            }
        }
    }

    public void showAll(boolean showAll) {
        this.showAll = showAll;
        invalidate();
    }

    public void setOldShoots(ArrayList<Passe> oldOnes) {
        mOldShots = oldOnes;
        invalidate();
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

        circleColorP = new Paint();
        circleColorP.setAntiAlias(true);
        circleColorP.setStrokeWidth(2 * density);

        thinLine = new Paint();
        thinLine.setAntiAlias(true);
        thinLine.setStyle(Paint.Style.STROKE);

        if (isInEditMode()) {
            round = new RoundTemplate();
            round.arrowsPerPasse = 3;
            mPasse = new Passe(3);
            mPasseDrawer.setPasse(mPasse);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int curZone;
        if (currentArrow > -1 && currentArrow < round.arrowsPerPasse) {
            curZone = mPasse.shot[currentArrow].zone;
        } else {
            curZone = -2;
        }

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
            drawZoomedInTarget(canvas);
        }

        // Draw selection for currentArrow
        if (curZone >= -1 && !mPasseDrawer.isAnimating() && mCurSelecting == -1 && mModeEasy) {
            float circleY;
            if (curZone == -1) {
                circleY = midY + radius;
            } else {
                circleY = midY + radius * (curZone + 1) / (float) mZoneCount;
            }

            if (curZone > -1) {
                circleColorP.setColor(round.target.getZoneColor(curZone));
                canvas.drawLine(midX, circleY, midX + radius + 10 * density, circleY, circleColorP);
            }
        }

        // Draw all points of this passe at the bottom
        mPasseDrawer.draw(canvas);
    }

    private void drawZoomedInTarget(Canvas canvas) {
        canvas.save(Canvas.CLIP_SAVE_FLAG);
        float px = mPasse.shot[currentArrow].x;
        float py = mPasse.shot[currentArrow].y;
        calcZoomedRect(px, py);
        canvas.clipRect(mZoomedRect, Region.Op.REPLACE);
        int x = (int) (mZoomedRect.left - radius * (ZOOM_FACTOR * px - 0.5));
        int y = (int) (mZoomedRect.top - radius * (ZOOM_FACTOR * py - 0.5));
        drawTarget(canvas, x, y, (int) (radius * ZOOM_FACTOR));
        canvas.restore();
    }

    private void drawTarget(Canvas canvas, int x, int y, int radius) {
        // Erase background
        drawColorP.setColor(0xffeeeeee);
        canvas.drawRect(0, 0, contentWidth, contentHeight, drawColorP);

        // Draw actual target face
        round.target.setBounds(x - radius, y - radius, x + radius, y + radius);
        round.target.draw(canvas);

        // Draw exact arrow position
        if (!mModeEasy) {
            Midpoint m = new Midpoint();
            drawPasseShots(canvas, x, y, radius, m, mPasse, false);

            if (showAll) {
                for (Passe p : mOldShots) {
                    if (p.getId() != mPasse.getId()) {
                        drawPasseShots(canvas, x, y, radius, m, p, true);
                    }
                }
            }

            if (m.count >= 2) {
                drawColorP.setColor(Color.RED);
                canvas.drawCircle(x + (m.sumX / m.count) * radius, y + (m.sumY / m.count) * radius,
                        3 * density, drawColorP);
            }
        }
    }

    /**
     * Draws all shots of a passe as dots onto the target.
     * The currently selected shot is drawn as a cross.
     *
     * @param canvas Canvas to draw on
     * @param x      X-coordinate of the middle of the target
     * @param y      Y-coordinate of the middle of the target
     * @param radius The targets radius in pixels
     * @param m      Midpoint object where the information about this shot is added
     * @param p      The passe to be drawn
     * @param old    True if the provided passe is not the current one
     */
    private void drawPasseShots(Canvas canvas, int x, int y, int radius, Midpoint m, Passe p, boolean old) {
        for (int i = 0;
             !old ? (i <= lastSetArrow + 1 && i < round.arrowsPerPasse && p.shot[i].zone != -2) :
                     i < p.shot.length; i++) {

            // For yellow and white background use black font color
            int colorInd = i == mZoneCount || p.shot[i].zone < 0 ? 0 :
                    round.target.getZoneColor(p.shot[i].zone);
            drawColorP
                    .setColor(colorInd == 0 || colorInd == Color.WHITE ? Color.BLACK : Color.WHITE);
            float selX = p.shot[i].x;
            float selY = p.shot[i].y;
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

    /**
     * Draws a rect on the right that shows all possible points.
     *
     * @param canvas Canvas to draw on
     */
    private void drawRightSelectorBar(Canvas canvas) {
        if (mModeEasy || mCurSelecting == -2) {
            for (int i = 0; i <= mZoneCount; i++) {
                float percent = 1;
                if (mCurSelecting == -2) {
                    percent = mModeEasy ? mCurAnimationProgress : 1 - mCurAnimationProgress;
                }
                int X1 = (int) (contentWidth - 60 * percent * density);
                int X2 = (int) (X1 + 40 * density);
                int Y1 = contentHeight * i / (mZoneCount + 1);
                int Y2 = contentHeight * (i + 1) / (mZoneCount + 1);

                int colorInd = 0;
                // For all rectangles except mistake draw background
                if (i != mZoneCount) {
                    rectColorP.setColor(round.target.getZoneColor(i));
                    canvas.drawRect(X1, Y1, X2, Y2, rectColorP);
                    canvas.drawRect(X1, Y1, X2, Y2, round.target.getStrokeColor(i) == Color.BLACK ?
                            thinWhiteBorder : thinBlackBorder);
                } else {
                    canvas.drawRect(X1, Y1, X2, Y2, thinBlackBorder);
                }

                // For yellow and white background use black font color
                mTextPaint.setColor(
                        round.target.getZoneColor(i) == Color.BLACK ? Color.WHITE : Color.BLACK);
                canvas.drawText(round.target.zoneToString(i), X1 + (X2 - X1) / 2,
                        Y1 + (Y2 - Y1) / 2 + 10 * density, mTextPaint);
            }
        }
    }

    @Override
    protected Coordinate initAnimationPositions(int i) {
        Coordinate coordinate = new Coordinate();
        if (mModeEasy) {
            coordinate.x = midX + radius + 27 * density;
            if (mPasse.shot[i].zone == -1) {
                coordinate.y = midY + radius;
            } else {
                coordinate.y = midY + radius * (mPasse.shot[i].zone + 1) / (float) mZoneCount;
            }
        } else {
            coordinate.x = midX + radius * mPasse.shot[i].x;
            coordinate.y = midY + radius * mPasse.shot[i].y;
        }
        return coordinate;
    }

    private int getZoneColor(int zone) {
        /*final int curZone;
        if (currentArrow > -1 && currentArrow < round.arrowsPerPasse) {
            curZone = mPasse.shot[currentArrow].zone;
        } else {
            curZone = -2;
        }

        int gray = Target.grayColor[target[zone]];
        int highlight = Target.highlightColor[target[zone]];
        if (mPasseDrawer.isAnimating() && mModeEasy) {
            if (zone == mPasseDrawer.getSelectedZone()) {
                return BitmapUtils.animateColor(gray, highlight, mPasseDrawer.getProgress());
            } else if (zone == mPasseDrawer.getOldSelectedZone()) {
                return BitmapUtils.animateColor(highlight, gray, mPasseDrawer.getProgress());
            }
        } else if (mCurSelecting == -2) {
            if (mModeEasy) {
                return BitmapUtils.animateColor(highlight, gray, mCurAnimationProgress);
            } else {
                return BitmapUtils.animateColor(gray, highlight, mCurAnimationProgress);
            }
        }
        return zone == curZone || !mModeEasy ? highlight : gray;*/
        return round.target.getZoneColor(zone);
    }

    @Override
    public void saveState(Bundle b) {
        super.saveState(b);
        b.putSerializable("oldShots", mOldShots);
    }

    @Override
    public void restoreState(Bundle b) {
        super.restoreState(b);
        mOldShots = (ArrayList<Passe>) b.getSerializable("oldShots");
    }

    @Override
    protected void calcSizes() {
        float radH = (contentHeight - 10 * density) / 2.45f;
        float radW = (contentWidth - (mModeEasy ? 70 : 20) * density) * (mModeEasy ? 1 : 0.5f);
        radius = (int) (Math.min(radW, radH));
        midX = mModeEasy ? 0 : contentWidth / 2;
        midY = radius + (int) (10 * density);
        mZoomedRect.left = midX - radius;
        mZoomedRect.right = midX;
        mZoomedRect.top = midY - radius;
        mZoomedRect.bottom = midY;
        RectF rect = new RectF();
        rect.left = (mModeEasy ? 20 : 30) * density;
        rect.right = contentWidth - (mModeEasy ? 80 : 30) * density;
        rect.top = midY + radius;
        rect.bottom = contentHeight;
        mPasseDrawer.animateToRect(rect);
    }

    private void calcZoomedRect(float px, float py) {
        float x = midX + px * radius;
        float y = midY + py * radius;
        float inner = 1 / (float) mZoneCount;
        if (mZoomedRect.contains(x, y)) {
            boolean isLeftUpperRegion = px < 0 && py < 0;
            boolean isInnerRegion = px * px + py * py < inner * inner;
            int x_shift = (isLeftUpperRegion && !isInnerRegion) ? 1 : 0;
            mZoomedRect.left = midX - radius + x_shift * radius;
            mZoomedRect.right = midX + radius * x_shift;
            mZoomedRect.top = midY - radius + x_shift * radius;
            mZoomedRect.bottom = midY + radius * x_shift;
        }
    }

    @Override
    protected Shot getShotFromPos(float x, float y) {
        int rings = round.target.getZones();
        Shot s = new Shot();
        s.x = (x - midX) / radius;
        s.y = (y - midY) / radius;
        // Handle selection via right indicator bar
        if (x > midX + radius + 30 * density && mModeEasy) {
            s.zone = (int) (y * (rings + 1) / (float) contentHeight);
        } else { // Handle via target
            s.zone = round.target.getZoneFromPoint(s.x, s.y);
        }

        // Correct points_zone
        if (s.zone < -1 || s.zone >= rings) {
            s.zone = Shot.MISS;
        }

        return s;
    }

    @Override
    protected boolean selectPreviousShots(MotionEvent motionEvent, float x, float y) {
        // Handle selection of already saved shoots
        int arrow = mPasseDrawer.getPressedPosition(x, y);
        if (arrow != -1 && currentArrow != arrow) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (longPressTimer != null) {
                    mPasseDrawer.setPressed(-1);
                    longPressTimer.cancel();
                    longPressTimer = null;
                    animateSelectCircle(arrow);
                }
            } else if (mPasseDrawer.getPressed() != arrow) {
                // If new item gets selected cancel old timer and start new one
                mPasseDrawer.setPressed(arrow);
                if (longPressTimer != null) {
                    longPressTimer.cancel();
                }
                longPressTimer = new Timer();
                longPressTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        h.post(task);
                    }
                }, 1500);
            }
            invalidate();
            return true;
        } else {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (longPressTimer != null) {
                    longPressTimer.cancel();
                    longPressTimer = null;
                }
            }
            mPasseDrawer.setPressed(-1);
        }
        return false;
    }

    private void animateMode() {
        mCurSelecting = -2;
        mCurAnimationProgress = 0;
        mOutFromX = midX;
        mOutFromY = midY;
        oldRadius = radius;
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

    class Midpoint {
        float count = 0;
        float sumX = 0;
        float sumY = 0;
    }
}
