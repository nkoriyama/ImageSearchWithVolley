package org.nkoriyama.imagesearchwithvolley;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class ImageSearchWithVolley extends Application {
    private static ImageLoader sImageLoader;
    private static RequestQueue sRequestQueue;
    private static Context sContext;

    public static synchronized ImageLoader getImageLoader() {
        if (sImageLoader == null) {
            sImageLoader = new ImageLoader(getRequestQueue(), new BitmapLruCache());
        }
        return sImageLoader;
    }

    public static synchronized RequestQueue getRequestQueue() {
        if (sRequestQueue == null) {
            sRequestQueue = Volley.newRequestQueue(sContext, new OkHttpStack());
        }
        return sRequestQueue;
    }

    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();
    }
}