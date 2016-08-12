package de.dreier.mytargets.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.InputActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase;
import de.dreier.mytargets.activities.StatisticsActivity;
import de.dreier.mytargets.fragments.RoundFragment;
import de.dreier.mytargets.fragments.TrainingFragment;

/**
 * Utility class for starting activities.
 */
public class ActivityUtils {

    /**
     * Starts the given activity with the standard forward animation (Right in).
     *
     * @param context  Activity context used to fire the intent
     * @param activity Activity to start
     */
    public static void startActivityAnimated(Activity context, Class<?> activity) {
        Intent i = new Intent(context, activity);
        context.startActivity(i);
        context.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public static void showStatistics(Activity context, List<Long> roundIds) {
        Intent i = new Intent(context, StatisticsActivity.class);
        i.putExtra(StatisticsActivity.ROUND_IDS, Utils.toArray(roundIds));
        context.startActivity(i);
        context.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    /**
     * Starts the given activity with the standard forward animation (Right in).
     * Attaches the given key-long value pair to the intent.
     *
     * @param context  Activity context used to fire the intent
     * @param activity Activity to start
     * @param key      Extra key
     * @param value    Extra value
     */
    public static void startActivityAnimated(Activity context, Class<?> activity, String key, long value) {
        Intent i = new Intent(context, activity);
        i.putExtra(key, value);
        context.startActivity(i);
        context.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    /**
     * Starts the given activity with the standard forward animation (Right in).
     * Attaches the given key-int value pair to the intent.
     *
     * @param context  Activity context used to fire the intent
     * @param activity Activity to start
     * @param key      Extra key
     * @param value    Extra value
     */
    public static void startActivityAnimated(Activity context, Class<?> activity, String key, int value) {
        Intent i = new Intent(context, activity);
        i.putExtra(key, value);
        context.startActivity(i);
        context.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    /**
     * Starts the InputActivity to enter scores for the given round.
     * When called after creating a new training TrainingActivity is also started, but not visible
     * to the user. This ensures that the user gets back to the training overview after entering its
     * data. The RoundActivity is also started hidden.
     *
     * @param activity   Activity context used to launch the intent
     * @param trainingId Training to launch the activity for or -1 to indicate that the training activity should not be started.
     * @param roundId    Newly created round to start entering scores
     */
    public static void openPasseForNewRound(Activity activity, long trainingId, long roundId) {
        if (trainingId != -1) {
            Intent i = new Intent(activity, SimpleFragmentActivityBase.TrainingActivity.class);
            i.putExtra(TrainingFragment.ITEM_ID, trainingId);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            activity.startActivity(i);
        }

        Intent i = new Intent(activity, SimpleFragmentActivityBase.RoundActivity.class);
        i.putExtra(RoundFragment.ROUND_ID, roundId);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.startActivity(i);

        i = new Intent(activity, InputActivity.class);
        i.putExtra(InputActivity.ROUND_ID, roundId);
        i.putExtra(InputActivity.PASSE_IND, 0);
        activity.startActivity(i);
        activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    /**
     * Starts the given activity with the standard forward animation (Right in).
     *
     * @param context     Activity context used to fire the intent
     * @param activity    Activity to start
     * @param requestCode Request code for Activity#startActivityForResult
     */
    public static void startActivityAnimated(Activity context, Class<?> activity, int requestCode) {
        Intent i = new Intent(context, activity);
        context.startActivityForResult(i, requestCode);
        context.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    /**
     * Starts the given activity with the standard forward animation (Right in).
     * Attaches the given key-parcelable value pair to the intent.
     *
     * @param context     Activity context used to fire the intent
     * @param activity    Activity to start
     * @param requestCode Request code for Activity#startActivityForResult
     * @param key         Extra key
     * @param value       Extra value
     */
    public static void startActivityAnimated(Activity context, Class<?> activity, int requestCode, String key, Parcelable value) {
        Intent i = new Intent(context, activity);
        i.putExtra(key, value);
        context.startActivityForResult(i, requestCode);
        context.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }
}
