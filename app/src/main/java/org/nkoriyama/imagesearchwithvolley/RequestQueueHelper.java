package org.nkoriyama.imagesearchwithvolley;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import oauth.signpost.OAuthConsumer;

public class RequestQueueHelper {
    final private static Map<OAuthConsumer, RequestQueue> sOAuthConsumerRequestQueueMap = new HashMap<>();
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

    public static synchronized RequestQueue getRequestQueue(OAuthConsumer consumer) {
        if (!sOAuthConsumerRequestQueueMap.containsKey(consumer)) {
            sOAuthConsumerRequestQueueMap.put(
                    consumer,
                    Volley.newRequestQueue(sContext, new OAuthHurlStack(consumer)));
        }
        return sOAuthConsumerRequestQueueMap.get(consumer);
    }
}
