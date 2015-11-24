/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.target.Target;

public class ArrowDispersionView extends View implements View.OnTouchListener {
    private static final float ZOOM_FACTOR = 3;
    protected int contentWidth;
    protected int contentHeight;
    protected float density;
    protected int mZoneCount;
    private Paint fillPaint;
    private ArrayList<Shot> shots;
    private float orgRadius, orgMidX, orgMidY;
    private Target target;
    private float zoomInX = -1, zoomInY = -1;

    public ArrowDispersionView(Context context) {
        this(context, null);
    }
    public ArrowDispersionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrowDispersionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnTouchListener(this);
        fillPaint = new Paint();
        fillPaint.setAntiAlias(true);
        density = getResources().getDisplayMetrics().density;
    }

    public void reset() {
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        contentWidth = getWidth();
        contentHeight = getHeight();
        calcSizes();
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();

        // Handle via target
        zoomInX = (x - orgMidX) / (orgRadius - 30 * density);
        zoomInY = (y - orgMidY) / (orgRadius - 30 * density);
        invalidate();

        // If finger is released go to next shoot
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            zoomInX = -1;
            zoomInY = -1;
            invalidate();
            return true;
        }
        return true;
    }

    public void setShoots(ArrayList<Shot> passes) {
        shots = passes;
        invalidate();
    }

    public void setTarget(Target target) {
        this.target = target;
        mZoneCount = target.getZones();
        reset();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (zoomInX != -1 || zoomInY != -1) {
            drawZoomedInTarget(canvas);
        } else {
            drawTarget(canvas, orgMidX, orgMidY, orgRadius);
        }
    }

    private void drawZoomedInTarget(Canvas canvas) {
        float px = zoomInX;
        float py = zoomInY;
        int radius2 = (int) (orgRadius * ZOOM_FACTOR);
        int x = (int) (orgMidX - px * (orgRadius + 30 * density));
        int y = (int) (orgMidY - py * (orgRadius + 30 * density) - 60 * density);
        drawTarget(canvas, x, y, radius2);
    }

    private void drawTarget(Canvas canvas, float x, float y, float radius) {
        // Erase background
        fillPaint.setColor(0xFFEEEEEE);
        canvas.drawRect(0, 0, contentWidth, contentHeight, fillPaint);

        // Draw actual target face
        target.setBounds((int) (x - radius), (int) (y - radius), (int) (x + radius),
                (int) (y + radius));
        target.draw(canvas);

        // Draw exact arrow position
        drawArrows(canvas);
    }

    private void drawArrows(Canvas canvas) {
        int spots = 1;
        Midpoint m = new Midpoint();

        for (Shot shot : shots) {
            target.drawArrow(canvas, shot);
            m.sumX += shot.x;
            m.sumY += shot.y;
            m.count++;
        }

        for (int i = 0; i < spots; i++) {
            if (m.count >= 2) {
                target.drawArrowAvg(canvas, m.sumX / m.count,
                        m.sumY / m.count, i);
            }
        }
    }

    protected void calcSizes() {
        float radH = (contentHeight - 10 * density) / 2.45f;
        float radW = (contentWidth - 20 * density) * 0.5f;
        orgRadius = (int) (Math.min(radW, radH));
        orgMidX = contentWidth / 2;
        orgMidY = orgRadius + (int) (10 * density);
    }

    class Midpoint {
        float count = 0;
        float sumX = 0;
        float sumY = 0;
    }
}
