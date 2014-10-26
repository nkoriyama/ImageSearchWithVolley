package org.nkoriyama.imagesearchwithvolley;

import android.app.ActionBar;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
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

        mPager.setAdapter(new PhotoListPagerAdapter(getChildFragmentManager(), mPhotoListPagerItems, query));
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mSlidingTabLayout.setViewPager(mPager);

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

        final MainActivity activity = (MainActivity) getActivity();
        assert activity != null;

        final ActionBar actionBar = activity.getActionBar();
        assert actionBar != null;

        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(query);

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
