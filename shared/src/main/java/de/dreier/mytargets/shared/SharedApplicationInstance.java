package de.dreier.mytargets.shared;

import android.app.Application;
import android.content.Context;

public class SharedApplicationInstance extends Application {

    protected static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
}