/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.dreier.mytargets;

import android.graphics.Rect;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.espresso.util.HumanReadables;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;

import static android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;

/**
 * Enables scrolling to the given view. View must be a descendant of a ScrollView.
 */
public final class NestedScrollToAction implements ViewAction {
  private static final String TAG = NestedScrollToAction.class.getSimpleName();

  @SuppressWarnings("unchecked")
  @Override
  public Matcher<View> getConstraints() {
    return CoreMatchers.allOf(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), ViewMatchers.isDescendantOfA(CoreMatchers.anyOf(
            ViewMatchers.isAssignableFrom(ScrollView.class), ViewMatchers.isAssignableFrom(HorizontalScrollView.class), ViewMatchers.isAssignableFrom(NestedScrollView.class))));
  }

  @Override
  public void perform(UiController uiController, View view) {
    if (isDisplayingAtLeast(90).matches(view)) {
      Log.i(TAG, "View is already displayed. Returning.");
      return;
    }
    Rect rect = new Rect();
    view.getDrawingRect(rect);
    if (!view.requestRectangleOnScreen(rect, true /* immediate */)) {
      Log.w(TAG, "Scrolling to view was requested, but none of the parents scrolled.");
    }
    uiController.loopMainThreadUntilIdle();
    if (!isDisplayingAtLeast(90).matches(view)) {
      throw new PerformException.Builder()
        .withActionDescription(this.getDescription())
        .withViewDescription(HumanReadables.describe(view))
        .withCause(new RuntimeException(
              "Scrolling to view was attempted, but the view is not displayed"))
        .build();
    }
  }

  @Override
  public String getDescription() {
    return "scroll to";
  }
}