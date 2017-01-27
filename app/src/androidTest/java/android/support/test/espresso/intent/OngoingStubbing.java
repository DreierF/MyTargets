/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.support.test.espresso.intent;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityResult;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.RawRes;

import org.hamcrest.Matcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.dreier.mytargets.shared.utils.FileUtils;

import static android.support.test.espresso.intent.Checks.checkNotNull;

/**
 * Supports method chaining after @Intents#intending method call.
 */
public final class OngoingStubbing {

    private final Matcher<Intent> matcher;
    private final ResettingStubber resettingStubber;
    private final Instrumentation instrumentation;

    OngoingStubbing(Matcher<Intent> matcher, ResettingStubber resettingStubber,
                    Instrumentation instrumentation) {
        this.matcher = checkNotNull(matcher);
        this.resettingStubber = checkNotNull(resettingStubber);
        this.instrumentation = checkNotNull(instrumentation);
    }

    /**
     * Sets a response for the intent being stubbed.
     */
    public void respondWith(final ActivityResult result) {
        checkNotNull(result);
        instrumentation.waitForIdleSync();
        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                resettingStubber.setActivityResultForIntent(matcher, intent -> result);
            }
        });
        instrumentation.waitForIdleSync();
    }

    /**
     * Sets a response for the intent being stubbed.
     */
    public void respondWith(final IntentHandler intentHandler) {
        checkNotNull(intentHandler);
        instrumentation.waitForIdleSync();
        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                resettingStubber.setActivityResultForIntent(matcher, intentHandler);
            }
        });
        instrumentation.waitForIdleSync();
    }

    public void respondWithRawImage(@RawRes final int rawResId) {
        checkNotNull(rawResId);
        respondWith(intent -> {
            Intent resultIntent = new Intent();
            Uri uriToSaveImage = intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
            //save ready-made mock image to the provided Uri
            try {
                Context testContext = instrumentation.getContext();
                Resources testRes = testContext.getResources();
                InputStream ts = testRes.openRawResource(rawResId);
                OutputStream stream = testContext.getContentResolver()
                        .openOutputStream(uriToSaveImage);
                FileUtils.copy(ts, stream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new Instrumentation.ActivityResult(Activity.RESULT_OK, resultIntent);
        });
    }
}
