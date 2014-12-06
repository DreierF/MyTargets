package de.dreier.mytargets.models;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

public class LinearSeries {

    protected Paint mPaint = new Paint();

    private final SortedSet<LinearPoint> mPoints = Collections.synchronizedSortedSet(new TreeSet<LinearPoint>());

    private long mMinX = Long.MAX_VALUE;
    private long mMaxX = Long.MIN_VALUE;
    private long mMinY = Long.MAX_VALUE;
    private long mMaxY = Long.MIN_VALUE;

    private PointF mLastPoint;

    public void draw(Canvas canvas, Rect gridBounds, RectD valueBounds, float scaleX, float scaleY) {
        for (LinearPoint point : mPoints) {
            final float x = (float) (gridBounds.left + (scaleX * (point.getX() - valueBounds.left)));
            final float y = (float) (gridBounds.bottom + (scaleY * ((point.getY() == -1 ? valueBounds.bottom : point.getY())-valueBounds.bottom)));

            if (mLastPoint != null)
                canvas.drawLine(mLastPoint.x, mLastPoint.y, x, y, mPaint);
            else
                mLastPoint = new PointF();

            // This covers up the possible gaps between different lines
            canvas.drawCircle(x, y, (mPaint.getStrokeWidth() / 2) - 0.2F, mPaint);

            mLastPoint.set(x, y);
        }

        mLastPoint = null;
    }

    public LinearSeries() {
        mPaint.setAntiAlias(true);
    }

    public SortedSet<LinearPoint> getPoints() {
        return mPoints;
    }

    public void setPoints(Collection<? extends LinearPoint> points) {
        mPoints.clear();
        mPoints.addAll(points);

        recalculateRange();
    }

    public void addPoint(LinearPoint linearPoint) {
        extendRange(linearPoint.getX(), linearPoint.getY());
        mPoints.add(linearPoint);
    }

    public void setLineColor(int color) {
        mPaint.setColor(color);
    }

    public void setLineWidth(float width) {
        mPaint.setStrokeWidth(width);
    }

    private void resetRange() {
        mMinX = Long.MAX_VALUE;
        mMaxX = Long.MIN_VALUE;
        mMinY = Long.MAX_VALUE;
        mMaxY = Long.MIN_VALUE;
    }

    private void extendRange(long x, long y) {
        if (x < mMinX) mMinX = x;
        if (x > mMaxX) mMaxX = x;
        if (y < mMinY) mMinY = y;
        if (y > mMaxY) mMaxY = y;
    }

    protected void recalculateRange() {
        resetRange();

        for (LinearPoint point : mPoints)
            extendRange(point.getX(), point.getY());
    }

    public long getMinX() {
        return mMinX;
    }

    public long getMaxX() {
        return mMaxX;
    }

    public long getMinY() {
        return mMinY;
    }

    public long getMaxY() {
        return mMaxY;
    }

    public static class LinearPoint implements Comparable<LinearPoint> {
        private long mX;
        private long mY;

        public LinearPoint(long x, long y) {
            mX = x;
            mY = y;
        }

        public long getX() {
            return mX;
        }

        public long getY() {
            return mY;
        }

        @Override
        public int compareTo(LinearPoint another) {
            return Double.compare(mX, another.mX);
        }

        @Override
        public String toString() {
            return "(" + mX + ", " + mY + ")";
        }
    }
}