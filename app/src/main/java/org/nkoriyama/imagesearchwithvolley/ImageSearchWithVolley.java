package org.nkoriyama.imagesearchwithvolley;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

public class ImageSearchWithVolley extends Application {
    private GoogleCastManager mGoogleCastManager = null;

    public void onCreate() {
        super.onCreate();

        RequestQueueHelper.init(getApplicationContext());

        if (BuildConfig.USE_CRASHLYTICS) {
            Crashlytics.start(this);
        }
    }

    public synchronized GoogleCastManager getGoogleCastManager() {
        if (mGoogleCastManager == null) {
            mGoogleCastManager = new GoogleCastManager(getApplicationContext());
        }
        return mGoogleCastManager;
    }
}