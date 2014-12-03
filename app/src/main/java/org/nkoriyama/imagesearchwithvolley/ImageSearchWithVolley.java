package org.nkoriyama.imagesearchwithvolley;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.cast.CastMediaControlIntent;

public class ImageSearchWithVolley extends Application {
    private static Context sContext = null;
    private static GoogleCastManager sGoogleCastManager = null;
    private static String sReceiverApplicationId = null;

    public synchronized static GoogleCastManager getGoogleCastManager() {
        if (sGoogleCastManager == null) {
            sGoogleCastManager = new GoogleCastManager(sContext, sReceiverApplicationId);
        }
        return sGoogleCastManager;
    }

    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();

        RequestQueueHelper.init(sContext);

        sReceiverApplicationId = CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID;
        //sReceiverApplicationId = "1421B487";

        if (BuildConfig.USE_CRASHLYTICS) {
            Crashlytics.start(this);
        }
    }
}