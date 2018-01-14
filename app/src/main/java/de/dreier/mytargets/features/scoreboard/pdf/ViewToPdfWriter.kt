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

package de.dreier.mytargets.features.scoreboard.pdf

import android.annotation.SuppressLint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.print.PageRange
import android.print.PrintAttributes
import android.support.annotation.RequiresApi
import android.view.View
import android.view.View.MeasureSpec.EXACTLY
import android.view.View.MeasureSpec.makeMeasureSpec
import android.widget.LinearLayout
import java.io.IOException
import java.io.OutputStream

/**
 * Handles printing the content view to a PDF stream.
 * It is responsible for laying out pages by traversing the children of the content view and
 * printing them onto the page. If one of the child views does no longer have sufficient space
 * it gets placed on the next page.
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
class ViewToPdfWriter(private val content: LinearLayout) {
    private var contentRect: RectF? = null
    private var fullPage: RectF? = null

    /**
     * Calculates the number of pages it takes to print the content to the given print medium.
     * MUST be called before [.writePdfDocument].
     */
    @SuppressLint("Range")
    fun layoutPages(resolution: PrintAttributes.Resolution, mediaSize: PrintAttributes.MediaSize): Int {
        fullPage = RectF(0f, 0f,
                mediaSize.widthMils * resolution.horizontalDpi / 1000f,
                mediaSize.heightMils * resolution.verticalDpi / 1000f)

        contentRect = RectF(fullPage)
        contentRect!!.inset(resolution.horizontalDpi * MARGIN_HORIZONTAL,
                resolution.verticalDpi * MARGIN_VERTICAL)

        content.measure(makeMeasureSpec(contentRect!!.width().toInt(), EXACTLY),
                makeMeasureSpec(contentRect!!.height().toInt(), View.MeasureSpec.UNSPECIFIED))
        content.layout(0, 0, contentRect!!.width().toInt(), contentRect!!.height().toInt())

        var sumHeight = 0
        var pageCount = 1
        for (i in 0 until content.childCount) {
            val measuredHeight = content.getChildAt(i).measuredHeight
            sumHeight += measuredHeight
            if (sumHeight > contentRect!!.height().toInt()) {
                sumHeight = measuredHeight
                pageCount++
            }
        }
        return pageCount
    }

    /**
     * Writes the given pages as PDF to the output stream.
     */
    @Throws(IOException::class)
    fun writePdfDocument(pages: Array<PageRange>, outputStream: OutputStream) {
        val document = PdfDocument()

        var sumHeight = 0
        var pageNumber = 0
        var topAnchor = 0
        var page: PdfDocument.Page? = null
        if (containsPage(pages, pageNumber)) {
            val pageInfo = PdfDocument.PageInfo.Builder(fullPage!!
                    .width().toInt(), fullPage!!.height().toInt(), pageNumber).create()
            page = document.startPage(pageInfo)
        }

        for (i in 0 until content.childCount) {
            val view = content.getChildAt(i)
            val measuredHeight = view.measuredHeight
            if (sumHeight + measuredHeight > contentRect!!.height()) {
                sumHeight = 0
                topAnchor = view.top
                pageNumber++

                if (page != null) {
                    document.finishPage(page)
                    page = null
                }

                if (containsPage(pages, pageNumber)) {
                    val pageInfo = PdfDocument.PageInfo.Builder(fullPage!!
                            .width().toInt(), fullPage!!.height().toInt(), pageNumber).create()
                    page = document.startPage(pageInfo)
                }
            }

            if (page != null) {
                val canvas = page.canvas
                canvas.save()
                canvas.translate(contentRect!!.left, contentRect!!.top + view.top - topAnchor)
                view.draw(canvas)
                canvas.restore()
            }
            sumHeight += measuredHeight
        }

        if (page != null) {
            document.finishPage(page)
        }

        document.writeTo(outputStream)
        document.close()
    }

    /**
     * Tests if the given page is contained in one of the given page ranges.
     *
     * @param pages      Page ranges (zero-based indices)
     * @param pageNumber Page to search for (zero-based)
     */
    private fun containsPage(pages: Array<PageRange>, pageNumber: Int): Boolean {
        return pages.any { (it.start..it.end).contains(pageNumber) }
    }

    companion object {

        /**
         * Left and Right page margin in inches
         */
        private const val MARGIN_HORIZONTAL = 0.78f

        /**
         * Top and Bottom page margin in inches
         */
        private const val MARGIN_VERTICAL = 0.78f
    }
}
