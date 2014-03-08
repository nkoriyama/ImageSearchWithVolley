package org.nkoriyama.imagesearchwithvolley;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.nkoriyama.imagesearchwithvolley.model.FlickrPhotoResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FlickrPhotoListFragment extends PhotoListFragment {
    private final String FLICKR_API_KEY = BuildConfig.FLICKR_API_KEY;

    @Override
    protected String getPhotoListUrl() {
        String url = "";
        try {
            url = "http://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=" +
                    FLICKR_API_KEY +  "&text=" + URLEncoder.encode(mQuery, "utf-8") +
                    "&tags=" + URLEncoder.encode(mQuery, "utf-8") + "&per_page=" + mPerPage +
                    "&page=" + mPage++ + "&format=json&nojsoncallback=1";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return url;
    }

    @Override
    protected void loadMoreItems() {
        mIsLoading = true;
        mRequestQueue.add(new GsonRequest<FlickrPhotoResponse>(
                getPhotoListUrl(),
                FlickrPhotoResponse.class,
                null,
                new Response.Listener<FlickrPhotoResponse>() {
                    @Override
                    public void onResponse(FlickrPhotoResponse flickrPhotoResponse) {
                        if (flickrPhotoResponse != null && flickrPhotoResponse.isOK()) {
                            mPhotoAdapter.addAll(flickrPhotoResponse.getPhotoInfoList());
                            mTotal = flickrPhotoResponse.getTotal();
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
        ));
    }

    @Override
    public void init(String query) {
        super.init(query);
        mInitialPage = 1;
        mPage = mInitialPage;
        mPerPage = 50; // default 100
    }
}
