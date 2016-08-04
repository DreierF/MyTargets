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
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import de.dreier.mytargets.shared.models.db.Passe;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.utils.ParcelsBundler;
import de.dreier.mytargets.shared.utils.ScoresDrawer;
import icepick.Icepick;
import icepick.State;

public class PasseView extends View {

    private Passe passe = new Passe(3);
    private float density;
    @State(ParcelsBundler.class)
    ScoresDrawer mScoresDrawer = new ScoresDrawer();
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
        mScoresDrawer.init(this, density, target);
        if (rect.width() > 0) {
            mScoresDrawer.animateToRect(rect);
        }
        mScoresDrawer.setShots(p.getShots());
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mScoresDrawer.draw(canvas);
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
        mScoresDrawer.animateToRect(rect);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = (int) (60 * passe.getShots().size() * density);
        int desiredHeight = (int) (50 * density);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

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

    @Override
    public Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
    }
}
