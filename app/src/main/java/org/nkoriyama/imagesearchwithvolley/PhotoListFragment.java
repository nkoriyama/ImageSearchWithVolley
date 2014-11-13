package org.nkoriyama.imagesearchwithvolley;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import org.nkoriyama.imagesearchwithvolley.model.PhotoInfo;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public abstract class PhotoListFragment extends Fragment {
    protected PhotoAdapter mPhotoAdapter;
    protected String mQuery;
    protected int mInitialPage;
    protected int mPerPage;
    protected int mPage;
    protected boolean mHasMoreItems;
    @InjectView(R.id.list)
    RecyclerView mRecyclerView;

    protected static void setBundle(Bundle bundle, String query, int initialPage, int perPage) {
        Preconditions.checkNotNull(bundle);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(query));
        bundle.putString("query", query);
        bundle.putInt("initialPage", initialPage);
        bundle.putInt("perPage", perPage);
    }

    protected abstract void loadMoreItems();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoAdapter = new PhotoAdapter(R.layout.list_item, new ArrayList<PhotoInfo>());
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
        mHasMoreItems = true;

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mPhotoAdapter);

        final GridLayoutManager layoutManager =  new GridLayoutManager(getActivity(), getSpanCount());
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();

                if (!mPhotoAdapter.mIsLoading && mHasMoreItems &&
                        !Strings.isNullOrEmpty(mQuery) &&
                        totalItemCount > 0 &&
                        totalItemCount <= 10 + (firstVisibleItem + visibleItemCount))
                    loadMoreItems();
            }
        });

        if (mPhotoAdapter.getItemCount() == 0 && !Strings.isNullOrEmpty(mQuery)) {
            loadMoreItems();
        }

        return view;
    }

    private int getSpanCount() {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        // TODO: don't use "160" as item width
        // item width 160dip
        return dm.widthPixels / Math.round(160 * dm.density);
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
}