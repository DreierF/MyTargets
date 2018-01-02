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

package de.dreier.mytargets.features.statistics

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.print.PrintAttributes
import android.support.annotation.RequiresApi
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.shared.targets.drawable.TargetImpactAggregationDrawable
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

object DispersionPatternUtils {

    @Throws(FileNotFoundException::class)
    fun createDispersionPatternImageFile(size: Int, f: File, statistic: ArrowStatistic) {
        val b = getDispersionPatternBitmap(size, statistic)
        val fOut = FileOutputStream(f)
        try {
            b.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
            fOut.flush()
            fOut.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                fOut.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Throws(FileNotFoundException::class)
    fun generatePdf(f: File, statistic: ArrowStatistic) {
        val outputStream = FileOutputStream(f)
        try {
            val target = TargetImpactAggregationDrawable(statistic.target)
            target.replaceShotsWith(statistic.shots)
            target.setArrowDiameter(statistic.arrowDiameter,
                    SettingsManager.inputArrowDiameterScale)

            val document = PdfDocument()

            val mediaSize = PrintAttributes.MediaSize.ISO_A4
            @SuppressLint("Range")
            val pageInfo = PdfDocument.PageInfo.Builder((mediaSize
                    .widthMils * 0.072f).toInt(), (mediaSize.heightMils * 0.072f).toInt(), 1)
                    .create()

            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            target.setBounds(0, 0, canvas.width, canvas.height)
            target.draw(canvas)
            document.finishPage(page)

            document.writeTo(outputStream)
            document.close()
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    fun getDispersionPatternBitmap(size: Int, statistic: ArrowStatistic): Bitmap {
        val b = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(b)
        canvas.drawColor(Color.WHITE)

        val target = TargetImpactAggregationDrawable(statistic.target)
        target.replaceShotsWith(statistic.shots)
        target.setArrowDiameter(statistic.arrowDiameter,
                SettingsManager.inputArrowDiameterScale)
        target.bounds = Rect(0, 0, size, size)
        target.draw(canvas)
        return b
    }
}
