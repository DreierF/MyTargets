package de.dreier.mytargets.utils.matchers;

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static org.hamcrest.CoreMatchers.is;

public class MatcherUtils {
    public static View getParentViewById(View view, int parentViewId) {
        if (view.getId() == parentViewId) {
            return view;
        } else if (view.getParent() != null && view.getParent() instanceof ViewGroup) {
            return getParentViewById((View) view.getParent(), parentViewId);
        }
        return null;
    }

    public static boolean isInViewHierarchy(View view, View viewToFind) {
        if (view == viewToFind) {
            return true;
        } else if (view.getParent() != null && view.getParent() instanceof ViewGroup) {
            return isInViewHierarchy((View) view.getParent(), viewToFind);
        }
        return false;
    }

    public static ViewInteraction matchToolbarTitle(CharSequence title) {
        return onView(isAssignableFrom(Toolbar.class))
                .check(ViewAssertions.matches(withToolbarTitle(is(title))));
    }

    private static Matcher<Object> withToolbarTitle(
            final Matcher<CharSequence> textMatcher) {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override
            public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getTitle());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with toolbar title: ");
                textMatcher.describeTo(description);
            }
        };
    }

    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }
}
