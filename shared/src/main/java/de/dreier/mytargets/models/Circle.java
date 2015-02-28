package de.dreier.mytargets.models;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;

public class Circle {
    private final float density;
    private final int target;
    private final Paint circleColorP;
    private Paint mTextPaint;

    public Circle(float density, int target) {
        this.density = density;
        this.target = target;

        // Set up default Paint object
        circleColorP = new Paint();
        circleColorP.setAntiAlias(true);

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void draw(Canvas can, float x, float y, int zone, int rad, boolean comment) {
        int font_size = (int) (1.2323f * rad + 0.7953f);
        int colorInd;
        if (zone > -1) {
            colorInd = Target.target_rounds[target][zone];
        } else {
            colorInd = 3;
        }

        circleColorP.setStrokeWidth(2 * density);
        circleColorP.setStyle(Paint.Style.FILL_AND_STROKE);
        circleColorP.setColor(Target.rectColor[colorInd]);
        can.drawCircle(x, y, rad * density, circleColorP);
        circleColorP.setStyle(Paint.Style.STROKE);
        circleColorP.setColor(Target.circleStrokeColor[colorInd]);
        can.drawCircle(x, y, rad * density, circleColorP);

        mTextPaint.setTextSize(22 * density);
        mTextPaint.setColor(colorInd == 0 || colorInd == 4 ? Color.BLACK : Color.WHITE);
        mTextPaint.setTextSize(font_size * density);
        can.drawText(Target.getStringByZone(target, zone), x, y + font_size * 7 * density / 22.0f, mTextPaint);

        if (comment) {
            circleColorP.setStyle(Paint.Style.FILL_AND_STROKE);
            circleColorP.setColor(0xFFFF0000);
            can.drawCircle(x + rad * 0.8f * density, y - rad * 0.8f * density, 8 * density, circleColorP);
            mTextPaint.setColor(0xFFFFFFFF);
            can.drawText("+", x + rad * 0.8f * density, y - rad * 0.4f * density, mTextPaint);
        }
    }
}
