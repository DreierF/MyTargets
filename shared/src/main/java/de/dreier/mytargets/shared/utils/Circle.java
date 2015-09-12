package de.dreier.mytargets.shared.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;

import de.dreier.mytargets.shared.models.target.Target;

public class Circle {
    private final float density;
    private final Target target;
    private final Paint circleColorP;
    private Paint mTextPaint;

    public Circle(float density, Target target) {
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

    public void draw(Canvas can, float x, float y, int zone, int rad, boolean comment, int arrow, int number) {
        // Get color index and font size
        int font_size = (int) (1.2323f * rad + 0.7953f);

        // Draw the circles background
        circleColorP.setStrokeWidth(2 * density);
        circleColorP.setStyle(Paint.Style.FILL_AND_STROKE);
        circleColorP.setColor(target.getFillColor(zone));
        can.drawCircle(x, y, rad * density, circleColorP);

        // Draw the circles border
        circleColorP.setStyle(Paint.Style.STROKE);
        circleColorP.setColor(target.getStrokeColor(zone));
        can.drawCircle(x, y, rad * density, circleColorP);

        // Draw the text inside the circle
        mTextPaint.setTextSize(22 * density);
        mTextPaint.setColor(target.getTextColor(zone));
        mTextPaint.setTextSize(font_size * density);
        can.drawText(target.zoneToString(zone, arrow), x, y + font_size * 7 * density / 22.0f,
                mTextPaint);

        // Draw red circled + as indicator that this impact is commented
        if (comment) {
            circleColorP.setStyle(Paint.Style.FILL_AND_STROKE);
            circleColorP.setColor(0xFFFF0000);
            can.drawCircle(x + rad * 0.8f * density, y - rad * 0.8f * density, 8 * density,
                    circleColorP);
            mTextPaint.setColor(0xFFFFFFFF);
            can.drawText("+", x + rad * 0.8f * density, y - rad * 0.4f * density, mTextPaint);
        }
        if (number > -1) {
            circleColorP.setStyle(Paint.Style.FILL_AND_STROKE);
            circleColorP.setColor(0xFF333333);
            can.drawCircle(x + rad * 0.8f * density, y + rad * 0.8f * density, 8 * density,
                    circleColorP);
            mTextPaint.setTextSize(font_size * density*0.5f);
            mTextPaint.setColor(0xFFFFFFFF);
            can.drawText(String.valueOf(number), x + rad * 0.8f * density, y + rad * 1.05f * density,
                    mTextPaint);
        }
    }
}
