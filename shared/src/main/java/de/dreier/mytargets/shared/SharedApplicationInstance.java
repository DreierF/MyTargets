package de.dreier.mytargets.shared;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;

public class SharedApplicationInstance extends Application {

    protected static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    public static String get(@StringRes int string) {
        return mContext.getString(string);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }
}