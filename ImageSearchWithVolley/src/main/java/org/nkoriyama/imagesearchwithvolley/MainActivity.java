package org.nkoriyama.imagesearchwithvolley;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.ShareActionProvider;

import com.crashlytics.android.Crashlytics;
import com.google.common.base.Preconditions;

import org.nkoriyama.imagesearchwithvolley.model.PhotoInfo;

public class MainActivity extends Activity implements
        PhotoListFragment.OnPhotoSelectedListener,
        PhotoDetailPagerFragment.OnPhotoDetailLongPressedListener {
    private MenuItem mSearchItem;
    private MenuItem mShareItem;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);

        setContentView(R.layout.activity_main);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            if (mSearchItem == null) {
                return;
            }

            String query = intent.getStringExtra(SearchManager.QUERY);

            setTitle(query);
            mSearchItem.collapseActionView();

            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);

            getFragmentManager().popBackStack(null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,
                            PhotoListPagerFragment.newInstance(query),
                            "PHOTO_LIST_PAGER")
                    .commit();
        }
    }

    private void clearSearchHistory() {
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
        suggestions.clearHistory();
    }

    public void setShareIntent(Intent shareIntent) {
        if (mShareItem != null) {
            mShareItem.setVisible(shareIntent != null);
        }
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        mSearchItem = menu.findItem(R.id.action_search);
        assert mSearchItem != null;

        SearchView searchView = (SearchView) mSearchItem.getActionView();
        assert searchView != null;

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        searchView.setSearchableInfo(searchableInfo);

        mShareItem = menu.findItem(R.id.action_share);
        assert mShareItem != null;

        mShareActionProvider = (ShareActionProvider) mShareItem.getActionProvider();
        setShareIntent(null);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_clear_history:
                clearSearchHistory();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPhotoDetailLongPressed(PhotoInfo photoinfo) {
        Preconditions.checkNotNull(photoinfo);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,
                        PhotoDetailFragment.newInstance(photoinfo, true),
                        "PHOTO_DETAIL_ZOOM")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onPhotoListSelected(PhotoAdapter adapter, int position) {
        Preconditions.checkNotNull(adapter);
        Preconditions.checkElementIndex(position, adapter.getCount());
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,
                        PhotoDetailPagerFragment.newInstance(adapter, position),
                        "PHOTO_DETAIL_PAGER")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }
}
