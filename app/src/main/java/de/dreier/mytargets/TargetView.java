package de.dreier.mytargets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * TODO: document your custom view class.
 */
public class TargetView extends View implements View.OnTouchListener {
    public static int[] highlightColor = {
            0xFFFCEA0F, 0xFFFCEA0F, // Gelb
            0xFFE30513, 0xFFE30513, // Rot
            0xFF1D70B7, 0xFF1D70B7, //Blau
            0xFF050505, 0xFF050505, // Schwarz
            Color.WHITE, Color.WHITE, // Weiß
            0xFF1C1C1B, 0xFF1C1C1B // Mistake
    };

    public static int[] circleStrokeColor = {
            0xFFFCEA0F, 0xFFFCEA0F, // Gelb
            0xFFE30513, 0xFFE30513, // Rot
            0xFF1D70B7, 0xFF1D70B7, //Blau
            0xFF050505, 0xFF050505, // Schwarz
            0xFF1C1C1B, 0xFF1C1C1B, // Weiß wird schwarz dargestellt
            0xFF1C1C1B, 0xFF1C1C1B // Mistake
    };

    public static int[] rectColor = {
            0xFFFCEA0F, 0xFFFCEA0F, // Gelb
            0xFFE30513, 0xFFE30513, // Rot
            0xFF1D70B7, 0xFF1D70B7, //Blau
            0xFF1C1C1B, 0xFF1C1C1B, // Schwarz
            Color.WHITE, Color.WHITE, // Weiß
            0xFF1C1C1B, 0xFF1C1C1B // Mistake
    };

    public static int[] grayColor = {
            0xFF7a7439, 0xFF7a7439, // Gelb
            0xFF7a2621, 0xFF7a2621, // Rot
            0xFF234466, 0xFF234466, //Blau
            0xFF1C1C1B, 0xFF1C1C1B, // Schwarz
            0xFF7d7a80, 0xFF7d7a80 // Weiß
    };

    private int radius, midX, midY;
    private int contentWidth, contentHeight;

    private TextPaint mTextPaint;
    private Paint thinBlackBorder, drawColorP, rectColorP, circleColorP;

    private int[] points = {-2,-2,-2};
    private int currentArrow = 0, lastSetArrow = -1;
    private OnTargetSetListener setListener=null;


    public void reset() {
        currentArrow = 0;
        lastSetArrow = -1;
        for(int i=0;i<3;i++) points[i] = -2;
        invalidate();
    }

    public void setPoints(int[] points) {
        currentArrow = 0;
        lastSetArrow = -1;
        this.points = points;
        invalidate();
    }

    public interface OnTargetSetListener {
        public void OnTargetSet(int[] points);
    };

    public TargetView(Context context) {
        super(context);
        init(null, 0);
    }

    public TargetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TargetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(60);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        thinBlackBorder = new Paint();
        thinBlackBorder.setColor(0xFF1C1C1B);
        thinBlackBorder.setAntiAlias(true);
        thinBlackBorder.setStyle(Paint.Style.STROKE);

        drawColorP = new Paint();
        drawColorP.setAntiAlias(true);

        rectColorP = new Paint();
        rectColorP.setAntiAlias(true);

        circleColorP = new Paint();
        circleColorP.setAntiAlias(true);
        circleColorP.setStrokeWidth(6);

        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for(int i=10;i>0;i--) {
            boolean isSelected = false;
            if(currentArrow<3)
                isSelected = i==11-points[currentArrow];
            drawColorP.setColor((isSelected?highlightColor:grayColor)[i-1]);
            rectColorP.setColor(rectColor[i-1]);

            // Zeichne einen Ring
            canvas.drawCircle(midX,midY, radius *i*0.1f, drawColorP);

            // Zeichne die Trennlinie zwischen den Ringen
            canvas.drawCircle(midX, midY, radius * i * 0.1f, thinBlackBorder);

            // Zeichne den Indikator rechts
            canvas.drawRect(midX + radius + 180, contentHeight * (i - 1) / 11, midX + radius + 230, contentHeight * i / 11, rectColorP);
            canvas.drawRect(midX + radius + 180, contentHeight * (i - 1) / 11, midX + radius + 230, contentHeight * i / 11, thinBlackBorder);
        }

        if(currentArrow<3 && points[currentArrow]>=0) {
            float circleY;
            int i = 11 - points[currentArrow];
            circleColorP.setColor(circleStrokeColor[i - 1]);
            if (i == 11) {
                circleY = midY + radius;
            } else {
                circleY = midY + radius * i * 0.1f;
                canvas.drawLine(midX, circleY, midX + radius + 40, circleY, circleColorP);
            }
            drawCircle(canvas, midX + radius + 80, circleY, points[currentArrow]);
        }
        if(lastSetArrow>=0) {
            drawCircle(canvas, midX + 200, midY+radius*1.15f, points[0]);
        }
        if(lastSetArrow>=1) {
            drawCircle(canvas, midX + 350, midY+radius*1.15f, points[1]);
        }
        if(lastSetArrow>=2) {
            drawCircle(canvas, midX + 500, midY+radius*1.15f, points[2]);
        }
    }

    public void saveState(Bundle b) {
        b.putIntArray("points",points);
        b.putInt("currentArrow",currentArrow);
        b.putInt("lastSetArrow",lastSetArrow);
    }

    public void restoreState(Bundle b) {
        points = b.getIntArray("points");
        currentArrow = b.getInt("currentArrow");
        lastSetArrow = b.getInt("lastSetArrow");
    }

    private void drawCircle(Canvas can, float x, float y, int points) {
        int i=10-points;
        circleColorP.setStyle(Paint.Style.FILL_AND_STROKE);
        circleColorP.setColor(rectColor[i]);
        can.drawCircle(x, y, 50, circleColorP);
        circleColorP.setStyle(Paint.Style.STROKE);
        circleColorP.setColor(circleStrokeColor[i]);
        can.drawCircle(x, y, 50, circleColorP);
        mTextPaint.setColor(i>1&&i<8||i>9 ? Color.WHITE : Color.BLACK);
        can.drawText(points==0?"M":"" + points, x, y + 20, mTextPaint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        contentWidth = getWidth();
        contentHeight = getHeight();

        int bounds = Math.min(contentWidth*2,contentHeight);

        radius = bounds/2-120;
        midX = 0;
        midY = radius+30;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();

        if(x>midX+100 && x<midX+700 && y> midY+radius*1.15f-50 && y<midY+radius*1.15f+50) {
            int arrow = (int)((x-midX-100)/200.0);
            if(arrow<3 && points[arrow]>=0) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    currentArrow = arrow;
                    invalidate();
                }
                return true;
            }
        }

        int point;
        if(x>midX + radius + 100) { // Handle selection with indikator
            point = 10 - (int) (y * 11.0 / (float) contentHeight);
        } else { // Handle with target
            double xDiff = x-midX;
            double yDiff = y-midY;
            point = (int)(11.0-(Math.sqrt(xDiff*xDiff+yDiff*yDiff)*10.0/ (float) radius));
        }

        // Correct points
        if (point < 0)
            point = 0;

        if (point > 10)
            point = 10;

        if (currentArrow < 3)
            points[currentArrow] = point;

        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            //go to next page
            if(currentArrow==lastSetArrow+1) {
                lastSetArrow++;
                currentArrow++;
            } else if(lastSetArrow<3) {
                currentArrow = lastSetArrow+1;
            }


            if (currentArrow == 3 && setListener != null)
                setListener.OnTargetSet(points);
        }

        invalidate();
        return true;
    }

    public void setOnTargetSetListener(OnTargetSetListener listener) {
        setListener = listener;
    }
}
