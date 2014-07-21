package org.nkoriyama.imagesearchwithvolley;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PhotoListPagerFragment extends Fragment {
    @InjectView(R.id.list_pager)
    ViewPager mPager;

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

        final Bundle bundle;
        if (savedInstanceState != null) {
            bundle = savedInstanceState;
        } else {
            bundle = getArguments();
        }
        assert bundle != null;

        final String query = bundle.getString("query");
        mPhotoListPagerAdapter = new PhotoListPagerAdapter(
                getChildFragmentManager(),
                new ArrayList<Class<? extends PhotoListFragment>>() {{
                    add(FlickrPhotoListFragment.class);
                    add(BingPhotoListFragment.class);
                    add(AmazonPhotoListFragment.class);
                }},
                query
        );
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

        mPager.setAdapter(mPhotoListPagerAdapter);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());

        final MainActivity activity = (MainActivity) getActivity();
        assert activity != null;

        final ActionBar actionBar = activity.getActionBar();
        assert actionBar != null;

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(query);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                actionBar.setSelectedNavigationItem(position);
            }
        });

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                mPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        };

        actionBar.removeAllTabs();
        for (int i = 0; i < mPhotoListPagerAdapter.getCount(); i++) {
            actionBar.addTab(actionBar.newTab()
                            .setText(mPhotoListPagerAdapter.getPageTitle(i))
                            .setTabListener(tabListener)
            );
        }

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
