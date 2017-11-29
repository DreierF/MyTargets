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

package de.dreier.mytargets.features.scoreboard.pdf;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.LinearLayout;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Handles printing the content view to a PDF stream.
 * It is responsible for laying out pages by traversing the children of the content view and
 * printing them onto the page. If one of the child views does no longer have sufficient space
 * it gets placed on the next page.
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class ViewToPdfWriter {

    /**
     * Left and Right page margin in inches
     */
    private static final int MARGIN_HORIZONTAL = 1;

    /**
     * Top and Bottom page margin in inches
     */
    private static final int MARGIN_VERTICAL = 1;

    private final LinearLayout content;
    private RectF contentRect;
    private RectF fullPage;

    public ViewToPdfWriter(LinearLayout content) {
        this.content = content;
    }

    /**
     * Calculates the number of pages it takes to print the content to the given print medium.
     * MUST be called before {@link #writePdfDocument(PageRange[], OutputStream)}.
     */
    public int layoutPages(PrintAttributes.Resolution resolution, PrintAttributes.MediaSize mediaSize) {
        fullPage = new RectF(0, 0,
                mediaSize.getWidthMils() * resolution.getHorizontalDpi() / 1000,
                mediaSize.getHeightMils() * resolution.getVerticalDpi() / 1000);

        contentRect = new RectF(fullPage);
        contentRect.inset(MARGIN_HORIZONTAL * resolution.getHorizontalDpi(),
                MARGIN_VERTICAL * resolution.getVerticalDpi());

        content.measure((int) contentRect.width(), (int) contentRect.height());
        content.layout(0, 0, (int) contentRect.width(), (int) contentRect.height());

        int sumHeight = 0;
        int pageCount = 1;
        for (int i = 0; i < content.getChildCount(); i++) {
            int measuredHeight = content.getChildAt(i).getMeasuredHeight();
            sumHeight += measuredHeight;
            if (sumHeight > (int) contentRect.height()) {
                sumHeight = measuredHeight;
                pageCount++;
            }
        }
        return pageCount;
    }

    /**
     * Writes the given pages as PDF to the output stream.
     */
    public void writePdfDocument(PageRange[] pages, OutputStream outputStream) throws IOException {
        PdfDocument document = new PdfDocument();

        int sumHeight = 0;
        int pageNumber = 0;
        int topAnchor = 0;
        PdfDocument.Page page = null;
        if (containsPage(pages, pageNumber)) {
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder((int) fullPage
                    .width(), (int) fullPage
                    .height(), pageNumber).create();
            page = document.startPage(pageInfo);
        }

        for (int i = 0; i < content.getChildCount(); i++) {
            View view = content.getChildAt(i);
            int measuredHeight = view.getMeasuredHeight();
            if (sumHeight + measuredHeight > contentRect.height()) {
                sumHeight = 0;
                topAnchor = view.getTop();
                pageNumber++;

                if (page != null) {
                    document.finishPage(page);
                    page = null;
                }

                if (containsPage(pages, pageNumber)) {
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder((int) fullPage
                            .width(), (int) fullPage
                            .height(), pageNumber)
                            .create();
                    page = document.startPage(pageInfo);
                }
            }

            if (page != null) {
                Canvas canvas = page.getCanvas();
                canvas.save();
                canvas.translate(contentRect.left,
                        contentRect.top + view.getTop() - topAnchor);
                view.draw(canvas);
                canvas.restore();
            }
            sumHeight += measuredHeight;
        }

        if (page != null) {
            document.finishPage(page);
        }

        document.writeTo(outputStream);
        document.close();
    }

    /**
     * Tests if the given page is contained in one of the given page ranges.
     *
     * @param pages      Page ranges (zero-based indices)
     * @param pageNumber Page to search for (zero-based)
     */
    private boolean containsPage(PageRange[] pages, int pageNumber) {
        for (PageRange pageRange : pages) {
            if (pageRange.getStart() <= pageNumber && pageRange.getEnd() >= pageNumber) {
                return true;
            }
        }
        return false;
    }
}
