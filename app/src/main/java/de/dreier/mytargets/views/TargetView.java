/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
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
    private static final int SPOT_ZOOM_IN = -3;
    private static final int MODE_CHANGE = -2;
    private float radius, midX, midY;
    private Paint drawColorP;
    private boolean showAll = false;
    private ArrayList<Passe> mOldShots;
    private Timer longPressTimer;
    private final Handler h = new Handler();
    private float oldRadius;
    RectF[] spotRects;
    private float orgRadius, orgMidX, orgMidY;
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
    private boolean spotFocused = false;
    private Paint shadowP;
    private RectF orgRect;
    private Paint scorePaint;

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
        initSpotBounds();
    }

    private void initSpotBounds() {
        Rect rect = new Rect(0, 0, 500, 500);
        if (isSpot()) {
            SpotBase spotBase = (SpotBase) round.target;
            spotRects = new RectF[spotBase.getFaceCount()];
            for (int i = 0; i < spotBase.getFaceCount(); i++) {
                spotRects[i] = spotBase.getBoundsF(i, rect);
            }
        } else {
            spotRects = new RectF[1];
            spotRects[0] = new RectF(rect);
        }
    }

    private void init() {
        // Set up a default TextPaint object
        density = getResources().getDisplayMetrics().density;
        drawColorP = new Paint();
        drawColorP.setAntiAlias(true);

        shadowP = new Paint();
        shadowP.setAntiAlias(true);
        shadowP.setColor(0xFF009900);

        scorePaint = new TextPaint();
        scorePaint.setAntiAlias(true);
        scorePaint.setColor(Color.BLACK);
        scorePaint.setTextSize(15 * density);

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
            drawTarget(canvas, mOutFromX + (midX - mOutFromX) * mCurAnimationProgress,
                    mOutFromY + (midY - mOutFromY) * mCurAnimationProgress,
                    oldRadius + (radius - oldRadius) * mCurAnimationProgress);
        } else {
            if (!mKeyboardMode && curZone >= -1) {
                drawZoomedInTarget(canvas);
            } else {
                drawTarget(canvas, midX, midY, radius);
            }
        }

        // Draw all points of this passe at the bottom
        mPasseDrawer.draw(canvas);
    }

    private void drawZoomedInTarget(Canvas canvas) {
        float px = mPasse.shot[currentArrow].x;
        float py = mPasse.shot[currentArrow].y;
        int radius2 = (int) (radius * ZOOM_FACTOR);
        int x = (int) ((midX - orgMidX) * ZOOM_FACTOR + orgMidX - px * (orgRadius + 30 * density));
        int y = (int) ((midY - orgMidY) * ZOOM_FACTOR + orgMidY - py * (orgRadius + 30 * density) -
                60 * density);
        drawTarget(canvas, x, y, radius2);
    }

    private void drawTarget(Canvas canvas, float x, float y, float radius) {
        // Erase background
        drawColorP.setColor(0xfffafafa);
        canvas.drawRect(0, 0, contentWidth, contentHeight, drawColorP);

        // Draw actual target face
        round.target.setBounds((int) (x - radius), (int) (y - radius), (int) (x + radius),
                (int) (y + radius));
        round.target.draw(canvas);

        // Draw exact arrow position
        if (!mKeyboardMode) {
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
    private void drawPasseShots(Canvas canvas, float x, float y, float radius, Midpoint m, Passe p, boolean old) {
        RectF rect = new RectF(
                x - radius,
                y - radius,
                x + radius,
                y + radius);
        for (int i = 0;
             old ? i < p.shot.length :
                     (i <= lastSetArrow + 1 && i < round.arrowsPerPasse &&
                             p.shot[i].zone != Shot.NOTHING_SELECTED); i++) {

            // For yellow and white background use black font color
            int colorInd = i == mZoneCount || p.shot[i].zone < 0 ? 0 :
                    round.target.getZoneColor(p.shot[i].zone);
            drawColorP.setColor(
                    colorInd == 0 || colorInd == Color.WHITE ? Color.BLACK : Color.WHITE);
            float selX = p.shot[i].x;
            float selY = p.shot[i].y;
            if (i != currentArrow || old) {
                m.sumX += selX;
                m.sumY += selY;
                m.count++;
            }

            // Draw arrow position
            RectF spotRect = new RectF(spotRects[i % spotRects.length]);
            float scale = radius / 250.0f;
            spotRect.left = rect.left + spotRect.left * scale;
            spotRect.top = rect.top + spotRect.top * scale;
            spotRect.right = rect.left + spotRect.right * scale;
            spotRect.bottom = rect.top + spotRect.bottom * scale;

            float xp = spotRect.left + (1 + selX) * spotRect.width() * 0.5f;
            float yp = spotRect.top + (1 + selY) * spotRect.height() * 0.5f;

            if (i == currentArrow && !old) { // As + if it is currently selected
                int zone = round.target.getZoneFromPoint(selX, selY);
                String zoneString = round.target.zoneToString(zone, currentArrow);
                float width = scorePaint.measureText(zoneString) / 2.0f;
                canvas.drawText(zoneString, xp - width, yp - 10 * density, scorePaint);
                canvas.drawCircle(xp, yp, 3 * density, shadowP);
                canvas.drawLine(xp - 10 * density, yp, xp + 10 * density, yp, shadowP);
                canvas.drawLine(xp, yp - 10 * density, xp, yp + 10 * density, shadowP);
            } else { // otherwise as dot
                canvas.drawCircle(xp, yp, 3 * density, drawColorP);
            }
        }
    }

    @Override
    protected Coordinate initAnimationPositions(int i) {
        Coordinate coordinate = new Coordinate();
        if (mKeyboardMode) {
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
        int keyboardHeight = mKeyboardMode ? keyboard.getMeasuredHeight() : 0;
        float radH = (contentHeight - 10 * density - keyboardHeight) / 2.45f;
        float radW = (contentWidth - 20 * density) * 0.5f;
        orgRadius = (int) (Math.min(radW, radH));
        orgMidX = contentWidth / 2;
        orgMidY = orgRadius + (int) (10 * density);
        orgRect = new RectF(
                orgMidX - orgRadius,
                orgMidY - orgRadius,
                orgMidX + orgRadius,
                orgMidY + orgRadius);
        RectF rect = new RectF();
        rect.left = 30 * density;
        rect.right = contentWidth - 30 * density;
        rect.top = orgMidY + orgRadius;
        rect.bottom = contentHeight - keyboardHeight;
        mPasseDrawer.animateToRect(rect);
        animateToZoomSpot();
    }

    private void initKeyboard() {
        keyboard = (LinearLayout) ((ViewGroup) getParent()).findViewById(R.id.keyboard);
        keyboard.findViewById(R.id.hide_keyboard).setOnClickListener(v -> switchMode(false, true));
        ((ViewGroup) getParent()).findViewById(R.id.show_keyboard)
                .setOnClickListener(v -> switchMode(true, true));
        keyboard.findViewById(R.id.backspace).setOnClickListener(v -> {
            if (currentArrow > 0) {
                int index = Math.min(currentArrow, mPasse.shot.length) - 1;
                mPasse.shot[index].zone = Shot.NOTHING_SELECTED;
                currentArrow = index;
                lastSetArrow = index - 1;
                invalidate();
            }
        });

        populateKeyboard();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean mode = prefs.getBoolean("target_mode", false);
        switchMode(mode, false);
    }

    private void switchMode(boolean mode, boolean animate) {
        if (mode != mKeyboardMode) {
            mKeyboardMode = mode;
            if (animate) {
                animateMode();
            } else {
                keyboard.setVisibility(mode ? VISIBLE : GONE);
            }
            if (mKeyboardMode) {
                animateFromZoomSpot();
            } else {
                animateToZoomSpot();
            }
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            prefs.edit().putBoolean("target_mode", mKeyboardMode).apply();
        }
    }

    private class Zone {
        final int zone;
        final String text;

        public Zone(int zone, String text) {
            this.zone = zone;
            this.text = text;
        }
    }

    private void populateKeyboard() {
        ArrayList<Zone> list = new ArrayList<>();
        String last = "";
        for (int i = 0; i < round.target.getZones(); i++) {
            String zone = round.target.zoneToString(i, currentArrow);
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

        line1.removeAllViews();
        line2.removeAllViews();
        line3.removeAllViews();

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
            button.setOnClickListener(v -> {
                if (currentArrow < round.arrowsPerPasse) {
                    mPasse.shot[currentArrow].zone = zone.zone;
                    mPasse.shot[currentArrow].x = -1;
                    mPasse.shot[currentArrow].y = -1;
                    mPasseDrawer.setSelection(currentArrow,
                            initAnimationPositions(currentArrow),
                            mKeyboardMode ? PasseDrawer.MAX_CIRCLE_SIZE : 0);

                    if (currentArrow == lastSetArrow + 1) {
                        lastSetArrow++;
                    }

                    animateSelectCircle(lastSetArrow + 1);

                    if (lastSetArrow + 1 >= round.arrowsPerPasse && setListener != null) {
                        mPasse.setId(setListener.onTargetSet(new Passe(mPasse), false));
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
        s.x = (x - orgMidX) / (orgRadius - 30 * density);
        s.y = (y - orgMidY) / (orgRadius - 30 * density);

        s.zone = round.target.getZoneFromPoint(s.x, s.y);

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
        moveAnimator.addUpdateListener(valueAnimator -> {
            mCurAnimationProgress = (Float) valueAnimator.getAnimatedValue();
            if (mKeyboardMode) {
                keyboard.setTranslationY(
                        (1 - mCurAnimationProgress) * keyboard.getMeasuredHeight());
            } else {
                keyboard.setTranslationY(mCurAnimationProgress * keyboard.getMeasuredHeight());
            }
            if (mCurAnimationProgress == 1.0f) {
                moveAnimator.cancel();
                mCurSelecting = -1;
                if (!mKeyboardMode) {
                    keyboard.setVisibility(GONE);
                }
                keyboard.setTranslationY(0);
            }
            invalidate();
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
        if (round.target.dependsOnArrowIndex()) {
            populateKeyboard();
        }
        if (isSpot() && spotFocused) {
            mCurSelecting = SPOT_ZOOM_IN;
            initAnimation();

            radius = orgRadius;
            midX = orgMidX;
            midY = orgMidY;

            final ValueAnimator moveAnimator = ValueAnimator.ofFloat(0, 1);
            moveAnimator.setInterpolator(
                    currentArrow < round.arrowsPerPasse ? new AccelerateInterpolator() :
                            new AccelerateDecelerateInterpolator());
            moveAnimator.addUpdateListener(valueAnimator -> {
                mCurAnimationProgress = (Float) valueAnimator.getAnimatedValue();
                if (mCurAnimationProgress == 1.0f) {
                    moveAnimator.cancel();
                    spotFocused = false;
                    animateToZoomSpot();
                }
                invalidate();
            });
            moveAnimator.setDuration(200);
            moveAnimator.start();
        }
    }

    @Override
    protected void animateToZoomSpot() {
        if (!spotFocused) {
            radius = orgRadius;
            midX = orgMidX;
            midY = orgMidY;
        }
        if (isSpot() && currentArrow < round.arrowsPerPasse && radius > 0 &&
                !spotFocused && !mKeyboardMode) {
            mCurSelecting = SPOT_ZOOM_IN;
            initAnimation();

            RectF spotRect = new RectF(spotRects[currentArrow % spotRects.length]);
            float scale = orgRadius / 250;
            spotRect.left = orgRect.left + spotRect.left * scale;
            spotRect.top = orgRect.top + spotRect.top * scale;
            spotRect.right = orgRect.left + spotRect.right * scale;
            spotRect.bottom = orgRect.top + spotRect.bottom * scale;

            float zoomFactor = orgRadius * 2.0f / spotRect.width();
            radius = (int) (orgRadius * zoomFactor);
            midX = (int) (radius + orgMidX + (orgRect.left - spotRect.centerX()) * zoomFactor);
            midY = (int) (radius + orgMidY + (orgRect.top - spotRect.centerY()) * zoomFactor);

            final ValueAnimator moveAnimator = ValueAnimator.ofFloat(0, 1);
            moveAnimator.setInterpolator(
                    currentArrow == 0 ? new AccelerateDecelerateInterpolator() :
                            new DecelerateInterpolator());
            moveAnimator.addUpdateListener(valueAnimator -> {
                mCurAnimationProgress = (Float) valueAnimator.getAnimatedValue();
                if (mCurAnimationProgress == 1.0f) {
                    moveAnimator.cancel();
                    mCurSelecting = -1;
                    spotFocused = true;
                }
                invalidate();
            });
            moveAnimator.setDuration(200);
            moveAnimator.start();
        }
    }

    private boolean isSpot() {
        return round.target instanceof SpotBase;
    }

    class Midpoint {
        float count = 0;
        float sumX = 0;
        float sumY = 0;
    }
}
