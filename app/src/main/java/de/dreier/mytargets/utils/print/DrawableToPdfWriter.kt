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

package de.dreier.mytargets.utils.print

import android.annotation.SuppressLint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.print.PageRange
import android.print.PrintAttributes
import android.support.annotation.RequiresApi
import de.dreier.mytargets.shared.utils.RectUtils
import de.dreier.mytargets.shared.utils.toClosestRect
import java.io.IOException
import java.io.OutputStream

/**
 * Handles printing the content view to a PDF stream.
 * It is responsible for laying out pages by traversing the children of the content view and
 * printing them onto the page. If one of the child views does no longer have sufficient space
 * it gets placed on the next page.
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
class DrawableToPdfWriter(private val content: Drawable) : IPdfWriter {
    private lateinit var contentRect: Rect
    private lateinit var fullPage: Rect

    /**
     * Calculates the number of pages it takes to print the content to the given print medium.
     * MUST be called before [.writePdfDocument].
     */
    @SuppressLint("Range")
    override fun layoutPages(resolution: PrintAttributes.Resolution, mediaSize: PrintAttributes.MediaSize): Int {
        val fullPageRectF = RectF(0f, 0f,
                mediaSize.widthMils * resolution.horizontalDpi / 1000f,
                mediaSize.heightMils * resolution.verticalDpi / 1000f)
        fullPage = fullPageRectF.toClosestRect()

        val contentRectF = RectF(fullPageRectF)
        contentRectF.inset(resolution.horizontalDpi * MARGIN_HORIZONTAL,
                resolution.verticalDpi * MARGIN_VERTICAL)

        contentRect = RectUtils.fitRectWithin(Rect(0, 0, 1, 1), contentRectF.toClosestRect())
        return 1
    }

    /**
     * Writes the given pages as PDF to the output stream.
     */
    @Throws(IOException::class)
    override fun writePdfDocument(pages: Array<PageRange>, outputStream: OutputStream) {
        val document = PdfDocument()

        if (!containsPage(pages, 0)) {
            document.close()
            return
        }
        val pageInfo = PdfDocument.PageInfo.Builder(fullPage.width(), fullPage.height(), 0).create()
        val page = document.startPage(pageInfo)

        val canvas = page.canvas
        content.bounds = contentRect
        content.draw(canvas)

        document.finishPage(page)

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
