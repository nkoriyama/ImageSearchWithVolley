package org.nkoriyama.imagesearchwithvolley;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.google.common.base.Preconditions;

import org.nkoriyama.imagesearchwithvolley.model.PhotoInfo;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends ActionBarActivity implements
        PhotoListFragment.OnPhotoSelectedListener,
        PhotoDetailPagerFragment.OnPhotoDetailLongPressedListener,
        PhotoDetailPagerFragment.OnPhotoDetailDoubleTappedListener {
    private MenuItem mSearchItem;
    private MenuItem mShareItem;
    private ShareActionProvider mShareActionProvider;
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);

        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);
        setSupportActionBar(mToolbar);

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
            MenuItemCompat.collapseActionView(mSearchItem);

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

    private void clearSearchSuggestions() {
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
        suggestions.clearHistory();
    }

    private void clearSearchHistory() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        clearSearchSuggestions();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

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

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        assert searchView != null;

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        searchView.setSearchableInfo(searchableInfo);

        mShareItem = menu.findItem(R.id.action_share);
        assert mShareItem != null;

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(mShareItem);
        setShareIntent(null);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
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
    public void onPhotoDetailDoubleTapped(PhotoInfo photoinfo) {
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
