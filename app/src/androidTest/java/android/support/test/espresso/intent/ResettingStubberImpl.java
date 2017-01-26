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

import static android.support.test.espresso.intent.Checks.checkNotNull;
import static android.support.test.espresso.intent.Checks.checkState;

import android.support.test.InstrumentationRegistry;

import android.app.Instrumentation.ActivityResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Looper;
import android.util.Pair;

import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Implementation of {@link ResettingStubber}
 */
public final class ResettingStubberImpl implements ResettingStubber {

  // Should be accessed only from main thread
  private List<Pair<Matcher<Intent>, ActivityResult>> intentResponsePairs =
      new ArrayList<Pair<Matcher<Intent>, ActivityResult>>();

  private PackageManager packageManager;
  private boolean isInitialized;

  @Override
  public void initialize() {
    packageManager = InstrumentationRegistry.getTargetContext().getPackageManager();
    isInitialized = true;
  }

  @Override
  public boolean isInitialized() {
    return isInitialized;
  }

  @Override
  public void reset() {
    checkMain();
    intentResponsePairs.clear();
    isInitialized = false;
  }

  @Override
  public void setActivityResultForIntent(Matcher<Intent> matcher, ActivityResult result) {
    checkState(isInitialized, "ResettingStubber must be initialized before calling this method");
    checkNotNull(matcher);
    checkMain();
    intentResponsePairs.add(new Pair<Matcher<Intent>, ActivityResult>(matcher, result));
  }

  @Override
  public ActivityResult getActivityResultForIntent(Intent intent) {
    checkState(isInitialized, "ResettingStubber must be initialized before calling this method");
    checkNotNull(intent);
    checkMain();
    ListIterator<Pair<Matcher<Intent>, ActivityResult>> reverseIterator =
        intentResponsePairs.listIterator(intentResponsePairs.size());
    while (reverseIterator.hasPrevious()) {
      Pair<Matcher<Intent>, ActivityResult> pair = reverseIterator.previous();
      // We resolve the intent so that the toPackage matcher has the necessary information to match
      // the intent.
      if (pair.first.matches(resolveIntent(intent))) {
        return pair.second;
      }
    }
    return null;
  }

  // package private, so that Intents can use this to resolve intents as it records them.
  ResolvedIntent resolveIntent(Intent intent) {
    // Crazy Android API. Setting flags param to zero per discussion here:
    // http://stackoverflow.com/questions/9623079/
    // why-does-the-flag-specified-in-queryintentactivities-method-is-set-to-zero and
    // http://developer.android.com/training/basics/intents/sending.html
    List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);
    if (null == resolveInfos) {
      // Gingerbread returns null here if nothing resolves, other APIs return an empty list.
      resolveInfos = new ArrayList<ResolveInfo>();
    }
    return new ResolvedIntentImpl(intent, resolveInfos);
  }

  private static void checkMain() {
    checkState(Looper.myLooper() == Looper.getMainLooper(), "Must be called on main thread.");
  }
}
