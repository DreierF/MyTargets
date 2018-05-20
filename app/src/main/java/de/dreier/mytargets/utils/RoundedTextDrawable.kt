/*
 * Copyright (C) 2018 Florian Dreier
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

package de.dreier.mytargets.utils

import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import de.dreier.mytargets.features.statistics.ArrowStatistic
import java.util.*

class RoundedTextDrawable private constructor(
    private val text: String,
    bgColor: Int,
    textColor: Int
) : Drawable() {
    private val paint = Paint()
    private val rectF = RectF()
    private val textPaint = TextPaint()
    private val textBounds = Rect()

    init {
        textPaint.color = textColor
        textPaint.typeface = Typeface.DEFAULT_BOLD
        paint.isAntiAlias = true
        paint.isDither = true
        paint.color = bgColor
    }

    constructor(item: ArrowStatistic) : this(
        String.format(Locale.US, "%.1f", item.totalScore.shotAverage),
        item.appropriateBgColor,
        item.appropriateTextColor
    )

    override fun draw(canvas: Canvas) {
        canvas.drawOval(rectF, paint)
        canvas.drawText(
            text, rectF.centerX() - textBounds.width() / 2,
            rectF.centerY() + textBounds.height() / 2, textPaint
        )
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)

        rectF.set(bounds)
        textPaint.textSize = rectF.height() / 2.5f
        textPaint.getTextBounds(text, 0, text.length, textBounds)
    }

    override fun setAlpha(alpha: Int) {
        if (paint.alpha != alpha) {
            paint.alpha = alpha
            invalidateSelf()
        }
    }

    override fun setColorFilter(cf: ColorFilter?) {
        paint.colorFilter = cf
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setFilterBitmap(filter: Boolean) {
        paint.isFilterBitmap = filter
        invalidateSelf()
    }
}
