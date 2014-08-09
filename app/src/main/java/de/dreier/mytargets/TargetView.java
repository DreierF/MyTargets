package de.dreier.mytargets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


/**
 * TODO: document your custom view class.
 */
public class TargetView extends View implements View.OnTouchListener {
    public static final int[] highlightColor = {
            0xFFFCEA0F, // Gelb
            0xFFE30513, // Rot
            0xFF1D70B7, //Blau
            0xFF050505, // Schwarz
            Color.WHITE,  // Weiß
            0xFF1C1C1B // Mistake
    };

    public static final int[] circleStrokeColor = {
            0xFFFCEA0F, // Gelb
            0xFFE30513, // Rot
            0xFF1D70B7, //Blau
            0xFF050505, // Schwarz
            0xFF1C1C1B, // Weiß wird schwarz dargestellt
            0xFF1C1C1B, // Mistake
    };

    public static final int[] rectColor = {
            0xFFFCEA0F, // Gelb
            0xFFE30513, // Rot
            0xFF1D70B7, //Blau
            0xFF1C1C1B, // Schwarz
            Color.WHITE, // Weiß
            0xFF1C1C1B // Mistake
    };

    public static final int[] grayColor = {
            0xFF7a7439, // Gelb
            0xFF7a2621, // Rot
            0xFF234466, //Blau
            0xFF1C1C1B, // Schwarz
            0xFF7d7a80 // Weiß
    };

    public static final int[][] target_rounds = {
            {0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4}, //WA
            {0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4},
            {0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4},
            {0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4},
            {0, 0, 0, 1, 1, 2}, // WA Spot
            {0, 0, 0, 1, 1, 2},
            {0, 0, 0, 1, 1, 2},
            {0, 0, 3, 3, 3, 3}, // WA Field
            {4, 4, 3, 3, 3, 3},  //DFBV Spiegel
            {4, 4, 3} //DFBV Spiegel Spot
    };

    public static final int[][] target_points = {
            {10, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1}, //WA
            {10, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1},
            {10, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1},
            {10, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1},
            {10, 10, 9, 8, 7, 6}, // WA Spot
            {10, 10, 9, 8, 7, 6},
            {10, 10, 9, 8, 7, 6},
            {5, 5, 4, 3, 2, 1}, // WA Field
            {5, 5, 4, 3, 2, 1},  //DFBV Spiegel
            {5, 5, 4} //DFBV Spiegel Spot
    };

    private int radius, midX, midY;
    private int contentWidth, contentHeight;

    private TextPaint mTextPaint;
    private Paint thinBlackBorder, drawColorP, rectColorP, circleColorP;

    private int[] points_zone = {-2,-2,-2};
    private int currentArrow = 0, lastSetArrow = -1;//*/

    private OnTargetSetListener setListener = null;
    private int ppp = 3;
    private int targetRound = 0;
    private float density;
    private float resultX1, resultX2, spacePerResult;

    public void reset() {
        currentArrow = 0;
        lastSetArrow = -1;
        for(int i=0;i<3;i++) points_zone[i] = -2;
        invalidate();
    }

    public void setPPP(int num) {
        ppp = num;
        points_zone = new int[num];
        for(int i=0;i<num;i++)
            points_zone[i] = -2;
    }

    public void setTargetRound(int i) {
        targetRound = i;
    }

    public void setZones(int[] zones) {
        currentArrow = zones.length;
        lastSetArrow = zones.length;
        this.points_zone = zones;
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
        density = getResources().getDisplayMetrics().density;
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(22*density);
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
        circleColorP.setStrokeWidth(2*density);

        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int[] target = target_rounds[targetRound];
        int zones = target.length;
        int curZone;
        if(currentArrow<ppp)
            curZone = points_zone[currentArrow];
        else
            curZone = -2;
        for(int i=zones; i>0; i--) {
            // Select colors to draw with
            drawColorP.setColor((i==curZone+1?highlightColor:grayColor)[target[i-1]]);
            rectColorP.setColor(rectColor[target[i-1]]);

            // Zeichne einen Ring mit Trennlinie
            float rad = (radius * i)/(float)zones;
            canvas.drawCircle(midX, midY, rad, drawColorP);
            canvas.drawCircle(midX, midY, rad, thinBlackBorder);

            // Zeichne den Indikator rechts
            int X1 = (int)(contentWidth - 50*density);
            int X2 = (int)(contentWidth - 20*density);
            int Y1 = contentHeight * (i - 1) / (zones+1);
            int Y2 = contentHeight * i / (zones+1);
            canvas.drawRect(X1, Y1, X2, Y2, rectColorP);
            canvas.drawRect(X1, Y1, X2, Y2, thinBlackBorder);
        }

        if(curZone>=-1) {
            float circleY;
            if (curZone == -1) {
                circleY = midY + radius;
            } else {
                circleY = midY + radius * (curZone+1) / (float)zones;
            }
            drawCircle(canvas, midX + radius + 27*density, circleY, curZone);
            if(curZone>-1)
                canvas.drawLine(midX, circleY, midX + radius + 10*density, circleY, circleColorP);
        }

        for(int i=0;i<=lastSetArrow && i<ppp;i++) {
            drawCircle(canvas, midX + spacePerResult*i+resultX1+(spacePerResult/2.0f), midY+radius*1.3f, points_zone[i]);
        }
    }

    public void saveState(Bundle b) {
        b.putIntArray("points_zone", points_zone);
        b.putInt("currentArrow", currentArrow);
        b.putInt("lastSetArrow", lastSetArrow);
        b.putInt("ppp",ppp);
        b.putInt("target_round",targetRound);
    }

    public void restoreState(Bundle b) {
        points_zone = b.getIntArray("points_zone");
        currentArrow = b.getInt("currentArrow");
        lastSetArrow = b.getInt("lastSetArrow");
        ppp = b.getInt("ppp");
        targetRound = b.getInt("target_round");
    }

    private void drawCircle(Canvas can, float x, float y, int zone) {
        int points, colorInd;
        if(zone>-1) {
            points = target_points[targetRound][zone];
            colorInd = target_rounds[targetRound][zone];
        } else {
            points = 0;
            colorInd = 3;
        }
        circleColorP.setStyle(Paint.Style.FILL_AND_STROKE);
        circleColorP.setColor(rectColor[colorInd]);
        can.drawCircle(x, y, 17*density, circleColorP);
        circleColorP.setStyle(Paint.Style.STROKE);
        circleColorP.setColor(circleStrokeColor[colorInd]);
        can.drawCircle(x, y, 17*density, circleColorP);
        mTextPaint.setColor(colorInd==0||colorInd==4 ? Color.BLACK : Color.WHITE);
        can.drawText(points==0?"M":(zone==0 && targetRound<4?"X":"" + points), x, y + 7*density, mTextPaint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        contentWidth = getWidth();
        contentHeight = getHeight();

        int bounds = Math.min(contentWidth*2,contentHeight);

        radius = (int)(bounds/2.0-50*density);
        midX = 0;
        midY = radius+(int)(10*density);
        resultX1 = 30*density;
        resultX2 = contentWidth-80*density;
        spacePerResult = (resultX2-resultX1)/ppp;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();

        if(x>resultX1 && x<resultX2 && y> midY+radius*1.3f-20*density && y<midY+radius*1.3f+20*density) {
            int arrow = (int)((x-resultX1)/spacePerResult);
            if(arrow<ppp && points_zone[arrow]>=0) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    currentArrow = arrow;
                    invalidate();
                }
                return true;
            }
        }

        int zone, ringe = target_rounds[targetRound].length;
        if(x>midX + radius + 30*density) { // Handle selection with indikator
            zone = (int) (y * (ringe+1) / (float) contentHeight);
        } else { // Handle with target
            double xDiff = x-midX;
            double yDiff = y-midY;
            zone = (int)(Math.sqrt(xDiff*xDiff+yDiff*yDiff)*ringe/ (float) radius);
        }

        // Correct points_zone
        if (zone < -1 || zone >= ringe)
            zone = -1;

        if (currentArrow < ppp)
            points_zone[currentArrow] = zone;

        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            //go to next page
            if(currentArrow==lastSetArrow+1) {
                lastSetArrow++;
                currentArrow++;
            } else if(lastSetArrow<ppp) {
                currentArrow = lastSetArrow+1;
            }

            if (lastSetArrow+1 >= ppp && setListener != null)
                setListener.OnTargetSet(points_zone);
        }

        invalidate();
        return true;
    }

    public void setOnTargetSetListener(OnTargetSetListener listener) {
        setListener = listener;
    }
}
