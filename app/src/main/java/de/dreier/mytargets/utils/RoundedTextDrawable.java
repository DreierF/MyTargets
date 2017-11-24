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

package de.dreier.mytargets.utils;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.TextPaint;

import java.util.Locale;

import de.dreier.mytargets.features.statistics.ArrowStatistic;

public class RoundedTextDrawable extends Drawable {
    @NonNull
    private final Paint mPaint;
    @NonNull
    private final RectF mRectF;
    private final String mText;
    @NonNull
    private final Paint mTextPaint;
    @NonNull
    private final Rect mTextBounds;

    private RoundedTextDrawable(String text, int bgColor, int textColor) {
        mRectF = new RectF();
        mTextBounds = new Rect();
        mPaint = new Paint();
        mTextPaint = new TextPaint();
        mTextPaint.setColor(textColor);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(bgColor);
        mText = text;
    }

    public RoundedTextDrawable(@NonNull ArrowStatistic item) {
        this(String.format(Locale.US, "%.3f", item.average.getStdDev()),
                item.getAppropriateBgColor(),
                item.getAppropriateTextColor());
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawOval(mRectF, mPaint);
        canvas.drawText(mText, mRectF.centerX() - mTextBounds.width() / 2,
                mRectF.centerY() + mTextBounds.height() / 2, mTextPaint);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        mRectF.set(bounds);
        mTextPaint.setTextSize(mRectF.height() / 2.5f);
        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBounds);
    }

    @Override
    public void setAlpha(int alpha) {
        if (mPaint.getAlpha() != alpha) {
            mPaint.setAlpha(alpha);
            invalidateSelf();
        }
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setFilterBitmap(boolean filter) {
        mPaint.setFilterBitmap(filter);
        invalidateSelf();
    }
}
