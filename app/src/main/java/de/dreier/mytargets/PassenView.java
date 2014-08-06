package de.dreier.mytargets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;


/**
 * TODO: document your custom view class.
 */
public class PassenView extends View {
    private int contentWidth, contentHeight;

    private TextPaint mTextPaint;
    private Paint circleColorP;

    private int[] points = {-2,-2,-2};
    private float placePerShoot;

    public PassenView(Context context) {
        super(context);
        init(null, 0);
    }

    public PassenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public PassenView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(60);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        circleColorP = new Paint();
        circleColorP.setAntiAlias(true);
        circleColorP.setStrokeWidth(6);
    }

    public void setPoints(int[] p) {
        points = p;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for(int i=0;i<points.length;i++) {
            drawCircle(canvas, i*placePerShoot+(placePerShoot/2.0f), contentHeight/2.0f, points[i]);
        }
    }

    private void drawCircle(Canvas can, float x, float y, int points) {
        int i=10-points;
        circleColorP.setStyle(Paint.Style.FILL_AND_STROKE);
        circleColorP.setColor(TargetView.rectColor[i]);
        can.drawCircle(x, y, 50, circleColorP);
        circleColorP.setStyle(Paint.Style.STROKE);
        circleColorP.setColor(TargetView.circleStrokeColor[i]);
        can.drawCircle(x, y, 50, circleColorP);
        mTextPaint.setColor(i>1&&i<8||i>9 ? Color.WHITE : Color.BLACK);
        can.drawText(points==0?"M":"" + points, x, y + 20, mTextPaint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        contentWidth = getWidth();
        contentHeight = getHeight();
        placePerShoot = contentWidth/(float)points.length;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = 200*points.length;
        int desiredHeight = 106;

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
