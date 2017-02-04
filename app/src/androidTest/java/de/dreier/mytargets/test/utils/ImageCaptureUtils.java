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
import android.support.test.runner.intent.IntentStubberRegistry;

import org.hamcrest.Matcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.dreier.mytargets.shared.utils.FileUtils;

import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;

public class ImageCaptureUtils {

    private static boolean matched = false;

    public static void intendingImageCapture(final Context context, final int mocked_image) {
        matched = false;
        final Matcher<Intent> intentMatcher = hasAction(MediaStore.ACTION_IMAGE_CAPTURE);
        IntentStubberRegistry.load(intent -> {
            if (intentMatcher.matches(intent)) {
                Uri uriToSaveImage = intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
                saveMockToUri(context, mocked_image, uriToSaveImage);
                matched = true;

                Intent resultIntent = new Intent();
                return new Instrumentation.ActivityResult(Activity.RESULT_OK, resultIntent);
            }
            return null;
        });
    }

    private static void saveMockToUri(Context context, int mocked_image, Uri uriToSaveImage) {
        try {
            Resources testRes = context.getResources();
            InputStream ts = testRes.openRawResource(mocked_image);
            OutputStream stream = context.getContentResolver()
                    .openOutputStream(uriToSaveImage);
            FileUtils.copy(ts, stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void intendedImageCapture() {
        if(!matched) {
            throw new RuntimeException("No intent captured with action ACTION_IMAGE_CAPTURE.");
        }
        IntentStubberRegistry.reset();
    }
}
