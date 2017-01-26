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
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;

import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitor;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.intent.IntentCallback;
import android.support.test.runner.intent.IntentMonitor;
import android.support.test.runner.intent.IntentMonitorRegistry;
import android.support.test.runner.intent.IntentStubberRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.support.test.internal.runner.tracker.UsageTrackerRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;

import android.app.Instrumentation;
import android.content.Intent;
import android.view.View;

import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Intents enables validation and stubbing of intents sent out by the application under test.
 * <p>An example test that simply validates an outgoing intent:
 * <p>
 * <code>
 * <pre>
 * public void testValidateIntentSentToPackage() {
 *   // User action that results in an external "phone" activity being launched.
 *   user.clickOnView(system.getView(R.id.callButton));
 *
 *   // Using a canned RecordedIntentMatcher to validate that an intent resolving
 *   // to the "phone" activity has been sent.
 *   intended(toPackage("com.android.phone"));
 * }
 * </pre>
 * </code>
 * <p>An example test with intent stubbing:
 * <p>
 * <code>
 * <pre>
 * public void testActivityResultIsHandledProperly() {
 *   // Build a result to return when a particular activity is launched.
 *   Intent resultData = new Intent();
 *   String phoneNumber = "123-345-6789";
 *   resultData.putExtra("phone", phoneNumber);
 *   ActivityResult result = new ActivityResult(Activity.RESULT_OK, resultData);
 *
 *   // Set up result stubbing when an intent sent to "contacts" is seen.
 *   intending(toPackage("com.android.contacts")).respondWith(result));
 *
 *   // User action that results in "contacts" activity being launched.
 *   // Launching activity expects phoneNumber to be returned and displays it on the screen.
 *   user.clickOnView(system.getView(R.id.pickButton));
 *
 *   // Assert that data we set up above is shown.
 *   assertTrue(user.waitForText(phoneNumber));
 * }
 * </pre>
 * </code>
 */
public final class Intents {
  private static Intents defaultInstance;

  static {
    UsageTrackerRegistry.getInstance().trackUsage("Intents");
  }

  // Should be accessed only from main thread
  private static final List<VerifiableIntent> recordedIntents = new ArrayList<VerifiableIntent>();

  private static boolean isInitialized = false;

  private final ResettingStubber resettingStubber;
  private final IntentCallback intentCallback = new IntentCallback() {
    @Override
    public void onIntentSent(Intent intent) {
      recordedIntents.add(
        new VerifiableIntentImpl(((ResettingStubberImpl) resettingStubber).resolveIntent(intent)));
    }
  };
  private IntentMonitor intentMonitor;
  private static Instrumentation instrumentation;

  // VisibleForTesting
  Intents(ResettingStubber resettingStubber) {
    this.resettingStubber = checkNotNull(resettingStubber);
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////
  // Static methods that delegate to internal* methods for implementation. This pattern is used to
  // enable unit testing, while preserving the convenience of static methods for the user.
  ////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Initializes Intents and begins recording intents. Must be called prior to triggering any
   * actions that send out intents which need to be verified or stubbed. This is similar to
   * MockitoAnnotations.initMocks.
   */
  public static void init() {
    if (!IntentStubberRegistry.isLoaded()) {
      ResettingStubber resettingStubber = new ResettingStubberImpl();
      IntentStubberRegistry.load(resettingStubber);
      defaultInstance = new Intents(resettingStubber);
    }
    defaultInstance.internalInit();
  }

  /**
   * Clears Intents state. Must be called after each test case.
   */
  public static void release() {
    defaultInstance.internalRelease();
  }

  /**
   * Enables stubbing intent responses. This method is similar to Mockito.when and is particularly
   * useful when the activity launching the intent expects data to be returned (and especially in
   * the case when the destination activity is external). In this case, the test author can call
   * intending(matcher).thenRespond(myResponse) and validate that the launching activity handles the
   * result correctly. <b>Note:</b> the destination activity will not be launched.
   *
   * @param matcher the {@link Matcher} that matches intents for which stubbed response should be
   *        provided
   * @return {@link OngoingStubbing} object to set stubbed response
   */
  public static OngoingStubbing intending(Matcher<Intent> matcher) {
    return defaultInstance.internalIntending(matcher);
  }

  /**
   * Asserts that the given matcher matches one and only one intent sent by the application under
   * test. This is an equivalent of verify(mock, times(1)) in Mockito. Verification does not have to
   * occur in the same order as the intents were sent. Intents are recorded from the time that
   * Intents.init is called.
   *
   * @param matcher the {@link Matcher} to be applied to captured intents
   * @throws AssertionFailedError if the given {@link Matcher} did not match any or matched more
   * than one of the recorded intents
   */
  public static void intended(Matcher<Intent> matcher) {
    intended(matcher, times(1));
  }

  /**
   * Asserts that the given matcher matches a specified number of intents sent by the application
   * under test. This is an equivalent of verify(mock, times(num)) in Mockito. Verification does
   * not have to occur in the same order as the intents were sent. Intents are recorded from the
   * time that Intents.init is called.
   *
   * @param matcher the {@link Matcher} to be applied to captured intents
   * @throws AssertionFailedError if the given {@link Matcher} did not match the expected number of
   * recorded intents
   */
  public static void intended(final Matcher<Intent> matcher,
      final VerificationMode verificationMode) {
    if (resumedActivitiesExist(instrumentation)) {
      // Running through Espresso to take advantage of its synchronization mechanism.
      onView(isRoot()).check(new ViewAssertion() {
        @Override
        public void check(View view, NoMatchingViewException noViewFoundException) {
          defaultInstance.internalIntended(matcher, verificationMode, recordedIntents);
        }
      });
    } else {
      // No activities are resumed, so we don't need Espresso's synchronization.
      instrumentation.waitForIdleSync();
      instrumentation.runOnMainSync(new Runnable() {
        @Override
        public void run() {
          defaultInstance.internalIntended(matcher, verificationMode, recordedIntents);
        }
      });
      instrumentation.waitForIdleSync();
    }
  }

  /**
   * Allows verifying a specific number of intents sent by the application under test. This is an
   * equivalent of times(num) in Mockito.
   *
   * @param times the number of times that the intent should be matched.
   */
  public static VerificationMode times(int times) {
    if (times < 0) {
      throw new IllegalArgumentException("times expects a nonnegative integer");
    }
    return VerificationModes.times(times);
  }

  /**
   * Asserts that Intents does not have any unverified intents. You can use this method after you
   * have verified your intents to make sure that nothing unexpected was sent out. This is an
   * equivalent of verifyNoMoreInteractions() in Mockito.
   */
  public static void assertNoUnverifiedIntents() {
    intended(IntentMatchers.anyIntent(), VerificationModes.noUnverifiedIntents());
  }

  private static boolean resumedActivitiesExist(Instrumentation instrumentation) {
    final FutureTask<Boolean> checkResumed = new FutureTask<Boolean>(new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        ActivityLifecycleMonitor monitor = ActivityLifecycleMonitorRegistry.getInstance();
        return !monitor.getActivitiesInStage(Stage.RESUMED).isEmpty();
      }
    });
    instrumentation.runOnMainSync(checkResumed);
    try {
      return checkResumed.get();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////
  // Internal impl methods. Used for unit testing (hence package private)
  ////////////////////////////////////////////////////////////////////////////////////////////////

  void internalInit() {
    // TODO(user): Get rid of this or will we need any init here later?
    checkState(!isInitialized,
        "#init was called twice in a row. Make sure to call #release after every #init");
    instrumentation = InstrumentationRegistry.getInstrumentation();
    intentMonitor = IntentMonitorRegistry.getInstance();
    intentMonitor.addIntentCallback(intentCallback);
    resettingStubber.initialize();
    isInitialized = true;
  }

  OngoingStubbing internalIntending(Matcher<Intent> matcher) {
    return new OngoingStubbing(matcher, resettingStubber, instrumentation);
  }

  void internalRelease() {
    checkState(isInitialized, "init() must be called prior to using this method.");
    intentMonitor.removeIntentCallback(intentCallback);
    IntentStubberRegistry.reset();
    instrumentation.runOnMainSync(new Runnable() {
        @Override
        public void run() {
          recordedIntents.clear();
          resettingStubber.reset();
        }
    });
    isInitialized = false;
  }

  void internalIntended(
      Matcher<Intent> matcher, VerificationMode verificationMode, List<VerifiableIntent> intents) {
    checkState(isInitialized, "init() must be called prior to using this method.");
    verificationMode.verify(matcher, intents);
  }
}
