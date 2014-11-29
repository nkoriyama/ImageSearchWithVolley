package org.nkoriyama.imagesearchwithvolley;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

public class ImageSearchWithVolley extends Application {
    private GoogleCastHelper mGoogleCastHelper = null;

    public void onCreate() {
        super.onCreate();

        RequestQueueHelper.init(getApplicationContext());

        if (BuildConfig.USE_CRASHLYTICS) {
            Crashlytics.start(this);
        }
    }

    public synchronized GoogleCastHelper getGoogleCastHelper() {
        if (mGoogleCastHelper == null) {
            mGoogleCastHelper = new GoogleCastHelper(getApplicationContext());
        }
        return mGoogleCastHelper;
    }
}