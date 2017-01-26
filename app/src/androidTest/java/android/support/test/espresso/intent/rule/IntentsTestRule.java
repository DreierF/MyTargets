/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.support.test.espresso.intent.rule;

import org.hamcrest.Matcher;

import android.app.Activity;
import android.support.test.annotation.Beta;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;

/**
 * This rule makes it easy to use Espresso-Intents APIs in functional UI tests. This class is an
 * extension of {@link ActivityTestRule}, which initializes Espresso-Intents before each test
 * annotated with
 * <a href="http://junit.org/javadoc/latest/org/junit/Test.html"><code>Test</code></a> and releases
 * Espresso-Intents after each test run. The Activity will be terminated after each test and this
 * rule can be used in the same way as {@link ActivityTestRule}.
 *
 * <p>
 * Espresso-Intents APIs can be used in two ways:
 * <ul>
 * <li>Intent Verification, using the {@link Intents#intended(Matcher)} API<li/>
 * <li>Intent Stubbing, using the {@link Intents#intending(Matcher)} API<li/>
 * </ul>
 *
 * @param <T> The activity to test
 */
@Beta
public class IntentsTestRule<T extends Activity> extends ActivityTestRule<T> {

    public IntentsTestRule(Class<T> activityClass) {
        super(activityClass);
    }

    public IntentsTestRule(Class<T> activityClass, boolean initialTouchMode) {
        super(activityClass, initialTouchMode);
    }

    public IntentsTestRule(Class<T> activityClass, boolean initialTouchMode,
                            boolean launchActivity) {
        super(activityClass, initialTouchMode, launchActivity);
    }

    @Override
    protected void afterActivityLaunched() {
        Intents.init();
        super.afterActivityLaunched();
    }

    @Override
    protected void afterActivityFinished() {
        super.afterActivityFinished();
        Intents.release();
    }

}
