package com.wubydax.romcontrol;

import android.app.Application;
import android.content.Context;

/**
 * Created by Anna Berkovitch on 11/06/2016.
 * Application object for context and gc uses
 */
public class MyApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Override
    public void onLowMemory() {
        Runtime.getRuntime().gc();
        super.onLowMemory();
    }

    public static Context getContext() {
        return mContext;
    }
}
