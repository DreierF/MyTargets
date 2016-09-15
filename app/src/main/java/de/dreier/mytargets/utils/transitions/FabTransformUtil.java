package de.dreier.mytargets.utils.transitions;

import android.app.Activity;
import android.view.View;

import de.dreier.mytargets.utils.Utils;

public class FabTransformUtil {
    public static void setup(Activity activity, View root) {
        if(Utils.isLollipop()) {
            FabTransform.setup(activity, root);
        }
    }
}
