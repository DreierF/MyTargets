package de.dreier.mytargets;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.test.espresso.FailureHandler;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.action.ViewActions;
import android.support.test.uiautomator.UiDevice;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.assertThat;

public class UITestBase extends InstrumentedTestBase {

    private static Matcher<View> androidHomeMatcher() {
        return allOf(
                withParent(withClassName(is(Toolbar.class.getName()))),
                withClassName(containsString("ImageButton"))
        );
    }

    protected static void navigateUp() {
        onView(androidHomeMatcher()).perform(click());
    }

    protected static UiDevice getUiDevice() {
        return UiDevice.getInstance(getInstrumentation());
    }

    protected static ViewAction clickTarget(final float x, final float y) {
        return new GeneralClickAction(
                Tap.SINGLE,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {
                        return LowLevelActions.getTargetCoordinates(view, new float[]{x, y});
                    }
                },
                Press.FINGER);
    }

    protected static ViewAction holdTapTarget(final float x, final float y) {
        return LowLevelActions.pressAndHold(new float[]{x, y});
    }

    protected static ViewAction releaseTapTarget(final float x, final float y) {
        return LowLevelActions.release(new float[]{x, y});
    }

    @NonNull
    protected static Matcher<View> matchFab() {
        return Matchers.allOf(withParent(withId(R.id.fab)), withClassName(endsWith("ImageView")), isDisplayed());
    }

    public static ViewAction nestedScrollTo() {
        return ViewActions.actionWithAssertions(new NestedScrollToAction());
    }

    public static ViewAssertion assertItemCount(int expectedCount) {
        return new ViewAssertion() {
            public void check (View view, NoMatchingViewException noViewFoundException){
                if (noViewFoundException != null) {
                    throw noViewFoundException;
                }

                RecyclerView recyclerView = (RecyclerView) view;
                RecyclerView.Adapter adapter = recyclerView.getAdapter();
                assertThat(adapter.getItemCount(), is(expectedCount));
            }
        };
    }

    protected void clickActionBarItem(@IdRes int menuItem, @StringRes int title) {
        onView(withId(menuItem)).withFailureHandler(new FailureHandler() {
            @Override
            public void handle(Throwable error, Matcher<View> viewMatcher) {
                openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
                onView(withText(title)).perform(click());
            }
        }).perform(click());
    }
}
