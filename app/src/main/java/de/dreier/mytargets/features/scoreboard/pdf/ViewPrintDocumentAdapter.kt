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

import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.support.annotation.RequiresApi;
import android.widget.LinearLayout;

import java.io.FileOutputStream;
import java.io.IOException;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class ViewPrintDocumentAdapter extends PrintDocumentAdapter {
    public static final PrintAttributes.Resolution DEFAULT_RESOLUTION = new PrintAttributes.Resolution("default", "Default", 300, 300);
    public static final PrintAttributes.MediaSize DEFAULT_MEDIA_SIZE = PrintAttributes.MediaSize.ISO_A4;

    private final ViewToPdfWriter pdfWriter;
    private final String fileName;

    public ViewPrintDocumentAdapter(LinearLayout content, String fileName) {
        this.pdfWriter = new ViewToPdfWriter(content);
        this.fileName = fileName;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        PrintAttributes.Resolution resolution = newAttributes.getResolution();
        if (resolution == null) {
            resolution = DEFAULT_RESOLUTION;
        }
        PrintAttributes.MediaSize mediaSize = newAttributes.getMediaSize();
        if (mediaSize == null) {
            mediaSize = DEFAULT_MEDIA_SIZE;
        }

        int pageCount = pdfWriter.layoutPages(resolution, mediaSize);

        PrintDocumentInfo pdi = new PrintDocumentInfo.Builder(fileName)
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(pageCount)
                .build();

        callback.onLayoutFinished(pdi, true);
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(destination.getFileDescriptor());
            pdfWriter.writePdfDocument(pages, outputStream);
            callback.onWriteFinished(pages);
        } catch (Exception e) {
            //Catch exception
            e.printStackTrace();
            callback.onWriteFailed(e.getLocalizedMessage());
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
