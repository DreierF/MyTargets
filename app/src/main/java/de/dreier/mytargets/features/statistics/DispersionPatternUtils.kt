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

package de.dreier.mytargets.features.statistics

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.print.PageRange
import android.support.annotation.RequiresApi
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.shared.targets.drawable.TargetImpactAggregationDrawable
import de.dreier.mytargets.utils.print.CustomPrintDocumentAdapter
import de.dreier.mytargets.utils.print.DrawableToPdfWriter
import de.dreier.mytargets.utils.writeToJPGFile
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

object DispersionPatternUtils {

    @Throws(IOException::class)
    fun createDispersionPatternImageFile(size: Int, file: File, statistic: ArrowStatistic) {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        val target = targetFromArrowStatistics(statistic)
        target.bounds = Rect(0, 0, size, size)
        target.draw(canvas)
        bitmap.writeToJPGFile(file)
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Throws(FileNotFoundException::class)
    fun generatePdf(f: File, statistic: ArrowStatistic) {
        FileOutputStream(f).use { outputStream ->
            val target = targetFromArrowStatistics(statistic)
            val pdfWriter = DrawableToPdfWriter(target)
            pdfWriter.layoutPages(
                CustomPrintDocumentAdapter.DEFAULT_RESOLUTION,
                CustomPrintDocumentAdapter.DEFAULT_MEDIA_SIZE
            )
            pdfWriter.writePdfDocument(arrayOf(PageRange(0, 0)), outputStream)
        }
    }

    fun targetFromArrowStatistics(statistic: ArrowStatistic): TargetImpactAggregationDrawable {
        val target = TargetImpactAggregationDrawable(statistic.target)
        target.replaceShotsWith(statistic.shots)
        target.setAggregationStrategy(SettingsManager.statisticsDispersionPatternAggregationStrategy)
        target.setArrowDiameter(statistic.arrowDiameter, SettingsManager.inputArrowDiameterScale)
        return target
    }
}
