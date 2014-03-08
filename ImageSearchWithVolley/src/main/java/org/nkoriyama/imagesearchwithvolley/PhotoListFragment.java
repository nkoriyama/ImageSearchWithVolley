package org.nkoriyama.imagesearchwithvolley;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.volley.RequestQueue;

import org.nkoriyama.imagesearchwithvolley.model.PhotoInfo;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public abstract class PhotoListFragment extends Fragment {
    @InjectView(R.id.list)
    GridView mGridView;

    private OnPhotoSelectedListener mOnPhotoSelectedListener;
    protected RequestQueue mRequestQueue;
    protected PhotoAdapter mPhotoAdapter;
    protected String mQuery;
    protected boolean mIsLoading;
    protected int mPage;
    protected int mInitialPage;
    protected int mPerPage;
    protected int mTotal;

    protected abstract String getPhotoListUrl();
    protected abstract void loadMoreItems();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mOnPhotoSelectedListener = (OnPhotoSelectedListener)activity;
        mRequestQueue = ((ImageSearchWithVolley) activity.getApplication()).getRequestQueue();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoAdapter = new PhotoAdapter(getActivity(), R.layout.list_item, new ArrayList<PhotoInfo>());

        if (savedInstanceState != null) {
            mQuery = savedInstanceState.getString("query");
            mIsLoading = false;
            mInitialPage = savedInstanceState.getInt("initialpage");
            mPage = mInitialPage;
            mPerPage = savedInstanceState.getInt("perpage");
            mTotal = 0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photolist, container, false);
        if (view == null) {
            return null;
        }

        ButterKnife.inject(this, view);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mOnPhotoSelectedListener.onPhotoListSelected(mPhotoAdapter, position);
            }
        });

        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!mIsLoading &&
                        totalItemCount > 0 &&
                        totalItemCount <= (int) (0.2 * mPerPage) +
                                (firstVisibleItem + visibleItemCount) &&
                        totalItemCount < mTotal &&
                        totalItemCount == mPhotoAdapter.getCount())
                    loadMoreItems();
            }
        });

        mGridView.setAdapter(mPhotoAdapter);
        if (mPhotoAdapter.getCount() == 0) {
            loadMoreItems();
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("query", mQuery);
        outState.putInt("initialpage", mInitialPage);
        outState.putInt("perpage", mPerPage);
    }

    public void init(String query) {
        if (mPhotoAdapter != null) {
            mPhotoAdapter.clear();
        }
        mQuery = query;
        mIsLoading = false;
        mInitialPage = 0;
        mPage = 0;
        mPerPage = 0;
        mTotal = 0;
    }

    public static interface OnPhotoSelectedListener {
        public abstract void onPhotoListSelected(PhotoAdapter adapter, int position);
    }
}
