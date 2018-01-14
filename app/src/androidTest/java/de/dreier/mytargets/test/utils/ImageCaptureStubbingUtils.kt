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

package de.dreier.mytargets.test.utils

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.support.test.runner.intent.IntentStubberRegistry
import java.io.IOException

object ImageCaptureStubbingUtils {

    private var matched = false

    fun intendingImageCapture(context: Context, mockedImage: Int) {
        matched = false
        IntentStubberRegistry.load { intent ->
            if (MediaStore.ACTION_IMAGE_CAPTURE == intent.action) {
                val uriToSaveImage = intent.getParcelableExtra<Uri>(MediaStore.EXTRA_OUTPUT)
                saveMockToUri(context, mockedImage, uriToSaveImage)
                matched = true

                val resultIntent = Intent()
                return@load Instrumentation.ActivityResult(Activity.RESULT_OK, resultIntent)
            }
            null
        }
    }

    private fun saveMockToUri(context: Context, mockedImage: Int, uriToSaveImage: Uri) {
        try {
            val testResources = context.resources
            val inputStream = testResources.openRawResource(mockedImage)
            val outputStream = context.contentResolver
                    .openOutputStream(uriToSaveImage)
            inputStream.copyTo(outputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun intendedImageCapture() {
        if (!matched) {
            throw RuntimeException("No intent captured with action ACTION_IMAGE_CAPTURE.")
        }
        IntentStubberRegistry.reset()
    }
}
