/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.dreier.mytargets.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * RectD holds four long coordinates for a rectangle. The rectangle is
 * represented by the coordinates of its 4 edges (left, top, right bottom).
 * These fields can be accessed directly. Use width() and height() to retrieve
 * the rectangle's width and height. Note: most methods do not check to see that
 * the coordinates are sorted correctly (i.e. left <= right and top <= bottom).
 */
public class RectD implements Parcelable {
    public long left;
    public long top;
    public long right;
    public long bottom;

    /**
     * Create a new empty RectD. All coordinates are initialized to 0.
     */
    public RectD() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RectD r = (RectD) o;
        return left == r.left && top == r.top && right == r.right && bottom == r.bottom;
    }

    public String toString() {
        return "RectD(" + left + ", " + top + ", " + right + ", " + bottom + ")";
    }

    /**
     * @return the rectangle's width. This does not check for a valid rectangle
     * (i.e. left <= right) so the result may be negative.
     */
    public final long width() {
        return right - left;
    }

    /**
     * Set the rectangle's coordinates to the specified values. Note: no range
     * checking is performed, so it is up to the caller to ensure that
     * left <= right and top <= bottom.
     *
     * @param left   The X coordinate of the left side of the rectangle
     * @param top    The Y coordinate of the top of the rectangle
     * @param right  The X coordinate of the right side of the rectangle
     * @param bottom The Y coordinate of the bottom of the rectangle
     */
    public void set(long left, long top, long right, long bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    /**
     * Parcelable interface methods
     */
    public int describeContents() {
        return 0;
    }

    /**
     * Write this rectangle to the specified parcel. To restore a rectangle from
     * a parcel, use readFromParcel()
     *
     * @param out The parcel to write the rectangle's coordinates into
     */
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(left);
        out.writeLong(top);
        out.writeLong(right);
        out.writeLong(bottom);
    }

    public static final Parcelable.Creator<RectD> CREATOR = new Parcelable.Creator<RectD>() {
        /**
         * Return a new rectangle from the data in the specified parcel.
         */
        public RectD createFromParcel(Parcel in) {
            RectD r = new RectD();
            r.readFromParcel(in);
            return r;
        }

        /**
         * Return an array of rectangles of the specified size.
         */
        public RectD[] newArray(int size) {
            return new RectD[size];
        }
    };

    /**
     * Set the rectangle's coordinates from the data stored in the specified
     * parcel. To write a rectangle to a parcel, call writeToParcel().
     *
     * @param in The parcel to read the rectangle's coordinates from
     */
    void readFromParcel(Parcel in) {
        left = in.readLong();
        top = in.readLong();
        right = in.readLong();
        bottom = in.readLong();
    }
}