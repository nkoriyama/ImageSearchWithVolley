package org.nkoriyama.imagesearchwithvolley;

import com.android.volley.toolbox.HurlStack;
import com.google.common.base.Preconditions;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * An {@link com.android.volley.toolbox.HttpStack HttpStack} implementation which
 * uses OkHttp as its transport.
 */
public class OkHttpStack extends HurlStack {
    private final OkHttpClient client;

    public OkHttpStack() {
        this(new OkHttpClient());
    }

    public OkHttpStack(OkHttpClient client) {
        this.client = Preconditions.checkNotNull(client, "Client must not be null.");
    }

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        return new OkUrlFactory(client).open(url);
    }
}
