package de.dreier.mytargets.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;

import de.dreier.mytargets.R;
import de.dreier.mytargets.utils.transitions.FabTransform;

public class IntentWrapper {
    private Bundle options = null;
    private final Activity activity;
    private final Intent intent;

    public IntentWrapper(Activity activity, Intent intent) {
        this.activity = activity;
        this.intent = intent;
    }

    public IntentWrapper(Activity context, Class<?> activity) {
        this.activity = context;
        this.intent = new Intent(context, activity);
    }

    public IntentWrapper fromFab(View fab) {
        return fromFab(fab, ContextCompat.getColor(activity, R.color.colorAccent),
                R.drawable.ic_add_white_24dp);
    }

    public IntentWrapper fromFab(View fab, int color, int icon) {
        if (Utils.isLollipop()) {
            FabTransform.addExtras(intent, color, icon);
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(activity, fab,
                            activity.getString(R.string.transition_root_view));
            this.options = options.toBundle();
        }
        return this;
    }

    public void startWithoutAnimation() {
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.startActivity(intent);
    }

    public void start() {
        if (Utils.isLollipop()) {
            activity.startActivity(intent, options);
        } else {
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }

    public void startForResult(int requestCode) {
        if (Utils.isLollipop()) {
            activity.startActivityForResult(intent, requestCode, options);
        } else {
            activity.startActivityForResult(intent, requestCode);
            activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }
}
