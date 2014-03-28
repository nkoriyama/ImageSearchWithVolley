package org.nkoriyama.imagesearchwithvolley;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.List;

public class PhotoListPagerAdapter extends FragmentPagerAdapter {
    private final List<Class<? extends PhotoListFragment>> mFragmentClassList;
    private final String mQuery;

    public PhotoListPagerAdapter(FragmentManager fm,
                                 List<Class<? extends PhotoListFragment>> fragmentClassList,
                                 String query) {
        super(fm);
        mFragmentClassList = fragmentClassList;
        mQuery = query;
    }

    @Override
    public Fragment getItem(int i) {
        if (mFragmentClassList.get(i) == FlickrPhotoListFragment.class) {
            return FlickrPhotoListFragment.newInstance(mQuery);
        }
        if (mFragmentClassList.get(i) == BingPhotoListFragment.class) {
            return BingPhotoListFragment.newInstance(mQuery);
        }
        if (mFragmentClassList.get(i) == AmazonPhotoListFragment.class) {
            return AmazonPhotoListFragment.newInstance(mQuery);
        }
        return null;
    }

    @Override
    public int getCount() {
        return  mFragmentClassList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        final String fragmentName = mFragmentClassList.get(position).getSimpleName();
        return fragmentName.substring(0, fragmentName.indexOf("PhotoListFragment"));
    }
}
