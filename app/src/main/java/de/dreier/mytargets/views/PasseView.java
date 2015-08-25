/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.target.Target;
import de.dreier.mytargets.shared.utils.PasseDrawer;

public class PasseView extends View {

    private Passe passe = new Passe(3);
    private float density;
    private PasseDrawer mPasseDrawer;
    private final RectF rect = new RectF();

    public PasseView(Context context) {
        super(context);
    }

    public PasseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PasseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPoints(Passe p, Target target) {
        passe = p;
        density = getResources().getDisplayMetrics().density;
        mPasseDrawer = new PasseDrawer(this, density, target);
        if (rect.width() > 0) {
            mPasseDrawer.animateToRect(rect);
        }
        mPasseDrawer.setPasse(p);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPasseDrawer.draw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int contentWidth = getWidth();
        int contentHeight = getHeight();
        rect.left = 0;
        rect.right = contentWidth;
        rect.top = 0;
        rect.bottom = contentHeight;
        mPasseDrawer.animateToRect(rect);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = (int) (60 * passe.shot.length * density);
        int desiredHeight = (int) (50 * density);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width, height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);
    }
}
