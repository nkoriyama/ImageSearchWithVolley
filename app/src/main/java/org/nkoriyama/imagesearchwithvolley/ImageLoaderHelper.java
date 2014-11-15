package org.nkoriyama.imagesearchwithvolley;

import com.android.volley.toolbox.ImageLoader;

public class ImageLoaderHelper {
    private static ImageLoader sImageLoader;

    public static synchronized ImageLoader getImageLoader() {
        if (sImageLoader == null) {
            sImageLoader = new ImageLoader(RequestQueueHelper.getRequestQueue(), new BitmapLruCache());
        }
        return sImageLoader;
    }
}
