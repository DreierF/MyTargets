package de.dreier.mytargets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class PassenView extends View {
    private int contentWidth, contentHeight;

    private TextPaint mTextPaint;
    private Paint circleColorP;

    private int[] points = {-2,-2,-2};
    private float placePerShoot;
    private int targetRound;
    private float density;

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
        density = getResources().getDisplayMetrics().density;
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(22*density);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        circleColorP = new Paint();
        circleColorP.setAntiAlias(true);
        circleColorP.setStrokeWidth(2*density);
    }

    public void setPoints(int[] p, int tar) {
        points = p;
        targetRound = tar;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        placePerShoot = contentWidth/(float)points.length;

        for(int i=0;i<points.length;i++) {
            drawCircle(canvas, i*placePerShoot+(placePerShoot/2.0f), contentHeight/2.0f, points[i]);
        }
    }

    private void drawCircle(Canvas can, float x, float y, int zone) {
        int colorInd;
        if(zone>-1) {
            colorInd = TargetView.target_rounds[targetRound][zone];
        } else {
            colorInd = 3;
        }
        circleColorP.setStyle(Paint.Style.FILL_AND_STROKE);
        circleColorP.setColor(TargetView.rectColor[colorInd]);
        can.drawCircle(x, y, 17*density, circleColorP);
        circleColorP.setStyle(Paint.Style.STROKE);
        circleColorP.setColor(TargetView.circleStrokeColor[colorInd]);
        can.drawCircle(x, y, 17*density, circleColorP);
        mTextPaint.setColor(colorInd==0||colorInd==4 ? Color.BLACK : Color.WHITE);
        can.drawText(TargetView.getStringByZone(targetRound, zone), x, y + 7*density, mTextPaint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        contentWidth = getWidth();
        contentHeight = getHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = (int)(60*points.length*density);
        int desiredHeight = (int)(40*density);

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
