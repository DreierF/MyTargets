/*
 * Copyright (C) 2017 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.shared.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.evernote.android.state.State;
import com.evernote.android.state.StateSaver;

import java.util.List;

import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.utils.EndRenderer;

public class EndView extends View {

    private int shotCount;
    private float density;
    @State
    EndRenderer endRenderer = new EndRenderer();
    private final RectF rect = new RectF();

    public EndView(Context context) {
        super(context);
    }

    public EndView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EndView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setShots(Target target, @NonNull List<Shot> shots) {
        shotCount = shots.size();
        density = getResources().getDisplayMetrics().density;
        endRenderer.init(this, density, target);
        endRenderer.setShots(shots);
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        endRenderer.draw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        rect.left = 0;
        rect.right = getWidth();
        rect.top = 0;
        rect.bottom = getHeight();
        endRenderer.setRect(rect);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = (int) (60 * shotCount * density);
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
        return StateSaver.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(StateSaver.restoreInstanceState(this, state));
    }

    public void setAmbientMode(boolean ambient) {
        endRenderer.setAmbientMode(ambient);
        invalidate();
    }
}
