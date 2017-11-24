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

package de.dreier.mytargets.shared.utils;

import android.graphics.Rect;
import android.support.annotation.NonNull;

public class RectUtils {
    public static Rect fitRectWithin(@NonNull Rect inner, @NonNull Rect outer) {
        float innerAspectRatio = inner.width() / (float) inner.height();
        float outerAspectRatio = outer.width() / (float) outer.height();

        float resizeFactor = (innerAspectRatio >= outerAspectRatio) ?
                (outer.width() / (float) inner.width()) :
                (outer.height() / (float) inner.height());

        float newWidth = inner.width() * resizeFactor;
        float newHeight = inner.height() * resizeFactor;
        float newLeft = outer.left + (outer.width() - newWidth) / 2f;
        float newTop = outer.top + (outer.height() - newHeight) / 2f;

        return new Rect((int) newLeft, (int) newTop, (int) (newWidth + newLeft), (int) (newHeight +
                newTop));
    }
}
