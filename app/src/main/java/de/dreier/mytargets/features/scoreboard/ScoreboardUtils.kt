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

package de.dreier.mytargets.features.scoreboard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.print.PageRange
import android.support.annotation.RequiresApi
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import de.dreier.mytargets.features.scoreboard.builder.ViewBuilder
import de.dreier.mytargets.features.scoreboard.layout.DefaultScoreboardLayout
import de.dreier.mytargets.features.scoreboard.pdf.ViewPrintDocumentAdapter
import de.dreier.mytargets.features.scoreboard.pdf.ViewToPdfWriter
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.Training
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

object ScoreboardUtils {

    private const val PAGE_WIDTH = 600
    private const val MARGIN = 50

    fun getScoreboardView(context: Context, locale: Locale, training: Training, roundId: Long, configuration: ScoreboardConfiguration): LinearLayout {
        val rounds: List<Round>? = if (roundId == -1L) {
            training.loadRounds()
        } else {
            listOf(Round[roundId]!!)
        }

        val scoreboardLayout = DefaultScoreboardLayout(context, locale, configuration)
        val viewBuilder = ViewBuilder(context)
        scoreboardLayout.generateWithBuilder(viewBuilder, training, rounds!!)
        return viewBuilder.build()
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Throws(IOException::class)
    fun generatePdf(content: LinearLayout, file: File) {
        val writer = ViewToPdfWriter(content)
        writer.layoutPages(ViewPrintDocumentAdapter
                .DEFAULT_RESOLUTION, ViewPrintDocumentAdapter
                .DEFAULT_MEDIA_SIZE)

        val fileOutputStream = FileOutputStream(file)
        writer.writePdfDocument(arrayOf(PageRange.ALL_PAGES), fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
    }

    @Throws(IOException::class)
    fun generateBitmap(context: Context, content: LinearLayout, file: File) {
        val density = context.resources.displayMetrics.density
        val pageWidth = (PAGE_WIDTH * density).toInt()
        val margin = (MARGIN * density).toInt()

        content.measure(pageWidth - 2 * margin, WRAP_CONTENT)
        val width = content.measuredWidth
        val height = content.measuredHeight
        content.layout(0, 0, width, height)

        val b = Bitmap
                .createBitmap(width + 2 * margin, height + 2 * margin, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(b)

        val paint = Paint()
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        canvas.drawPaint(paint)

        canvas.save()
        canvas.translate(margin.toFloat(), margin.toFloat())
        content.draw(canvas)
        canvas.restore()

        val fileOutputStream = FileOutputStream(file)
        b.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
        b.recycle()
    }
}
