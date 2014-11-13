package org.nkoriyama.imagesearchwithvolley;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.List;

public class PhotoListPagerAdapter extends FragmentPagerAdapter {
    private final List<PhotoListPagerItem> mPhotoListPagerItems;
    private final String mQuery;

    public PhotoListPagerAdapter(FragmentManager fm,
                                 List<PhotoListPagerItem> photoListPagerItems,
                                 String query) {
        super(fm);
        Preconditions.checkNotNull(photoListPagerItems);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(query));

        mPhotoListPagerItems = photoListPagerItems;
        mQuery = query;
    }

    @Override
    public Fragment getItem(int i) {
        Preconditions.checkElementIndex(i, mPhotoListPagerItems.size());
        return mPhotoListPagerItems.get(i).createFragment(mQuery);
    }

    @Override
    public int getCount() {
        return mPhotoListPagerItems.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Preconditions.checkElementIndex(position, mPhotoListPagerItems.size());
        return mPhotoListPagerItems.get(position).getTitle();
    }
}
