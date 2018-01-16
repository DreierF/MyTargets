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

package de.dreier.mytargets.shared.utils

import android.content.Context
import android.net.Uri
import android.support.v4.content.FileProvider
import java.io.File
import java.io.IOException

@Throws(IOException::class)
fun File.moveTo(to: File) {
    val directory = to.parentFile
    if (!directory.exists()) {
        directory.mkdir()
    }
    copyTo(to, overwrite = true)
    delete()
}

fun File.toUri(context: Context): Uri {
    val packageName = context.packageName
    val authority = packageName + ".fileprovider"
    return FileProvider.getUriForFile(context, authority, this)
}
