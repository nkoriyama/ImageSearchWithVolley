package org.nkoriyama.imagesearchwithvolley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.common.net.HttpHeaders;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by nkoriyama on 2016/03/08.
 */
public class MoshiRequest<T> extends Request<T> {
    private final Moshi moshi = new Moshi.Builder().build();
    private final Class<T> clazz;
    private final Map<String, String> headers;
    private final Response.Listener<T> listener;
    private JsonAdapter<T> adapter;

    public MoshiRequest(String url, Class<T> clazz, Map<String, String> headers,
                        Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.clazz = clazz;
        this.adapter = moshi.adapter(this.clazz);
        this.headers = headers;
        this.listener = listener;

    }
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            final String TYPE_UTF8_CHARSET = "charset=UTF-8";
            String type = response.headers.get(HttpHeaders.CONTENT_TYPE);
            if (type == null) {
                type = TYPE_UTF8_CHARSET;
                response.headers.put(HttpHeaders.CONTENT_TYPE, type);
            } else if (!type.contains("UTF-8")) {
                type += ";" + TYPE_UTF8_CHARSET;
                response.headers.put(HttpHeaders.CONTENT_TYPE, type);
            }

            String json = new String(
                    response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(
                    adapter.fromJson(json), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (IOException e) {
            return Response.error(new ParseError(e));
        }


    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }
}
