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

package de.dreier.mytargets.shared.utils

import android.content.Context
import android.net.Uri
import android.support.v4.content.FileProvider
import java.io.*

object FileUtils {
    @Throws(IOException::class)
    fun copy(src: File, dst: File) {
        val inStream = FileInputStream(src)
        val outStream = FileOutputStream(dst)
        val inChannel = inStream.channel
        val outChannel = outStream.channel
        inChannel.transferTo(0, inChannel.size(), outChannel)
        inStream.close()
        outStream.close()
    }

    @Throws(IOException::class)
    fun copy(inputStream: InputStream, dst: File) {
        copy(inputStream, FileOutputStream(dst))
    }

    @Throws(IOException::class)
    fun copy(inputStream: InputStream, out: OutputStream) {
        val buf = ByteArray(1024)
        var len: Int = inputStream.read(buf)
        while (len > 0) {
            out.write(buf, 0, len)
            len = inputStream.read(buf)
        }
        inputStream.close()
        out.close()
    }

    @Throws(IOException::class)
    fun move(from: File, to: File) {
        val directory = to.parentFile
        if (!directory.exists()) {
            directory.mkdir()
        }
        copy(from, to)
        from.delete()
    }
}

fun File.toUri(context: Context): Uri {
    val packageName = context.packageName
    val authority = packageName + ".fileprovider"
    return FileProvider.getUriForFile(context, authority, this)
}
