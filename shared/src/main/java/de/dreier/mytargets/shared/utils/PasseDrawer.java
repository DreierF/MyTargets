package de.dreier.mytargets.shared.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.target.Target;

/**
 * Created by Florian on 20.03.2015.
 */
public class PasseDrawer {
    public static final int NO_SELECTION = -1;
    private static final int MIN_PADDING = 2;
    public static int MAX_CIRCLE_SIZE = 17;

    private final Circle mCircle;
    private final View mParent;
    private RectF mRect;
    private Passe mPasse;
    private int mPressed = NO_SELECTION;
    private int mRadius;
    private Paint grayBackground;
    private float mDensity;
    private int mPPP;
    private int mShotsPerRow;

    private int mSelected = NO_SELECTION;
    private Coordinate mSelectedPosition;
    private float mRowHeight;
    private float mColumnWidth;

    // Animation
    private Coordinate[] mOldCoordinate;
    private float mCurAnimationProgress = -1;
    private ValueAnimator selectionAnimator;
    private int oldRadius, oldSelected, oldSelectedRadius;
    private int mSelectedRadius;

    public PasseDrawer(View parent, float density, Target target) {
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
        int neededRows, maxRows;
        do {
            neededRows = (int) Math
                    .ceil((mRadius * 2 * mDensity * mPPP) / rect.width());
            maxRows = (int) Math.floor(rect.height() / (mRadius * 2 * mDensity));
            mRadius--;
        } while (neededRows > maxRows);
        mRadius -= MIN_PADDING;
        int numRows = neededRows;
        mShotsPerRow = (int) Math.ceil(mPPP / numRows);
        mRowHeight = rect.height() / numRows;
        mColumnWidth = rect.width() / mShotsPerRow;
    }

    public void setPressed(int pressed) {
        mPressed = pressed;
        mParent.invalidate();
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
    public Coordinate getPosition(int i) {
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

    public Coordinate getAnimatedPosition(int i) {
        Coordinate coordinate = getPosition(i);
        if (mCurAnimationProgress != -1) {
            float oldX = mOldCoordinate[i].x; ///TODO fix NPE
            float oldY = mOldCoordinate[i].y;
            coordinate.x = oldX + (coordinate.x - oldX) * mCurAnimationProgress;
            coordinate.y = oldY + (coordinate.y - oldY) * mCurAnimationProgress;
        }
        return coordinate;
    }

    public int getRadius(int i) {
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

    public void saveCoordinates() {
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

    public void saveState(Bundle b) {
        b.putSerializable("pd_passe", mPasse);
        b.putInt("pd_pressed", mPressed);
        b.putInt("pd_selected", mSelected);
        b.putSerializable("pd_selected_pos", mSelectedPosition);
        b.putInt("pd_selected_radius", mSelectedRadius);
    }

    public void restoreState(Bundle b) {
        mPasse = (Passe) b.getSerializable("pd_passe");
        mPressed = b.getInt("pd_pressed");
        mSelected = b.getInt("pd_selected");
        mSelectedPosition = (Coordinate) b.getSerializable("pd_selected_pos");
        mSelectedRadius = b.getInt("pd_selected_radius");
    }
}
