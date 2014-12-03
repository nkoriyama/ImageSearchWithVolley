package org.nkoriyama.imagesearchwithvolley;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

public class ImageSearchWithVolley extends Application {

    public void onCreate() {
        super.onCreate();

        RequestQueueHelper.init(getApplicationContext());

        if (BuildConfig.USE_CRASHLYTICS) {
            Crashlytics.start(this);
        }
    }
}