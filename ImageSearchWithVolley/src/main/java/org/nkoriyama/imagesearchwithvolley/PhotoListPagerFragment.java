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

    private List<PhotoListPagerItem> mPhotoListPagerItems;

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

        mPhotoListPagerItems =
                new ArrayList<PhotoListPagerItem>() {{
                    add(new PhotoListPagerItem(FlickrPhotoListFragment.class, Color.BLUE, Color.GRAY));
                    add(new PhotoListPagerItem(BingPhotoListFragment.class, Color.RED, Color.GRAY));
                    add(new PhotoListPagerItem(AmazonPhotoListFragment.class, Color.YELLOW, Color.GRAY));
                }};
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_photolistpager, container, false);
        ButterKnife.inject(this, view);

        final Bundle bundle;
        if (savedInstanceState != null) {
            bundle = savedInstanceState;
        } else {
            bundle = getArguments();
        }
        assert bundle != null;

        final String query = bundle.getString("query");

        final MainActivity activity = (MainActivity) getActivity();
        assert activity != null;

        final ActionBar actionBar = activity.getSupportActionBar();
        assert actionBar != null;

        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(query);

        mPager.setAdapter(new PhotoListPagerAdapter(getChildFragmentManager(), mPhotoListPagerItems, query));
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

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        final MainActivity activity = (MainActivity) getActivity();
        if (activity == null) {
            return;
        }
        activity.setShareIntent(null);
    }
}
