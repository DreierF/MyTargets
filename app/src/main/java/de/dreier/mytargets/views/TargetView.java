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
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.target.SpotBase;
import de.dreier.mytargets.shared.utils.PasseDrawer;
import de.dreier.mytargets.shared.views.TargetViewBase;
import de.dreier.mytargets.utils.TextInputDialog;

public class TargetView extends TargetViewBase {

    private static final float ZOOM_FACTOR = 2;
    private static final int SPOT_ZOOMIN = -3;
    private static final int MODE_CHANGE = -2;
    private int radius, midX, midY;
    private TextPaint mTextPaint;
    private Paint thinBlackBorder, thinWhiteBorder, drawColorP, rectColorP, circleColorP;
    private boolean showAll = false;
    private ArrayList<Passe> mOldShots;
    private Timer longPressTimer;
    private final Handler h = new Handler();
    private int oldRadius;
    private int orgRadius, orgMidX, orgMidY;
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
    private LinearLayout keyboard;

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
        animateToZoomSpot();
        invalidate();
    }

    public void showAll(boolean showAll) {
        this.showAll = showAll;
        invalidate();
    }

    public void setOldShoots(ArrayList<Passe> oldOnes) {
        mOldShots = oldOnes;
        invalidate();
    }

    @Override
    public void setRoundTemplate(RoundTemplate r) {
        super.setRoundTemplate(r);
        initKeyboard();
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

        Paint thinLine = new Paint();
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
        if (mCurSelecting < -1) {
            drawTarget(canvas, (int) (mOutFromX + (midX - mOutFromX) * mCurAnimationProgress),
                    (int) (mOutFromY + (midY - mOutFromY) * mCurAnimationProgress),
                    (int) (oldRadius + (radius - oldRadius) * mCurAnimationProgress));
        } else {
            if (!mModeEasy && curZone >= -1) {
                drawZoomedInTarget(canvas);
            } else {
                drawTarget(canvas, midX, midY, radius);
            }
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

        int x = (int) (orgMidX - orgRadius - radius * ZOOM_FACTOR * px - orgRadius * 0.5);
        int y = (int) (orgMidY - orgRadius - radius * ZOOM_FACTOR * py - orgRadius * 0.5);
        drawTarget(canvas, x, y, (int) (radius * ZOOM_FACTOR));
        canvas.restore();
    }

    private void drawTarget(Canvas canvas, int x, int y, int radius) {
        // Erase background
        drawColorP.setColor(0xfffafafa);
        //canvas.drawRect(0, 0, contentWidth, contentHeight, drawColorP);

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
        contentHeight = getMeasuredHeight() - (mModeEasy ? keyboard.getMeasuredHeight() : 0);
        float radH = (contentHeight - 10 * density) / 2.45f;
        float radW = (contentWidth - 20 * density) * 0.5f;
        radius = (int) (Math.min(radW, radH));
        midX = contentWidth / 2;
        midY = radius + (int) (10 * density);
        orgRadius = radius;
        orgMidX = midX;
        orgMidY = midY;
        RectF rect = new RectF();
        rect.left = 30 * density;
        rect.right = contentWidth - 30 * density;
        rect.top = midY + radius;
        rect.bottom = contentHeight;
        mPasseDrawer.animateToRect(rect);
        animateToZoomSpot();
    }

    private void initKeyboard() {
        keyboard = (LinearLayout) ((ViewGroup) getParent()).findViewById(R.id.keyboard);
        keyboard.findViewById(R.id.hide_keyboard).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMode(false, true);
            }
        });
        ((ViewGroup) getParent()).findViewById(R.id.show_keyboard)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switchMode(true, true);
                    }
                });
        keyboard.findViewById(R.id.backspace).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentArrow > 0) {
                    int index = Math.min(currentArrow, mPasse.shot.length) - 1;
                    mPasse.shot[index].zone = Shot.NOTHING_SELECTED;
                    currentArrow = index;
                    lastSetArrow = index - 1;
                    invalidate();
                }
            }
        });
        if (mModeEasy) {
            populateKeyboard();
        }
    }

    private void switchMode(boolean mode, boolean animate) {
        if (mode != mModeEasy) {
            mModeEasy = mode;
            if (animate) {
                animateMode();
            }
        }
    }

    private class Zone {
        int zone;
        String text;

        public Zone(int zone, String text) {
            this.zone = zone;
            this.text = text;
        }
    }

    private void populateKeyboard() {
        ArrayList<Zone> list = new ArrayList<>();
        String last = "";
        for (int i = 0; i < round.target.getZones(); i++) {
            String zone = round.target.zoneToString(i);
            if (!last.equals(zone)) {
                list.add(new Zone(i, zone));
            }
            last = zone;
        }
        if (!last.equals("M")) {
            list.add(new Zone(-1, "M"));
        }

        LinearLayout line1 = ((LinearLayout) keyboard.findViewById(R.id.line1));
        LinearLayout line2 = ((LinearLayout) keyboard.findViewById(R.id.line2));
        LinearLayout line3 = ((LinearLayout) keyboard.findViewById(R.id.line3));

        if (line1.getChildCount() > 0) {
            line1.removeAllViews();
        }
        if (line2.getChildCount() > 0) {
            line2.removeAllViews();
        }
        if (line3.getChildCount() > 0) {
            line3.removeAllViews();
        }

        // Calculate arrangement
        int lines = (list.size() / 2 < 5 ? 2 : 3);
        int itemsPerLine = list.size() / lines;
        keyboard.findViewById(R.id.line2_container).setVisibility(lines < 3 ? GONE : VISIBLE);

        // Add buttons to layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        LinearLayout line = line1;
        for (int i = 0; i < list.size(); i++) {
            if (i == itemsPerLine && lines == 3) {
                line = line2;
            } else if (i % itemsPerLine == 0 && i > 0) {
                line = line3;
            }
            Button button = (Button) inflater.inflate(R.layout.key_layout, line, false);
            final Zone zone = list.get(i);
            button.setText(zone.text);
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentArrow < round.arrowsPerPasse) {
                        mPasse.shot[currentArrow].zone = zone.zone;
                        mPasse.shot[currentArrow].x = -1;
                        mPasse.shot[currentArrow].y = -1;
                        mPasseDrawer.setSelection(currentArrow,
                                initAnimationPositions(currentArrow),
                                mModeEasy ? PasseDrawer.MAX_CIRCLE_SIZE : 0);

                        if (currentArrow == lastSetArrow + 1) {
                            lastSetArrow++;
                        }

                        animateSelectCircle(lastSetArrow + 1);

                        if (lastSetArrow + 1 >= round.arrowsPerPasse && setListener != null) {
                            mPasse.setId(setListener.onTargetSet(new Passe(mPasse), false));
                        }
                    }
                }
            });
            line.addView(button);
        }
    }

    @Override
    protected Shot getShotFromPos(float x, float y) {
        int rings = round.target.getZones();
        Shot s = new Shot();
        s.x = (x - orgMidX) / orgRadius;
        s.y = (y - orgMidX) / orgRadius;
        // Handle selection via right indicator bar
        if (mModeEasy && x > midX + radius + 30 * density) {
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
        mCurSelecting = MODE_CHANGE;
        initAnimation();
        calcSizes();
        keyboard.setVisibility(VISIBLE);

        final ValueAnimator moveAnimator = ValueAnimator.ofFloat(0, 1);
        moveAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        moveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCurAnimationProgress = (Float) valueAnimator.getAnimatedValue();
                if (mModeEasy) {
                    keyboard.setTranslationY(
                            (1 - mCurAnimationProgress) * keyboard.getMeasuredHeight());
                } else {
                    keyboard.setTranslationY(mCurAnimationProgress * keyboard.getMeasuredHeight());
                }
                if (mCurAnimationProgress == 1.0f) {
                    moveAnimator.cancel();
                    mCurSelecting = -1;
                    if (!mModeEasy) {
                        keyboard.setVisibility(GONE);
                    }
                    keyboard.setTranslationY(0);
                }
                invalidate();
            }
        });
        moveAnimator.setDuration(300);
        moveAnimator.start();
    }

    private void initAnimation() {
        mCurAnimationProgress = 0;
        mOutFromX = midX;
        mOutFromY = midY;
        oldRadius = radius;
    }

    @Override
    protected void animateFromZoomSpot() {
        if (round.target instanceof SpotBase) {
            mCurSelecting = SPOT_ZOOMIN;
            initAnimation();

            radius = orgRadius;
            midX = orgMidX;
            midY = orgMidY;

            final ValueAnimator moveAnimator = ValueAnimator.ofFloat(0, 1);
            moveAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            moveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    mCurAnimationProgress = (Float) valueAnimator.getAnimatedValue();
                    if (mCurAnimationProgress == 1.0f) {
                        moveAnimator.cancel();
                        animateToZoomSpot();
                    }
                    invalidate();
                }
            });
            moveAnimator.setDuration(150);
            moveAnimator.start();
        }
    }

    @Override
    protected void animateToZoomSpot() {
        if (round.target instanceof SpotBase && currentArrow < round.arrowsPerPasse && radius > 0) {
            mCurSelecting = SPOT_ZOOMIN;
            initAnimation();

            Rect rect = new Rect(
                    orgMidX - orgRadius,
                    orgMidY - orgRadius,
                    orgMidX + orgRadius,
                    orgMidY + orgRadius);
            Rect spotRect = ((SpotBase) round.target).getBounds(currentArrow, rect);

            int zoomFactor = orgRadius * 2 / spotRect.width();
            radius = orgRadius * zoomFactor;
            midX = radius + orgMidX + (rect.left - spotRect.centerX()) * zoomFactor;
            midY = radius + orgMidY + (rect.top - spotRect.centerY()) * zoomFactor;

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
            moveAnimator.setDuration(150);
            moveAnimator.start();
        }
    }

    class Midpoint {
        float count = 0;
        float sumX = 0;
        float sumY = 0;
    }
}
