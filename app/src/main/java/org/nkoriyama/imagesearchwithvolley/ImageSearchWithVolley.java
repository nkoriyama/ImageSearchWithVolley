package org.nkoriyama.imagesearchwithvolley;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.cast.CastMediaControlIntent;

public class ImageSearchWithVolley extends Application {
    private static Context sContext = null;
    private static GoogleCastManager sCastManager = null;
    private static String sReceiverApplicationId = null;

    public synchronized static GoogleCastManager getCastManager() {
        if (sCastManager == null) {
            sCastManager = new GoogleCastManager(sContext, sReceiverApplicationId);
        }
        return sCastManager;
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