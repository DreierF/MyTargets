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

package de.dreier.mytargets.test.utils;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.test.runner.intent.IntentStubberRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.dreier.mytargets.shared.utils.FileUtils;

public class ImageCaptureStubbingUtils {

    private static boolean matched = false;

    public static void intendingImageCapture(@NonNull final Context context, final int mockedImage) {
        matched = false;
        IntentStubberRegistry.load(intent -> {
            if (MediaStore.ACTION_IMAGE_CAPTURE.equals(intent.getAction())) {
                Uri uriToSaveImage = intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
                saveMockToUri(context, mockedImage, uriToSaveImage);
                matched = true;

                Intent resultIntent = new Intent();
                return new Instrumentation.ActivityResult(Activity.RESULT_OK, resultIntent);
            }
            return null;
        });
    }

    private static void saveMockToUri(@NonNull Context context, int mockedImage, @NonNull Uri uriToSaveImage) {
        try {
            Resources testRes = context.getResources();
            InputStream ts = testRes.openRawResource(mockedImage);
            OutputStream stream = context.getContentResolver()
                    .openOutputStream(uriToSaveImage);
            FileUtils.INSTANCE.copy(ts, stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void intendedImageCapture() {
        if (!matched) {
            throw new RuntimeException("No intent captured with action ACTION_IMAGE_CAPTURE.");
        }
        IntentStubberRegistry.reset();
    }
}
