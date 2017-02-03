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
import android.support.test.runner.intent.IntentCallback;
import android.support.test.runner.intent.IntentMonitorRegistry;
import android.support.test.runner.intent.IntentStubber;
import android.support.test.runner.intent.IntentStubberRegistry;

import org.hamcrest.Matcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.dreier.mytargets.shared.utils.FileUtils;

import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;

public class ImageCaptureUtils {

    private static IntentCallback intentCallback;
    private static boolean matched = false;

    @NonNull
    public static void intendingImageCapture(final Context context, final int mocked_image) {
        matched = false;
        final Matcher<Intent> intentMatcher = hasAction(MediaStore.ACTION_IMAGE_CAPTURE);
        // stub intent handling for retrieving RESULT_OK status back
        IntentStubberRegistry.load(new IntentStubber() {
            @Override
            public Instrumentation.ActivityResult getActivityResultForIntent(Intent intent) {
                if (intentMatcher.matches(intent)) {
                    Intent resultIntent = new Intent();
                    return new Instrumentation.ActivityResult(Activity.RESULT_OK, resultIntent);
                }
                return null;
            }
        });
        intentCallback = intent -> {
            if (!intentMatcher.matches(intent)) {
                return;
            }
            //extract output path for captured image from intent
            Uri uriToSaveImage = intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
            try {
                Resources testRes = context.getResources();
                InputStream ts = testRes.openRawResource(
                        mocked_image);
                OutputStream stream = context.getContentResolver()
                        .openOutputStream(uriToSaveImage);
                //save ready-made mock image to the provided Uri
                FileUtils.copy(ts, stream);
                matched = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        IntentMonitorRegistry.getInstance().addIntentCallback(intentCallback);
    }

    public static void intendedImageCapture() {
        if(!matched) {
            throw new RuntimeException("No intent captured with action ACTION_IMAGE_CAPTURE.");
        }
        IntentMonitorRegistry.getInstance().removeIntentCallback(intentCallback);
    }
}
