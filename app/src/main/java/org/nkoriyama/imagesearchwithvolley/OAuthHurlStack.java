package org.nkoriyama.imagesearchwithvolley;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

public class OAuthHurlStack extends OkHttpStack {

    private final OAuthConsumer consumer;

    public OAuthHurlStack(OAuthConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        HttpURLConnection connection = super.createConnection(url);
        try {
            this.consumer.sign(connection);
        } catch (OAuthMessageSignerException | OAuthExpectationFailedException | OAuthCommunicationException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
