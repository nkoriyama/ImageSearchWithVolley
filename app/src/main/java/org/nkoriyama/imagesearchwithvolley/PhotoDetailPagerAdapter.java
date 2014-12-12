package org.nkoriyama.imagesearchwithvolley;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.google.common.base.Preconditions;

public class PhotoDetailPagerAdapter extends FragmentStatePagerAdapter {
    private PhotoAdapter mPhotoAdapter;


    public PhotoDetailPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public static PhotoDetailPagerAdapter newInstance(FragmentManager fm, PhotoAdapter photoadapter) {
        Preconditions.checkNotNull(fm);
        Preconditions.checkNotNull(photoadapter);
        final PhotoDetailPagerAdapter photoDetailPagerAdapter = new PhotoDetailPagerAdapter(fm);
        photoDetailPagerAdapter.mPhotoAdapter = photoadapter;
        return photoDetailPagerAdapter;
    }

    @Override
    public Fragment getItem(int i) {
        Preconditions.checkElementIndex(i, mPhotoAdapter.getItemCount());
        return PhotoDetailFragment.newInstance(mPhotoAdapter.getItem(i), false);
    }

    @Override
    public int getCount() {
        return mPhotoAdapter.getItemCount();
    }
}
