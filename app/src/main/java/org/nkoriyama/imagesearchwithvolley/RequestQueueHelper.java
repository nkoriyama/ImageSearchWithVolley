package org.nkoriyama.imagesearchwithvolley;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestQueueHelper {
    private static Context sContext;
    private static RequestQueue sRequestQueue;

    public static void init(Context context) {
        sContext = context;
    }

    public static synchronized RequestQueue getRequestQueue() {
        if (sRequestQueue == null) {
            sRequestQueue = Volley.newRequestQueue(sContext, new OkHttpStack());
        }
        return sRequestQueue;
    }
}
