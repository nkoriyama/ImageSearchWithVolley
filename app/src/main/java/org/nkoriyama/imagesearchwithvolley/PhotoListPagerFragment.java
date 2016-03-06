package org.nkoriyama.imagesearchwithvolley;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PhotoListPagerFragment extends Fragment {
    @Bind(R.id.list_pager)
    ViewPager mPager;
    @Bind(R.id.sliding_tabs)
    TabLayout mTabLayout;

    private String mQuery;

    private List<PhotoListPagerItem> mPhotoListPagerItems;

    private PhotoListPagerAdapter mPhotoListPagerAdapter;

    public static PhotoListPagerFragment newInstance(String query) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(query));
        PhotoListPagerFragment photoListPagerFragment = new PhotoListPagerFragment();

        final Bundle bundle = new Bundle();
        bundle.putString("query", query);
        photoListPagerFragment.setArguments(bundle);
        return photoListPagerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle bundle = getArguments();
        assert bundle != null;

        mQuery = bundle.getString("query");

        mPhotoListPagerItems =
                new ArrayList<PhotoListPagerItem>() {{
                    add(new PhotoListPagerItem(FlickrPhotoListFragment.class, Color.argb(255, 255, 255, 255), Color.argb(0, 0, 0, 0)));
                    add(new PhotoListPagerItem(BingPhotoListFragment.class, Color.argb(255, 255, 255, 255), Color.argb(0, 0, 0, 0)));
                    add(new PhotoListPagerItem(YahooPhotoListFragment.class, Color.argb(255, 255, 255, 255), Color.argb(0, 0, 0, 0)));
                    add(new PhotoListPagerItem(AmazonPhotoListFragment.class, Color.argb(255, 255, 255, 255), Color.argb(0, 0, 0, 0)));
                    add(new PhotoListPagerItem(InstagramPhotoListFragment.class, Color.argb(255, 255, 255, 255), Color.argb(0, 0, 0, 0)));
                }};
        mPhotoListPagerAdapter = new PhotoListPagerAdapter(getChildFragmentManager(), mPhotoListPagerItems, mQuery);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_photolistpager, container, false);
        ButterKnife.bind(this, view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ((MainActivity) getActivity()).setToolbarElevation(0);
            mTabLayout.setElevation(10);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final MainActivity activity = (MainActivity) getActivity();
        assert activity != null;

        final ActionBar actionBar = activity.getSupportActionBar();
        assert actionBar != null;

        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(mQuery);

        mPager.setAdapter(mPhotoListPagerAdapter);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());

        mTabLayout.setupWithViewPager(mPager);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);


        final Resources.Theme theme = actionBar.getThemedContext().getTheme();
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        final int primaryColor = typedValue.data;
        //theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
        //final int accentColor = typedValue.data;
        //theme.resolveAttribute(R.attr.actionMenuTextColor, typedValue, true);
        //final int textColor = typedValue.data;
        mTabLayout.setBackgroundColor(primaryColor);

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
        ButterKnife.unbind(this);
    }
}
