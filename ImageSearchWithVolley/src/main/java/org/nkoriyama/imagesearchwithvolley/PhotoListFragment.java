package org.nkoriyama.imagesearchwithvolley;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import org.nkoriyama.imagesearchwithvolley.model.PhotoInfo;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public abstract class PhotoListFragment extends Fragment {
    protected RequestQueue mRequestQueue;
    protected PhotoAdapter mPhotoAdapter;
    protected String mQuery;
    protected int mInitialPage;
    protected int mPerPage;
    protected int mPage;
    protected int mTotal;
    protected boolean mIsLoading;
    @InjectView(R.id.list)
    android.support.v7.widget.RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private OnPhotoSelectedListener mOnPhotoSelectedListener;

    protected static void setBundle(Bundle bundle, String query, int initialPage, int perPage) {
        Preconditions.checkNotNull(bundle);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(query));
        bundle.putString("query", query);
        bundle.putInt("initialPage", initialPage);
        bundle.putInt("perPage", perPage);
    }

    protected abstract void loadMoreItems();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mOnPhotoSelectedListener = (OnPhotoSelectedListener) activity;
        mRequestQueue = ((ImageSearchWithVolley) activity.getApplication()).getRequestQueue();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoAdapter = new PhotoAdapter(getActivity(), R.layout.list_item, new ArrayList<PhotoInfo>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_photolist, container, false);
        ButterKnife.inject(this, view);

        final Bundle bundle;
        if (savedInstanceState != null) {
            bundle = savedInstanceState;
        } else {
            bundle = getArguments();
        }
        assert bundle != null;

        mQuery = bundle.getString("query");
        mInitialPage = bundle.getInt("initialPage");
        mPerPage = bundle.getInt("perPage");
        mPage = mInitialPage;
        mTotal = 0;
        mIsLoading = false;

        mRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnPhotoSelectedListener.onPhotoListSelected(mPhotoAdapter, mRecyclerView.getChildPosition(v));
            }
        });

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = mLayoutManager.getChildCount();
                int totalItemCount = mLayoutManager.getItemCount();

                if (!mIsLoading &&
                        !Strings.isNullOrEmpty(mQuery) &&
                        totalItemCount > 0 &&
                        totalItemCount <= (int) (0.2 * mPerPage) +
                                (firstVisibleItem + visibleItemCount) &&
                        totalItemCount < mTotal &&
                        totalItemCount == mPhotoAdapter.getItemCount())
                    loadMoreItems();
            }
        });

        mRecyclerView.setAdapter(mPhotoAdapter);
        if (mPhotoAdapter.getItemCount() == 0 && !Strings.isNullOrEmpty(mQuery)) {
            loadMoreItems();
        }

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

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
        setBundle(outState, mQuery, mInitialPage, mPerPage);
    }

    public static interface OnPhotoSelectedListener {
        public abstract void onPhotoListSelected(PhotoAdapter adapter, int position);
    }
}
