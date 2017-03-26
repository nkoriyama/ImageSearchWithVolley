package org.nkoriyama.imagesearchwithvolley;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class PhotoListFragment extends Fragment implements
        com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout.OnRefreshListener {
    protected PhotoAdapter mPhotoAdapter;
    protected String mQuery;
    protected int mInitialPage;
    protected int mPerPage;
    protected int mPage;
    protected boolean mHasMoreItems;
    @BindDimen(R.dimen.list_item_width) int mListItemWidth;
    @BindView(R.id.list)
    RecyclerView mRecyclerView;
    @BindView(R.id.swiperefreshlayout)
    SwipyRefreshLayout mSwipeRefreshLayout;
    private Unbinder unbinder;


    protected static void setBundle(Bundle bundle, String query, int initialPage, int perPage) {
        Preconditions.checkNotNull(bundle);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(query));
        bundle.putString("query", query);
        bundle.putInt("initialPage", initialPage);
        bundle.putInt("perPage", perPage);
    }

    protected abstract void loadMoreItems();

    protected void refreshing(boolean refreshing) {
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(refreshing);
    }

    protected synchronized void loadItems() {
        if (!Strings.isNullOrEmpty(mQuery) && mHasMoreItems && !mPhotoAdapter.mIsInUse) {
            refreshing(true);
            loadMoreItems();
            // ここでは単純に2秒後にインジケータ非表示
        }
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection swipyRefreshLayoutDirection) {
        loadItems();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPhotoAdapter = new PhotoAdapter();

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_photolist, container, false);
        unbinder = ButterKnife.bind(this, view);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.red,
                R.color.green, R.color.blue,
                R.color.orange);
        // Listenerをセット
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mPhotoAdapter);

        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), getSpanCount());
        /*
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return getSpanCount();
            }
        });
        */
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        // incremental loading
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();

                if (totalItemCount > 0 && totalItemCount <= 10 + (firstVisibleItem + visibleItemCount)) {
                    loadItems();
                }
            }
        });

        if (mPhotoAdapter.getItemCount() == 0) {
            loadItems();
        }
    }

    private int getSpanCount() {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels / mListItemWidth;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(getSpanCount());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setBundle(outState, mQuery, mInitialPage, mPerPage);
    }
}
