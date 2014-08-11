package de.dreier.mytargets;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

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
    private int currentArrow = 0, lastSetArrow = -1;

    private OnTargetSetListener setListener = null;
    private int ppp = 3;
    private int targetRound = 0;
    private float density;
    private float resultX1, resultX2, spacePerResult;
    private float mCurAnimationProgress;
    private int mCurSelecting = -1;
    private float mInFromX, mInFromY;
    private float mInToX, mInToY;
    private float mOutFromX, mOutFromY;
    private float mOutToX, mOutToY;
    private int mInZone, mOutZone;
    private int mZoneCount;

    public void reset() {
        mCurSelecting = -1;
        currentArrow = 0;
        lastSetArrow = -1;
        for(int i=0;i<3;i++) points_zone[i] = -2;
        invalidate();
    }

    public void setPPP(int num) {
        mCurSelecting = -1;
        ppp = num;
        points_zone = new int[num];
        for(int i=0;i<num;i++)
            points_zone[i] = -2;
    }

    public void setTargetRound(int i) {
        targetRound = i;
        mZoneCount = target_rounds[targetRound].length;
    }

    public void setZones(int[] zones) {
        currentArrow = zones.length;
        lastSetArrow = zones.length;
        points_zone = zones;
        invalidate();
    }

    public interface OnTargetSetListener {
        public void OnTargetSet(int[] points);
    }

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

        final int[] target = target_rounds[targetRound];
        int curZone;
        if(currentArrow<ppp)
            curZone = points_zone[currentArrow];
        else
            curZone = -2;

        // Draw target with highlighted zone
        for(int i=mZoneCount; i>0; i--) {
            // Select colors to draw with
            drawColorP.setColor(getZoneColor(i-1));
            rectColorP.setColor(rectColor[target[i-1]]);

            // Zeichne einen Ring mit Trennlinie
            float rad = (radius * i)/(float)mZoneCount;
            canvas.drawCircle(midX, midY, rad, drawColorP);
            canvas.drawCircle(midX, midY, rad, thinBlackBorder);

            // Zeichne den Indikator rechts
            int X1 = (int)(contentWidth - 50*density);
            int X2 = (int)(contentWidth - 20*density);
            int Y1 = contentHeight * (i - 1) / (mZoneCount+1);
            int Y2 = contentHeight * i / (mZoneCount+1);
            canvas.drawRect(X1, Y1, X2, Y2, rectColorP);
            canvas.drawRect(X1, Y1, X2, Y2, thinBlackBorder);
        }

        // Draw selection for currentArrow
        if(curZone>=-1 && mCurSelecting==-1) {
            float circleY;
            if (curZone == -1) {
                circleY = midY + radius;
            } else {
                circleY = midY + radius * (curZone+1) / (float)mZoneCount;
            }
            drawCircle(canvas, midX + radius + 27*density, circleY, curZone);
            if(curZone>-1)
                canvas.drawLine(midX, circleY, midX + radius + 10*density, circleY, circleColorP);
        }

        // Draw all points of this passe at the bottom
        for(int i=0;i<=lastSetArrow && i<ppp;i++) {
            if(currentArrow!=i && mCurSelecting!=i)
                drawCircle(canvas, midX + spacePerResult*i+resultX1+(spacePerResult/2.0f), midY+radius*1.3f, points_zone[i]);
        }

        // Draw animation
        if(mCurSelecting!=-1) {
            // Draw outgoing object
            if(mOutZone>=-1) {
                drawCircle(canvas, mOutFromX + (mOutToX - mOutFromX) * mCurAnimationProgress, mOutFromY + (mOutToY - mOutFromY) * mCurAnimationProgress, mOutZone);
                if(mOutZone>-1) {
                    int colorInd = mOutZone>-1?target_rounds[targetRound][mOutZone]:3;
                    int color = circleStrokeColor[colorInd];
                    circleColorP.setColor(animateColor(color,color&0xFFFFFF));
                    canvas.drawLine(midX, mOutFromY, midX + radius + 10 * density, mOutFromY, circleColorP);
                }
            }
            // Draw incoming object
            if(mInZone>=-1) {
                drawCircle(canvas, mInFromX + (mInToX - mInFromX) * mCurAnimationProgress, mInFromY + (mInToY - mInFromY) * mCurAnimationProgress, mInZone);
                if(mInZone>-1) {
                    int colorInd = mInZone>-1?target_rounds[targetRound][mInZone]:3;
                    int color = circleStrokeColor[colorInd];
                    circleColorP.setColor(animateColor(color&0xFFFFFF,color));
                    canvas.drawLine(midX, mInToY, midX + radius + 10 * density, mInToY, circleColorP);
                }
            }
        }
    }

    private void initAnimationPositions() {
        //Calculate positions of outgoing object
        if(currentArrow<ppp && points_zone[currentArrow]>=-1) {
            mOutZone = points_zone[currentArrow];
            mOutToX = midX + spacePerResult * currentArrow + resultX1 + (spacePerResult / 2.0f);
            mOutToY = midY + radius * 1.3f;
            mOutFromX = midX + radius + 27 * density;
            if (mOutZone == -1) {
                mOutFromY = midY + radius;
            } else {
                mOutFromY = midY + radius * (mOutZone + 1) / (float)mZoneCount;
            }
        } else {
            mOutZone = -2;
        }

        // Calculate positions for incoming object
        if(mCurSelecting<ppp && points_zone[mCurSelecting]>=-1) {
            mInZone = points_zone[mCurSelecting];
            mInFromX = midX + spacePerResult * mCurSelecting + resultX1 + (spacePerResult / 2.0f);
            mInFromY = midY + radius * 1.3f;
            mInToX = midX + radius + 27 * density;
            if (mInZone == -1) {
                mInToY = midY + radius;
            } else {
                mInToY = midY + radius * (mInZone + 1) / (float)mZoneCount;
            }
        } else {
            mInZone = -2;
        }
    }

    private int getZoneColor(int zone) {
        final int[] target = target_rounds[targetRound];
        final int curZone;
        if(currentArrow<ppp)
            curZone = points_zone[currentArrow];
        else
            curZone = -2;

        if(mCurSelecting!=-1 && zone==mInZone) {
            return animateColor(grayColor[target[zone]],highlightColor[target[zone]]);
        } else if(mCurSelecting!=-1 && zone==mOutZone) {
            return animateColor(highlightColor[target[zone]],grayColor[target[zone]]);
        } else {
            return (zone==curZone?highlightColor:grayColor)[target[zone]];
        }
    }

    public int animateColor(int from, int to) {
        final int fa = (from >> 24) & 0xFF;
        final int fr = (from >> 16) & 0xFF;
        final int fg = (from >> 8) & 0xFF;
        final int fb = (from >> 0) & 0xFF;
        final int da = ((to >> 24) & 0xFF) - fa;
        final int dr = ((to >> 16) & 0xFF) - fr;
        final int dg = ((to >> 8) & 0xFF) - fg;
        final int db = ((to >> 0) & 0xFF) - fb;
        final int ra = (int)(fa+da*mCurAnimationProgress);
        final int rr = (int)(fr+dr*mCurAnimationProgress);
        final int rg = (int)(fg+dg*mCurAnimationProgress);
        final int rb = (int)(fb+db*mCurAnimationProgress);
        return (ra<<24)|(rr<<16)|(rg<<8)|rb;
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
        can.drawCircle(x, y, 17 * density, circleColorP);
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
            if(arrow<ppp && points_zone[arrow]>=-1 && arrow!=currentArrow) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    animateSelectCircle(arrow);
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

        if (currentArrow < ppp && points_zone[currentArrow]!=zone) {
            points_zone[currentArrow] = zone;
            invalidate();
        }

        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            //go to next page
            if(currentArrow==lastSetArrow+1)
                lastSetArrow++;

            animateSelectCircle(lastSetArrow+1);

            if (lastSetArrow+1 >= ppp && setListener != null)
                setListener.OnTargetSet(points_zone);

            return true;
        }
        return true;
    }

    private void animateSelectCircle(final int i) {
        mCurSelecting = i;
        mCurAnimationProgress = 0;
        initAnimationPositions();

        final ValueAnimator moveAnimator = ValueAnimator.ofFloat(0, 1);
        moveAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        moveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCurAnimationProgress = (Float) valueAnimator.getAnimatedValue();
                if (mCurAnimationProgress == 1.0f) {
                    moveAnimator.cancel();
                    currentArrow = i;
                    mCurSelecting = -1;
                }
                invalidate();
            }
        });
        moveAnimator.setDuration(300);
        moveAnimator.start();
    }

    public void setOnTargetSetListener(OnTargetSetListener listener) {
        setListener = listener;
    }
}
