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

import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.support.annotation.RequiresApi
import java.io.FileOutputStream

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
class CustomPrintDocumentAdapter(private val pdfWriter: IPdfWriter, private val fileName: String) :
    PrintDocumentAdapter() {

    override fun onLayout(
        oldAttributes: PrintAttributes,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal,
        callback: PrintDocumentAdapter.LayoutResultCallback,
        extras: Bundle
    ) {
        if (cancellationSignal.isCanceled) {
            callback.onLayoutCancelled()
            return
        }

        val resolution = newAttributes.resolution ?: DEFAULT_RESOLUTION
        val mediaSize = newAttributes.mediaSize ?: DEFAULT_MEDIA_SIZE
        val pageCount = pdfWriter.layoutPages(resolution, mediaSize)

        val pdi = PrintDocumentInfo.Builder(fileName)
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(pageCount)
            .build()

        callback.onLayoutFinished(pdi, true)
    }

    override fun onWrite(
        pages: Array<PageRange>,
        destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal,
        callback: PrintDocumentAdapter.WriteResultCallback
    ) {
        val outputStream = FileOutputStream(destination.fileDescriptor)

        try {
            outputStream.use {
                pdfWriter.writePdfDocument(pages, outputStream)
            }
            callback.onWriteFinished(pages)
        } catch (e: Exception) {
            e.printStackTrace()
            callback.onWriteFailed(e.localizedMessage)
        }
    }

    companion object {
        val DEFAULT_RESOLUTION = PrintAttributes.Resolution("default", "Default", 300, 300)
        val DEFAULT_MEDIA_SIZE: PrintAttributes.MediaSize = PrintAttributes.MediaSize.ISO_A4
    }
}
