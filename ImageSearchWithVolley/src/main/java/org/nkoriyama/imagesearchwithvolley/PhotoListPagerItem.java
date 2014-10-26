package org.nkoriyama.imagesearchwithvolley;

import android.app.Fragment;

public class PhotoListPagerItem {
    private final Class<? extends PhotoListFragment> mFragmentClass;
    private final int mIndicatorColor;
    private final int mDividerColor;


    PhotoListPagerItem(Class<? extends PhotoListFragment> fragmentClass,
                       int indicatorColor, int dividerColor) {
        mFragmentClass = fragmentClass;
        mIndicatorColor = indicatorColor;
        mDividerColor = dividerColor;
    }

    Fragment createFragment(String query) {
        if (mFragmentClass == FlickrPhotoListFragment.class) {
            return FlickrPhotoListFragment.newInstance(query);
        }
        if (mFragmentClass == BingPhotoListFragment.class) {
            return BingPhotoListFragment.newInstance(query);
        }
        if (mFragmentClass == AmazonPhotoListFragment.class) {
            return AmazonPhotoListFragment.newInstance(query);
        }
        return null;
    }

    CharSequence getTitle() {
        final String fragmentName =  mFragmentClass.getSimpleName();
        return fragmentName.substring(0, fragmentName.indexOf("PhotoListFragment"));
    }

    int getIndicatorColor() {
        return mIndicatorColor;
    }

    int getDividerColor() {
        return mDividerColor;
    }
}
