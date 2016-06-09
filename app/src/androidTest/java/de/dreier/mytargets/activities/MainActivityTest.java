package de.dreier.mytargets.activities;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dreier.mytargets.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void mainActivityTest() {
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fab), isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction floatingActionButton2 = onView(
                allOf(withId(R.id.fab1), isDisplayed()));
        floatingActionButton2.perform(click());

        ViewInteraction distanceSelector = onView(
                allOf(withId(R.id.distanceSpinner),
                        withParent(allOf(withId(R.id.practiceLayout),
                                withParent(withId(R.id.notEditable)))),
                        isDisplayed()));
        distanceSelector.perform(click());

        ViewInteraction recyclerView = onView(
                allOf(withId(android.R.id.list), isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(10, click()));

        ViewInteraction appCompatRadioButton = onView(
                allOf(withId(R.id.indoor), withText("Indoor"), isDisplayed()));
        appCompatRadioButton.perform(click());

        ViewInteraction environmentSelector = onView(
                allOf(withId(R.id.environmentSpinner), isDisplayed()));
        environmentSelector.perform(click());

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.sunny), withContentDescription("Sonnig"), isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction windSpeedSelector = onView(
                allOf(withId(R.id.windSpeed),
                        withParent(withId(R.id.content)),
                        isDisplayed()));
        windSpeedSelector.perform(click());

        ViewInteraction recyclerView2 = onView(
                allOf(withId(android.R.id.list), isDisplayed()));
        recyclerView2.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction windDirectionSelector = onView(
                allOf(withId(R.id.windDirection),
                        withParent(withId(R.id.content)),
                        isDisplayed()));
        windDirectionSelector.perform(click());

        ViewInteraction recyclerView3 = onView(
                allOf(withId(android.R.id.list), isDisplayed()));
        recyclerView3.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.location), withText("Neufahrn bei Freising"), isDisplayed()));
        textInputEditText.perform(click());

        ViewInteraction textInputEditText2 = onView(
                allOf(withId(R.id.location), withText("Neufahrn bei Freising"), isDisplayed()));
        textInputEditText2.perform(replaceText("Mein Standort"));

        pressBack();

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.action_save), withContentDescription("Speichern"),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.details),
                        withText("Wind: 0 Btf von vorne\nStandort: Mein Standort"),
                        withParent(withId(R.id.content)),
                        isDisplayed()));
        textView.check(matches(withText("Wind: 0 Btf von vorne Standort: Mein Standort")));

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.trainingDate), withText("09.06.2016"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(android.R.id.button1), withText("OK"), isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction textInputEditText3 = onView(
                allOf(withId(R.id.training), withText("Training"),
                        withParent(withId(R.id.training_layout)),
                        isDisplayed()));
        textInputEditText3.perform(click());

        ViewInteraction textInputEditText4 = onView(
                allOf(withId(R.id.training), withText("Training"),
                        withParent(withId(R.id.training_layout)),
                        isDisplayed()));
        textInputEditText4.perform(replaceText("TrainingTitle"));

        ViewInteraction actionMenuItemView2 = onView(
                allOf(withId(R.id.action_save), withContentDescription("Speichern"),
                        isDisplayed()));
        actionMenuItemView2.perform(click());

        ViewInteraction actionMenuItemView3 = onView(
                allOf(withId(R.id.action_show_sidebar), withContentDescription("Schießuhr"),
                        isDisplayed()));
        actionMenuItemView3.perform(click());

        ViewInteraction actionMenuItemView4 = onView(
                allOf(withId(R.id.action_show_sidebar), withContentDescription("Schießuhr"),
                        isDisplayed()));
        actionMenuItemView4.perform(click());

        ViewInteraction actionMenuItemView5 = onView(
                allOf(withId(R.id.action_show_all), withContentDescription("Zeige alle Schüsse"),
                        isDisplayed()));
        actionMenuItemView5.perform(click());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.title), withText("Aktuelle Runde"), isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction imageButton = onView(
                allOf(withContentDescription("Nach oben"),
                        withParent(allOf(withId(R.id.action_bar),
                                withParent(withId(R.id.action_bar_container)))),
                        isDisplayed()));
        imageButton.perform(click());

        ViewInteraction actionMenuItemView6 = onView(
                allOf(withId(R.id.action_share), withContentDescription("Teilen"), isDisplayed()));
        actionMenuItemView6.perform(click());

        ViewInteraction linearLayout = onView(
                allOf(withId(R.id.scoreboard), isDisplayed()));
        linearLayout.perform(click());

        ViewInteraction view = onView(
                allOf(withId(R.id.touch_outside), isDisplayed()));
        view.perform(click());

    }
}
