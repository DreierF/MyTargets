package de.dreier.mytargets;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.parceler.ParcelClass;
import org.parceler.ParcelClasses;

import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.shared.models.ArrowNumber;
import de.dreier.mytargets.shared.models.Bow;
import de.dreier.mytargets.shared.models.Coordinate;
import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Distance;
import de.dreier.mytargets.shared.models.Environment;
import de.dreier.mytargets.shared.models.NotificationInfo;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.SightSetting;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.shared.utils.PasseDrawer;
import de.dreier.mytargets.utils.MyBackupAgent;

/**
 * Application singleton. Gets instantiated exactly once and is used
 * throughout the app whenever a context is needed e.g. to query app
 * resources.
 */
@ParcelClasses({
        @ParcelClass(Arrow.class),
        @ParcelClass(ArrowNumber.class),
        @ParcelClass(Bow.class),
        @ParcelClass(Coordinate.class),
        @ParcelClass(Diameter.class),
        @ParcelClass(Dimension.class),
        @ParcelClass(Distance.class),
        @ParcelClass(Environment.class),
        @ParcelClass(Passe.class),
        @ParcelClass(PasseDrawer.class),
        @ParcelClass(Round.class),
        @ParcelClass(RoundTemplate.class),
        @ParcelClass(Shot.class),
        @ParcelClass(SightSetting.class),
        @ParcelClass(StandardRound.class),
        @ParcelClass(NotificationInfo.class),
        @ParcelClass(Target.class),
        @ParcelClass(Training.class)
})
public class ApplicationInstance extends Application {

    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    public static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public static SharedPreferences getLastSharedPreferences() {
        return mContext.getSharedPreferences(MyBackupAgent.PREFS, 0);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
}