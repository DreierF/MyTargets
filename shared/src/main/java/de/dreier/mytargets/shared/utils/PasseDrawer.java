package de.dreier.mytargets.shared.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import org.parceler.Parcel;

import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.Target;

@Parcel
public class PasseDrawer {
    public static final int NO_SELECTION = -1;
    public static final int MAX_CIRCLE_SIZE = 17;
    private static final int MIN_PADDING = 2;

    Passe mPasse;
    int mPressed = NO_SELECTION;
    int mSelected = NO_SELECTION;
    Coordinate mSelectedPosition;
    int mSelectedRadius;

    // Animation
    Coordinate[] mOldCoordinate;
    float mCurAnimationProgress = -1;
    private transient Circle mCircle;
    private transient View mParent;
    private transient RectF mRect;
    private transient int mRadius;
    private transient Paint grayBackground;
    private transient float mDensity;
    private transient int mPPP;
    private transient int mShotsPerRow;
    private transient float mRowHeight;
    private transient float mColumnWidth;
    private transient ValueAnimator selectionAnimator;
    private transient int oldRadius;
    private transient int oldSelected;
    private transient int oldSelectedRadius;

    public void init(View parent, float density, Target target) {
        mParent = parent;
        mDensity = density;
        mCircle = new Circle(density, target);
        grayBackground = new Paint();
        grayBackground.setColor(0xFFDDDDDD);
        grayBackground.setAntiAlias(true);
    }

    private void setRect(RectF rect) {
        mRect = rect;
        if (mPasse == null) {
            return;
        }
        mRadius = MAX_CIRCLE_SIZE + MIN_PADDING;
        int neededRows;
        int maxRows;
        do {
            neededRows = (int) Math
                    .ceil((mRadius * 2 * mDensity * mPPP) / rect.width());
            maxRows = (int) Math.floor(rect.height() / (mRadius * 2 * mDensity));
            mRadius--;
        } while (neededRows > maxRows);
        mRadius -= MIN_PADDING;
        int numRows = Math.max(neededRows, 1);
        mShotsPerRow = (int) Math.ceil(mPPP / numRows);
        mRowHeight = rect.height() / numRows;
        mColumnWidth = rect.width() / mShotsPerRow;
    }

    public void setPasse(Passe p) {
        boolean calcLayout = mRect != null && mPasse == null;
        mPasse = p;
        mPPP = p.shot.length;
        mOldCoordinate = new Coordinate[mPPP];
        if (calcLayout) {
            setRect(mRect);
        }
    }

    public void draw(Canvas canvas) {
        if (mRect == null) {
            return;
        }

        // Draw touch feedback if arrow is pressed
        if (mPressed != NO_SELECTION) {
            int radius = getRadius(mPressed);
            Coordinate pos = getPosition(mPressed);
            if (radius > 0) {
                canvas.drawRect(pos.x - radius, pos.y - radius,
                        pos.x + radius, pos.y + radius, grayBackground);
            }
        }

        // Draw all points of this passe into the given rect
        for (int i = 0; i < mPPP; i++) {
            Shot shot = mPasse.shot[i];
            if (shot.zone == Shot.NOTHING_SELECTED) {
                break;
            }

            Coordinate coordinate = getAnimatedPosition(i);
            int radius = getRadius(i);
            if (radius > 0) {
                mCircle.draw(canvas, coordinate.x, coordinate.y, shot.zone, radius,
                        !TextUtils.isEmpty(shot.comment) && i != mSelected, i, shot.arrow);
            }
        }
    }

    @NonNull
    private Coordinate getPosition(int i) {
        if (mSelected == i) {
            return new Coordinate(mSelectedPosition.x, mSelectedPosition.y);
        } else {
            Coordinate coordinate = new Coordinate();
            float column = i % mShotsPerRow + 0.5f;
            coordinate.x = mRect.left + column * mColumnWidth;
            float row = (float) (Math.ceil(i / mShotsPerRow) + 0.5);
            coordinate.y = mRect.top + row * mRowHeight;
            return coordinate;
        }
    }

    private Coordinate getAnimatedPosition(int i) {
        Coordinate coordinate = getPosition(i);
        if (mCurAnimationProgress != -1 && mOldCoordinate[i] != null) {
            float oldX = mOldCoordinate[i].x;
            float oldY = mOldCoordinate[i].y;
            coordinate.x = oldX + (coordinate.x - oldX) * mCurAnimationProgress;
            coordinate.y = oldY + (coordinate.y - oldY) * mCurAnimationProgress;
        }
        return coordinate;
    }

    private int getRadius(int i) {
        int rad = mRadius;
        int oRad = oldRadius;
        if (mSelected == i) {
            rad = mSelectedRadius;
        } else if (oldSelected == i) {
            oRad = oldSelectedRadius;
        }
        if (mCurAnimationProgress != -1) {
            return (int) (oRad + (rad - oRad) * mCurAnimationProgress);
        } else {
            return rad;
        }
    }

    public void animateToSelection(int selected, Coordinate c, int radius) {
        saveCoordinates();
        setSelection(selected, c, radius);
        animate();
    }

    public void setSelection(int selected, Coordinate c, int radius) {
        // Cancel eventually currently running animation
        cancel();

        mSelected = selected;
        mSelectedPosition = c;
        mSelectedRadius = radius;
    }

    private void animate() {
        // Cancel eventually currently running animation
        cancel();

        selectionAnimator = ValueAnimator.ofFloat(0, 1);
        selectionAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        selectionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCurAnimationProgress = (Float) valueAnimator.getAnimatedValue();
                mParent.invalidate();
            }
        });
        selectionAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                selectionAnimator = null;
                mCurAnimationProgress = -1;
                mParent.invalidate();
            }
        });
        selectionAnimator.setDuration(300);
        selectionAnimator.start();
    }

    public void animateToRect(RectF newRect) {
        if (mRect == null) {
            setRect(newRect);
            return;
        }
        saveCoordinates();
        setRect(newRect);
        animate();
    }

    public void cancel() {
        if (selectionAnimator != null) {
            selectionAnimator.cancel();
        }
    }

    private void saveCoordinates() {
        oldSelectedRadius = mSelectedRadius;
        oldRadius = mRadius;
        oldSelected = mSelected;
        for (int i = 0; i < mPPP; i++) {
            mOldCoordinate[i] = getPosition(i);
        }
    }

    public int getPressedPosition(float x, float y) {
        if (mRect.contains(x, y)) {
            int col = (int) Math.floor((x - mRect.left) / mColumnWidth);
            int row = (int) Math.floor((y - mRect.top) / mRowHeight);
            final int arrow = row * mShotsPerRow + col;
            if (arrow < mPPP && mPasse.shot[arrow].zone >= -1) {
                return arrow;
            }
        }
        return -1;
    }

    public int getPressed() {
        return mPressed;
    }

    public void setPressed(int pressed) {
        mPressed = pressed;
        mParent.invalidate();
    }
}