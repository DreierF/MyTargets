package de.dreier.mytargets.shared.analysis.aggregation;

import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.drawable.TargetImpactDrawable;

public class ShotAverageDrawable extends TargetImpactDrawable {
    public ShotAverageDrawable(Target target) {
        super(target);
    }

    /*
        if (midpointsDirty) {
            updateMidpoints();
        }
        int spots = targetModel.getFaceCount();
        for (int i = 0; i < spots; i++) {
            if (midpoints[i].shouldDraw()) {
                targetDrawable.drawArrowAvg(canvas, midpoints[i].getX(), midpoints[i].getY());
            }
        }


    public void drawArrowAvg(Canvas canvas, float x, float y) {
        int zone = getZoneFromPoint(x, y);
        int color = model.getContrastColor(zone);
        paintStroke.setColor(color);
        paintStroke.setStrokeWidth(0.0015f);
        float radius = arrowRadius;
        canvas.drawCircle(x, y, radius, paintStroke);
        canvas.drawLine(x, y + radius, x, y - radius, paintStroke);
        canvas.drawLine(x - radius, y, x + radius, y, paintStroke);
    }
        */
   /* private final Average mAverage;
    private boolean mDrawInner = false;
    private boolean mDrawOuter = false;
    private boolean mDrawStdDev = false;
    private boolean mDrawSubTitle = false;
    private boolean mDrawSymbol = false;
    private boolean mDrawTitle = false;
    private final Path mInnerDrawPath = new Path();
    private final Paint mInnerPaint = new Paint(1);
    private final Path mInnerPath = new Path();
    private final Path mOuterDrawPath = new Path();
    private final Paint mOuterPaint = new Paint(1);
    private final Path mOuterPath = new Path();
    private final Path mStdDevDrawPath = new Path();
    private final Paint mStdDevPaint = new Paint(1);
    private final Path mStdDevPath = new Path();
    private String mSubTitle = null;
    private final Paint mSubTitlePaint = new Paint(1);
    private final float[] mSubTitlePos_b = new float[2];
    private final float[] mSubTitlePos_v = new float[2];
    private float mSubTitleVOffset;
    private final Path mSymbolDrawPath = new Path();
    private final Paint mSymbolPaintI = new Paint(1);
    private final Paint mSymbolPaintO = new Paint(1);
    private final Path mSymbolPath = new Path();
    private String mTitle = null;
    private final Paint mTitlePaint = new Paint(1);
    private final float[] mTitlePos_b = new float[2];
    private final float[] mTitlePos_v = new float[2];
    private float mTitleVOffset;

    public ShotAverageDrawable(Context context, Average var2) {
        super(context);
        this.mAverage = var2;
        this.setDefaults();
        this.clear();
    }

    private void setDefaults() {
        this.mTitlePaint.setARGB(255, 255, 255, 255);
        this.mTitlePaint.setTextAlign(Align.CENTER);
        this.mSubTitlePaint.setARGB(255, 255, 255, 255);
        this.mSubTitlePaint.setTextAlign(Align.CENTER);
        this.setTitleSize(this.mContext.getResources().getDimension(2131361904), this.mContext.getResources().getDimension(2131361903));
        this.mSymbolPaintO.setARGB(255, 0, 0, 0);
        this.mSymbolPaintO.setStyle(Style.STROKE);
        this.mSymbolPaintO.setStrokeWidth(5.0F);
        this.mSymbolPaintI.setARGB(255, 0, 0, 0);
        this.mSymbolPaintI.setStyle(Style.STROKE);
        this.mSymbolPaintI.setStrokeWidth(3.0F);
        this.mInnerPaint.setARGB(255, 0, 0, 0);
        this.mInnerPaint.setStyle(Style.FILL);
        this.mStdDevPaint.setARGB(255, 0, 0, 0);
        this.mStdDevPaint.setStyle(Style.FILL);
        this.mStdDevPaint.setStrokeWidth(4.0F);
        this.mOuterPaint.setARGB(255, 0, 0, 0);
        this.mOuterPaint.setStyle(Style.FILL);
    }

    public void clear() {
        this.mSymbolPath.rewind();
        this.mInnerPath.rewind();
        this.mStdDevPath.rewind();
        this.mOuterPath.rewind();
    }

    public void drawInner(boolean var1) {
        this.mDrawInner = var1;
    }

    public void drawOuter(boolean var1) {
        this.mDrawOuter = var1;
    }

    public void drawStdDev(boolean var1) {
        this.mDrawStdDev = var1;
    }

    public void drawSubTitle(boolean var1) {
        this.mDrawSubTitle = var1;
    }

    public void drawSymbol(boolean var1) {
        this.mDrawSymbol = var1;
    }

    public void drawTitle(boolean var1) {
        this.mDrawTitle = var1;
    }

    public void onDrawableDraw(Canvas var1, Mode var2) {
        if(this.mAverage.size() >= 3) {
            if(this.mDrawOuter) {
                this.mOuterDrawPath.set(this.mOuterPath);
                this.mOuterDrawPath.transform(this.mMatrix_b_v);
                var1.drawPath(this.mOuterDrawPath, this.mOuterPaint);
            }

            if(this.mDrawStdDev) {
                this.mStdDevDrawPath.set(this.mStdDevPath);
                this.mStdDevDrawPath.transform(this.mMatrix_b_v);
                var1.drawPath(this.mStdDevDrawPath, this.mStdDevPaint);
            }

            if(this.mDrawInner) {
                this.mInnerDrawPath.set(this.mInnerPath);
                this.mInnerDrawPath.transform(this.mMatrix_b_v);
                var1.drawPath(this.mInnerDrawPath, this.mInnerPaint);
            }

            if(this.mDrawSymbol) {
                this.mSymbolDrawPath.set(this.mSymbolPath);
                this.mSymbolDrawPath.transform(this.mMatrix_b_v);
                var1.drawPath(this.mSymbolDrawPath, this.mSymbolPaintO);
                var1.drawPath(this.mSymbolDrawPath, this.mSymbolPaintI);
            }
        }

        if(this.mAverage.size() >= 1 && this.mDrawTitle && this.mTitle != null && this.mTitle.length() > 0) {
            this.mMatrix_b_v.mapPoints(this.mTitlePos_v, this.mTitlePos_b);
            var1.drawText(this.mTitle, this.mTitlePos_v[0], this.mTitlePos_v[1] + this.mTitleVOffset, this.mTitlePaint);
        }

        if(this.mAverage.size() >= 1 && this.mDrawSubTitle && this.mSubTitle != null && this.mSubTitle.length() > 0) {
            this.mMatrix_b_v.mapPoints(this.mSubTitlePos_v, this.mSubTitlePos_b);
            var1.drawText(this.mSubTitle, this.mSubTitlePos_v[0], this.mSubTitlePos_v[1] + this.mSubTitleVOffset, this.mSubTitlePaint);
        }

    }

    public void onDrawablePrepareDraw() {
        PointF var4 = this.mAverage.getAverage();
        RectF var5 = this.mAverage.getNonUniformStdDev();
        float var2 = var5.top;
        float var1 = var2;
        if(var5.bottom > var2) {
            var1 = var5.bottom;
        }

        var2 = var1;
        if(var5.left > var1) {
            var2 = var5.left;
        }

        float var3 = var2;
        if(var5.right > var2) {
            var3 = var5.right;
        }

        var2 = var5.top;
        var1 = var2;
        if(var5.bottom < var2) {
            var1 = var5.bottom;
        }

        var2 = var1;
        if(var5.left < var1) {
            var2 = var5.left;
        }

        var1 = var2;
        if(var5.right < var2) {
            var1 = var5.right;
        }

        if(this.mDrawOuter) {
            this.mOuterPath.rewind();
            this.mOuterPath.addCircle(var4.x, var4.y, var3, Direction.CW);
            this.mOuterPath.transform(this.mMatrix_s_b);
        }

        if(this.mDrawStdDev) {
            this.mStdDevPath.rewind();
            this.mStdDevPath.arcTo(new RectF(var4.x - var5.right, var4.y - var5.bottom, var4.x + var5.right, var4.y + var5.bottom), 270.0F, 90.0F, true);
            this.mStdDevPath.arcTo(new RectF(var4.x - var5.right, var4.y - var5.top, var4.x + var5.right, var4.y + var5.top), 0.0F, 90.0F);
            this.mStdDevPath.arcTo(new RectF(var4.x - var5.left, var4.y - var5.top, var4.x + var5.left, var4.y + var5.top), 90.0F, 90.0F);
            this.mStdDevPath.arcTo(new RectF(var4.x - var5.left, var4.y - var5.bottom, var4.x + var5.left, var4.y + var5.bottom), 180.0F, 90.0F);
            this.mStdDevPath.transform(this.mMatrix_s_b);
        }

        if(this.mDrawInner) {
            this.mInnerPath.rewind();
            this.mInnerPath.addCircle(var4.x, var4.y, var1, Direction.CW);
            this.mInnerPath.transform(this.mMatrix_s_b);
        }

        if(this.mDrawSymbol) {
            var2 = var1 / 4.0F;
            this.mSymbolPath.rewind();
            this.mSymbolPath.addCircle(var4.x, var4.y, var1 / 2.0F, Direction.CW);
            this.mSymbolPath.moveTo(var4.x - var5.left, var4.y - var2);
            this.mSymbolPath.lineTo(var4.x - var5.left, var4.y + var2);
            this.mSymbolPath.moveTo(var4.x - var5.left, var4.y);
            this.mSymbolPath.lineTo(var4.x + var5.right, var4.y);
            this.mSymbolPath.moveTo(var4.x + var5.right, var4.y - var2);
            this.mSymbolPath.lineTo(var4.x + var5.right, var4.y + var2);
            this.mSymbolPath.moveTo(var4.x - var2, var4.y + var5.top);
            this.mSymbolPath.lineTo(var4.x + var2, var4.y + var5.top);
            this.mSymbolPath.moveTo(var4.x, var4.y + var5.top);
            this.mSymbolPath.lineTo(var4.x, var4.y - var5.bottom);
            this.mSymbolPath.moveTo(var4.x - var2, var4.y - var5.bottom);
            this.mSymbolPath.lineTo(var4.x + var2, var4.y - var5.bottom);
            this.mSymbolPath.transform(this.mMatrix_s_b);
        }

        if(this.mDrawTitle && this.mTitle != null && this.mTitle.length() > 0) {
            var4 = this.mAverage.getAverage();
            this.mMatrix_s_b.mapPoints(this.mTitlePos_b, new float[]{var4.x, var4.y});
        }

        if(this.mDrawSubTitle && this.mSubTitle != null && this.mSubTitle.length() > 0) {
            var4 = this.mAverage.getAverage();
            this.mMatrix_s_b.mapPoints(this.mSubTitlePos_b, new float[]{var4.x, var4.y});
        }

    }

    public void setColor(int var1) {
        double[] var5 = new double[3];
        int var4 = Color.alpha(var1);
        ColorUtils.ColorToHSL(var1, var5);
        double var2 = var5[2];
        var5[2] = (1.0D - var2) * 0.7D + var2;
        this.mInnerPaint.setColor(ColorUtils.HSLToColor((int)((float)var4 * 0.8F), var5));
        this.mSymbolPaintI.setColor(ColorUtils.HSLToColor(var5));
        if(this.mStdDevPaint.getStyle() == Style.STROKE) {
            this.mStdDevPaint.setColor(ColorUtils.HSLToColor(var5));
        } else {
            this.mStdDevPaint.setColor(var1);
        }

        var5[2] = var2 * 0.7D;
        this.mOuterPaint.setColor(ColorUtils.HSLToColor((int)((float)var4 * 0.6F), var5));
        var5[2] = (1.0D - var2) * 0.9D + var2;
        this.mTitlePaint.setColor(ColorUtils.HSLToColor(var5));
        this.mSubTitlePaint.setColor(ColorUtils.HSLToColor(var5));
    }

    public void setStdDevPaintStyle(Style var1) {
        this.mStdDevPaint.setStyle(var1);
    }

    public void setSubTitle(String var1) {
        this.mSubTitle = var1;
    }

    public void setTitle(String var1) {
        this.mTitle = var1;
    }

    public void setTitleSize(float var1, float var2) {
        this.mTitlePaint.setTextSize(var1);
        Rect var3 = new Rect();
        this.mTitlePaint.getTextBounds("XX", 0, 2, var3);
        this.mTitleVOffset = (float)var3.height() / 2.0F;
        this.mSubTitlePaint.setTextSize(var2);
        Rect var4 = new Rect();
        this.mSubTitlePaint.getTextBounds("XX", 0, 2, var4);
        this.mSubTitleVOffset = (float)var3.height() + (float)var4.height() / 1.9F;
    }*/
}