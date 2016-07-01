package de.dreier.mytargets.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.InputActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase;
import de.dreier.mytargets.fragments.TrainingFragment;

public class ActivityUtils {
    /**
     * Starts the given activity with the standard animation
     *
     * @param context
     * @param activity Activity to start
     */
    public static void startActivityAnimated(Activity context, Class<?> activity) {
        Intent i = new Intent(context, activity);
        context.startActivity(i);
        context.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public static void startActivityAnimated(Activity context, Class<?> activity, String key, long value) {
        Intent i = new Intent(context, activity);
        i.putExtra(key, value);
        context.startActivity(i);
        context.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public static void startActivityAnimated(Activity context, Class<?> activity, String key, String value) {
        Intent i = new Intent(context, activity);
        i.putExtra(key, value);
        context.startActivity(i);
        context.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public static void startActivityAnimated(Activity context, Class<?> activity, String key, int value) {
        Intent i = new Intent(context, activity);
        i.putExtra(key, value);
        context.startActivity(i);
        context.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public static void openPasseForNewRound(Activity activity, long trainingId, long roundId) {
        if (trainingId != -1) {
            Intent i = new Intent(activity, SimpleFragmentActivityBase.TrainingActivity.class);
            i.putExtra(TrainingFragment.ITEM_ID, trainingId);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            activity.startActivity(i);
        }

        Intent i = new Intent(activity, InputActivity.class);
        i.putExtra(InputActivity.ROUND_ID, roundId);
        i.putExtra(InputActivity.PASSE_IND, 0);
        activity.startActivity(i);
        activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public static void startActivityAnimated(Activity context, Class<?> activity, int requestCode) {
        Intent i = new Intent(context, activity);
        context.startActivityForResult(i, requestCode);
        context.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public static void startActivityAnimated(Activity context, Class<?> activity, int requestCode, String key, Parcelable value) {
        Intent i = new Intent(context, activity);
        i.putExtra(key, value);
        context.startActivityForResult(i, requestCode);
        context.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }
}
