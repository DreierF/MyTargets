package de.dreier.mytargets.utils;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;

import de.dreier.mytargets.models.ArrowStatistic;

public class RoundedTextDrawable extends Drawable {
    private final Paint mPaint;
    private final RectF mRectF;
    private final String mText;
    private Paint mTextPaint;
    private Rect mTextBounds;

    public RoundedTextDrawable(String text, int bgColor, int textColor) {
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

    public RoundedTextDrawable(ArrowStatistic item) {
        this(String.format("%.1f", item.avgPoints()), item.getAppropriateBgColor(), item.getAppropriateTextColor());
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawOval(mRectF, mPaint);
        canvas.drawText(mText, mRectF.centerX() - mTextBounds.width() / 2, mRectF.centerY() + mTextBounds.height() / 2, mTextPaint);
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

    @Override
    public void setDither(boolean dither) {
        mPaint.setDither(dither);
        invalidateSelf();
    }
}