package de.dreier.mytargets.views;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import de.dreier.mytargets.models.Circle;
import de.dreier.mytargets.models.Shot;

public class PassesView extends View {
    private int contentWidth, contentHeight;

    private Shot[] points = new Shot[3];
    private float density;
    private Circle circle;

    public PassesView(Context context) {
        super(context);
        init();
    }

    public PassesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PassesView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        density = getResources().getDisplayMetrics().density;
    }

    public void setPoints(Shot[] p, int tar) {
        points = p;
        circle = new Circle(density, tar);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float placePerShoot = contentWidth / (float) points.length;

        for (int i = 0; i < points.length; i++) {
            circle.draw(canvas, i * placePerShoot + (placePerShoot / 2.0f),
                        contentHeight / 2.0f, points[i].zone,
                        17, !TextUtils.isEmpty(points[i].comment));
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        contentWidth = getWidth();
        contentHeight = getHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = (int) (60 * points.length * density);
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
