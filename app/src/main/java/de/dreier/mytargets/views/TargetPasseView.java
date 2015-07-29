/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.target.Target;

public class TargetPasseView extends View {

    private Passe passe = new Passe(3);
    private float density;
    private Paint drawColorP;
    private Target target;
    private int mZoneCount;
    private int radius;

    public TargetPasseView(Context context) {
        super(context);
        init();
    }

    public TargetPasseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TargetPasseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        drawColorP = new Paint();
        drawColorP.setAntiAlias(true);
        density = getResources().getDisplayMetrics().density;
    }

    public void setPasse(Passe p, Target tar) {
        passe = p;
        target = tar;
        mZoneCount = tar.getZones();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Initialize variables
        init();

        target.setBounds(0, 0, 2 * radius, 2 * radius);
        target.draw(canvas);

        // Draw exact arrow position
        drawArrows(canvas);
    }

    private void drawArrows(Canvas canvas) {
        float count = 0;
        float sumX = 0;
        float sumY = 0;
        for (int i = 0; i < passe.shot.length; i++) {
            // For yellow and white background use black font color
            int color = i == mZoneCount || passe.shot[i].zone < 0 ? 0xFF000000 : target.getFillColor(
                    passe.shot[i].zone);
            drawColorP.setColor(color);
            float selX = passe.shot[i].x;
            float selY = passe.shot[i].y;
            sumX += selX;
            sumY += selY;
            count++;

            // Draw arrow position
            float xp = radius + selX * radius;
            float yp = radius + selY * radius;
            canvas.drawCircle(xp, yp, 3 * density, drawColorP);
        }

        if (count >= 2) {
            drawColorP.setColor(Color.RED);
            canvas.drawCircle(radius + (sumX / count) * radius, radius + (sumY / count) * radius,
                    3 * density, drawColorP);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int contentWidth = getWidth();
        int contentHeight = getHeight();
        radius = Math.min(contentWidth, contentHeight) / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = (int) (60 * density);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width, height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(Math.min(heightSize, desiredWidth), widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(width, heightSize);
        } else {
            height = width;
        }

        setMeasuredDimension(width, height);
    }
}
