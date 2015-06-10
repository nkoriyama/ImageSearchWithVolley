package org.nkoriyama.imagesearchwithvolley;


import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;


public class ImageSearchWithVolley extends MultiDexApplication {

    public void onCreate() {
        super.onCreate();

        RequestQueueHelper.init(getApplicationContext());

        if (BuildConfig.USE_CRASHLYTICS) {
            Fabric.with(this, new Crashlytics());
        }


    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}


