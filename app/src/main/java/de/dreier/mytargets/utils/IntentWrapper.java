package de.dreier.mytargets.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;

import de.dreier.mytargets.R;
import de.dreier.mytargets.utils.transitions.FabTransform;

public class IntentWrapper {
    @Nullable
    private final Fragment fragment;
    private final Activity activity;
    private final Intent intent;
    private Bundle options = null;

    public IntentWrapper(Activity activity, Class<?> cls) {
        this(activity, new Intent(activity, cls));
    }

    public IntentWrapper(Fragment fragment, Class<?> cls) {
        this(fragment, new Intent(fragment.getContext(), cls));
    }

    public IntentWrapper(Activity activity, Intent intent) {
        this.activity = activity;
        this.fragment = null;
        this.intent = intent;
    }

    public IntentWrapper(Fragment fragment, Intent intent) {
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.intent = intent;
    }

    public IntentWrapper fromFab(View fab) {
        return fromFab(fab, ContextCompat.getColor(getContext(), R.color.colorAccent),
                R.drawable.ic_add_white_24dp);
    }

    private Context getContext() {
        return fragment == null ? activity : fragment.getContext();
    }

    public IntentWrapper fromFab(View fab, int color, int icon) {
        if (Utils.isLollipop()) {
            FabTransform.addExtras(intent, color, icon);
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(activity, fab,
                            getContext().getString(R.string.transition_root_view));
            this.options = options.toBundle();
        }
        return this;
    }

    public void startWithoutAnimation() {
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        if (fragment != null) {
            fragment.startActivity(intent);
        } else {
            activity.startActivity(intent);
        }
    }

    public void start() {
        if(fragment!=null) {
            fragment.startActivity(intent, options);
        } else {
            if (Utils.isLollipop()) {
                activity.startActivity(intent, options);
            } else {
                activity.startActivity(intent);
            }
        }
        animate();
    }

    public void startForResult(int requestCode) {
        if(fragment!=null) {
            fragment.startActivityForResult(intent, requestCode, options);
        } else {
            if (Utils.isLollipop()) {
                activity.startActivityForResult(intent, requestCode, options);
            } else {
                activity.startActivityForResult(intent, requestCode);
            }
        }
        animate();
    }

    private void animate() {
        if (!Utils.isLollipop()) {
            activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }

    public Intent build() {
        return intent;
    }
}
