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
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import org.nkoriyama.imagesearchwithvolley.model.PhotoInfo;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends ActionBarActivity implements
        PhotoAdapter.OnPhotoListItemSelectedListener,
        PhotoDetailPagerFragment.OnPhotoDetailLongPressedListener,
        PhotoDetailPagerFragment.OnPhotoDetailDoubleTappedListener {
    private static Context sContext;
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    private MenuItem mSearchItem;
    private MenuItem mShareItem;
    private ShareActionProvider mShareActionProvider;

    public static Context getContext() {
        return sContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sContext = this;

        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);
        setSupportActionBar(mToolbar);

        handleIntent(getIntent());
    }

    @Override
    protected void onStart() {
        super.onStart();
        getGoogleCastHelper().addCallback();
    }

    @Override
    protected void onStop() {
        getGoogleCastHelper().removeCallback();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getGoogleCastHelper().addCallback();
    }

    @Override
    protected void onPause() {
        getGoogleCastHelper().removeCallback();
        super.onPause();
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
            if (Strings.isNullOrEmpty(query)) {
                return;
            }

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
                switch (which) {
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

    public void updateActionBar(PhotoInfo photoInfo) {
        Preconditions.checkNotNull(photoInfo);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setTitle(photoInfo.getTitle());
        actionBar.setTitle("");

        final Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, photoInfo.getShareSubject());
        shareIntent.putExtra(Intent.EXTRA_TEXT, photoInfo.getShareText());
        setShareIntent(shareIntent);
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

        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider =
                (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
        mediaRouteActionProvider.setRouteSelector(getGoogleCastHelper().getMediaRouteSelector());

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
    public void onPhotoListItemSelected(PhotoAdapter adapter, int position) {
        Preconditions.checkNotNull(adapter);
        Preconditions.checkElementIndex(position, adapter.getItemCount());
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,
                        PhotoDetailPagerFragment.newInstance(adapter, position),
                        "PHOTO_DETAIL_PAGER")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    private GoogleCastHelper getGoogleCastHelper() {
        final ImageSearchWithVolley application = (ImageSearchWithVolley) getApplication();
        return application.getGoogleCastHelper();
    }

    public void castPhoto(PhotoInfo photoInfo) {
        final GoogleCastHelper helper = getGoogleCastHelper();
        if (helper.canCast()) {
            MediaMetadata mediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_PHOTO);
            mediaMetadata.putString(MediaMetadata.KEY_TITLE, photoInfo.getTitle());
            MediaInfo mediaInfo = new MediaInfo.Builder(
                    photoInfo.getImageUrl())
                    .setContentType("image/*")
                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    .setMetadata(mediaMetadata)
                    .build();
            helper.loadMedia(mediaInfo);
        }
    }
}
