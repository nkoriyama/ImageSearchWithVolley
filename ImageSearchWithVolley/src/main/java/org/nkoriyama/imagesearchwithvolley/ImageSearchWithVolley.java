package org.nkoriyama.imagesearchwithvolley;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class ImageSearchWithVolley extends Application {
    private ImageLoader mImageLoader;
    private RequestQueue mRequestQueue;

    public ImageSearchWithVolley() {
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public void onCreate() {
        super.onCreate();
        mRequestQueue = Volley.newRequestQueue(getApplicationContext(), new OkHttpStack());
        mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache());
    }
}