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
import android.print.PageRange
import android.print.PrintAttributes
import java.io.IOException
import java.io.OutputStream

interface IPdfWriter {
    /**
     * Calculates the number of pages it takes to print the content to the given print medium.
     * MUST be called before [.writePdfDocument].
     */
    @SuppressLint("Range")
    fun layoutPages(resolution: PrintAttributes.Resolution, mediaSize: PrintAttributes.MediaSize): Int

    /**
     * Writes the given pages as PDF to the output stream.
     */
    @Throws(IOException::class)
    fun writePdfDocument(pages: Array<PageRange>, outputStream: OutputStream)
}
