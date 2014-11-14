package org.nkoriyama.imagesearchwithvolley;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.common.view.SlidingTabLayout;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PhotoListPagerFragment extends Fragment {
    @InjectView(R.id.list_pager)
    ViewPager mPager;
    @InjectView(R.id.sliding_tabs)
    SlidingTabLayout mSlidingTabLayout;

    private String mQuery;

    private List<PhotoListPagerItem> mPhotoListPagerItems;

    public static PhotoListPagerFragment newInstance(String query) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(query));
        PhotoListPagerFragment photoListPagerFragment = new PhotoListPagerFragment();

        final Bundle bundle = new Bundle();
        setBundle(bundle, query);
        photoListPagerFragment.setArguments(bundle);
        return photoListPagerFragment;
    }

    private static void setBundle(Bundle bundle, String query) {
        Preconditions.checkNotNull(bundle);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(query));
        bundle.putString("query", query);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mPhotoListPagerItems =
                new ArrayList<PhotoListPagerItem>() {{
                    add(new PhotoListPagerItem(FlickrPhotoListFragment.class, Color.argb(222, 0, 0, 0), Color.argb(0, 0, 0, 0)));
                    add(new PhotoListPagerItem(BingPhotoListFragment.class, Color.argb(222, 0, 0, 0), Color.argb(0, 0, 0, 0)));
                    add(new PhotoListPagerItem(AmazonPhotoListFragment.class, Color.argb(222, 0, 0, 0), Color.argb(0, 0, 0, 0)));
                }};

        final Bundle bundle;
        if (savedInstanceState != null) {
            bundle = savedInstanceState;
        } else {
            bundle = getArguments();
        }
        assert bundle != null;

        mQuery = bundle.getString("query");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_photolistpager, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final MainActivity activity = (MainActivity) getActivity();
        assert activity != null;

        final ActionBar actionBar = activity.getSupportActionBar();
        assert actionBar != null;

        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(mQuery);

        mPager.setAdapter(new PhotoListPagerAdapter(getChildFragmentManager(), mPhotoListPagerItems, mQuery));
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());

        mSlidingTabLayout.setViewPager(mPager);

        final Resources.Theme theme = actionBar.getThemedContext().getTheme();
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        final int primaryColor = typedValue.data;
        //theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
        //final int accentColor = typedValue.data;
        //theme.resolveAttribute(R.attr.actionMenuTextColor, typedValue, true);
        //final int textColor = typedValue.data;
        mSlidingTabLayout.setBackgroundColor(primaryColor);

        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                Preconditions.checkElementIndex(position, mPhotoListPagerItems.size());
                return mPhotoListPagerItems.get(position).getIndicatorColor();
            }

            @Override
            public int getDividerColor(int position) {
                Preconditions.checkElementIndex(position, mPhotoListPagerItems.size());
                return mPhotoListPagerItems.get(position).getDividerColor();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        final MainActivity activity = (MainActivity) getActivity();
        assert activity != null;

        activity.setShareIntent(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setBundle(outState, mQuery);
    }
}
