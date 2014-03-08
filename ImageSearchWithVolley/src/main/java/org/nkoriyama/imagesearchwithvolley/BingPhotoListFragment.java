package org.nkoriyama.imagesearchwithvolley;


import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.nkoriyama.imagesearchwithvolley.model.BingPhotoResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class BingPhotoListFragment extends PhotoListFragment {
    private final String BING_API_KEY = BuildConfig.BING_API_KEY;

    @Override
    protected String getPhotoListUrl() {
        String url = "";
        try {
            url = "https://api.datamarket.azure.com/Bing/Search/Image?Query=" +
                    "'" + URLEncoder.encode(mQuery, "utf-8") + "'" +
                    "&$format=json" + "&$skip=" + (mPage++ * mPerPage) + "&$top=" + mPerPage;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return url;
    }

    @Override
    protected void loadMoreItems() {
        mIsLoading = true;
        mRequestQueue.add(new GsonRequest<BingPhotoResponse>(
                getPhotoListUrl(),
                BingPhotoResponse.class,
                null,
                new Response.Listener<BingPhotoResponse>() {
                    @Override
                    public void onResponse(BingPhotoResponse bingPhotoResponse) {
                        if (bingPhotoResponse != null && bingPhotoResponse.isOK()) {
                            mPhotoAdapter.addAll(bingPhotoResponse.getPhotoInfoList());
                            mTotal = mPhotoAdapter.getCount() +
                                    (bingPhotoResponse.hasNext() ? mPerPage : 0);
                        } else {
                            mTotal = mPhotoAdapter.getCount();
                        }
                        mIsLoading = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        --mPage;
                        mIsLoading = false;
                        volleyError.printStackTrace();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                Map<String, String> newHeaders = new HashMap<String, String>();
                newHeaders.putAll(headers);
                newHeaders.put("Authorization", "Basic " + Base64.encodeToString(BING_API_KEY.getBytes(), Base64.NO_WRAP));
                return newHeaders;
            }
        });
    }

    @Override
    public void init(String query) {
        super.init(query);
        mInitialPage = 0;
        mPage = mInitialPage;
        mPerPage = 50;
    }
}
