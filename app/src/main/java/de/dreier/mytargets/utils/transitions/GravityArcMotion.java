/*
 * Copyright (C) 2017 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.utils.transitions;

import android.graphics.Path;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.transition.ArcMotion;

/**
 * A tweak to {@link ArcMotion} which slightly alters the path calculation. In the real world
 * gravity slows upward motion and accelerates downward motion. This class emulates this behavior
 * to make motion paths appear more natural.
 * <p>
 * See https://www.google.com/design/spec/motion/movement.html#movement-movement-within-screen-bounds
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GravityArcMotion extends ArcMotion {

    private static final float DEFAULT_MAX_ANGLE_DEGREES = 70;
    private static final float DEFAULT_MAX_TANGENT = (float)
            Math.tan(Math.toRadians(DEFAULT_MAX_ANGLE_DEGREES / 2));

    private float mMinimumHorizontalAngle = 0;
    private float mMinimumVerticalAngle = 0;
    private float mMaximumAngle = DEFAULT_MAX_ANGLE_DEGREES;
    private float mMinimumHorizontalTangent = 0;
    private float mMinimumVerticalTangent = 0;
    private float mMaximumTangent = DEFAULT_MAX_TANGENT;

    /**
     * @inheritDoc
     */
    @Override
    public void setMinimumHorizontalAngle(float angleInDegrees) {
        mMinimumHorizontalAngle = angleInDegrees;
        mMinimumHorizontalTangent = toTangent(angleInDegrees);
    }

    /**
     * @inheritDoc
     */
    @Override
    public float getMinimumHorizontalAngle() {
        return mMinimumHorizontalAngle;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void setMinimumVerticalAngle(float angleInDegrees) {
        mMinimumVerticalAngle = angleInDegrees;
        mMinimumVerticalTangent = toTangent(angleInDegrees);
    }

    /**
     * @inheritDoc
     */
    @Override
    public float getMinimumVerticalAngle() {
        return mMinimumVerticalAngle;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void setMaximumAngle(float angleInDegrees) {
        mMaximumAngle = angleInDegrees;
        mMaximumTangent = toTangent(angleInDegrees);
    }

    /**
     * @inheritDoc
     */
    @Override
    public float getMaximumAngle() {
        return mMaximumAngle;
    }

    private static float toTangent(float arcInDegrees) {
        if (arcInDegrees < 0 || arcInDegrees > 90) {
            throw new IllegalArgumentException("Arc must be between 0 and 90 degrees");
        }
        return (float) Math.tan(Math.toRadians(arcInDegrees / 2));
    }

    @NonNull
    @Override
    public Path getPath(float startX, float startY, float endX, float endY) {
        // Here's a little ascii art to show how this is calculated:
        // c---------- b
        //  \        / |
        //    \     d  |
        //      \  /   e
        //        a----f
        // This diagram assumes that the horizontal distance is less than the vertical
        // distance between The start point (a) and end point (b).
        // d is the midpoint between a and b. c is the center point of the circle with
        // This path is formed by assuming that start and end points are in
        // an arc on a circle. The end point is centered in the circle vertically
        // and start is a point on the circle.

        // Triangles bfa and bde form similar right triangles. The control points
        // for the cubic Bezier arc path are the midpoints between a and e and e and b.

        Path path = new Path();
        path.moveTo(startX, startY);

        float ex;
        float ey;
        if (startY == endY) {
            ex = (startX + endX) / 2;
            ey = startY + mMinimumHorizontalTangent * Math.abs(endX - startX) / 2;
        } else if (startX == endX) {
            ex = startX + mMinimumVerticalTangent * Math.abs(endY - startY) / 2;
            ey = (startY + endY) / 2;
        } else {
            float deltaX = endX - startX;

            /**
             * This is the only change to ArcMotion
             */
            float deltaY;
            if (endY < startY) {
                deltaY = startY - endY; // Y is inverted compared to diagram above.
            } else {
                deltaY = endY - startY;
            }
            /**
             * End changes
             */

            // hypotenuse squared.
            float h2 = deltaX * deltaX + deltaY * deltaY;

            // Midpoint between start and end
            float dx = (startX + endX) / 2;
            float dy = (startY + endY) / 2;

            // Distance squared between end point and mid point is (1/2 hypotenuse)^2
            float midDist2 = h2 * 0.25f;

            float minimumArcDist2;

            if (Math.abs(deltaX) < Math.abs(deltaY)) {
                // Similar triangles bfa and bde mean that (ab/fb = eb/bd)
                // Therefore, eb = ab * bd / fb
                // ab = hypotenuse
                // bd = hypotenuse/2
                // fb = deltaY
                float eDistY = h2 / (2 * deltaY);
                ey = endY + eDistY;
                ex = endX;

                minimumArcDist2 = midDist2 * mMinimumVerticalTangent * mMinimumVerticalTangent;
            } else {
                // Same as above, but flip X & Y
                float eDistX = h2 / (2 * deltaX);
                ex = endX + eDistX;
                ey = endY;

                minimumArcDist2 = midDist2 * mMinimumHorizontalTangent * mMinimumHorizontalTangent;
            }
            float arcDistX = dx - ex;
            float arcDistY = dy - ey;
            float arcDist2 = arcDistX * arcDistX + arcDistY * arcDistY;

            float maximumArcDist2 = midDist2 * mMaximumTangent * mMaximumTangent;

            float newArcDistance2 = 0;
            if (arcDist2 < minimumArcDist2) {
                newArcDistance2 = minimumArcDist2;
            } else if (arcDist2 > maximumArcDist2) {
                newArcDistance2 = maximumArcDist2;
            }
            if (newArcDistance2 != 0) {
                float ratio2 = newArcDistance2 / arcDist2;
                float ratio = (float) Math.sqrt(ratio2);
                ex = dx + (ratio * (ex - dx));
                ey = dy + (ratio * (ey - dy));
            }
        }
        float controlX1 = (startX + ex) / 2;
        float controlY1 = (startY + ey) / 2;
        float controlX2 = (ex + endX) / 2;
        float controlY2 = (ey + endY) / 2;
        path.cubicTo(controlX1, controlY1, controlX2, controlY2, endX, endY);
        return path;
    }

}
