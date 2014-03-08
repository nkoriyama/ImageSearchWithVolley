package org.nkoriyama.imagesearchwithvolley;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

public class PhotoDetailPagerAdapter extends FragmentStatePagerAdapter {
    private PhotoAdapter mPhotoAdapter;


    public PhotoDetailPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public static PhotoDetailPagerAdapter newInstance(FragmentManager fm, PhotoAdapter photoadapter) {
        PhotoDetailPagerAdapter photoDetailPagerAdapter = new PhotoDetailPagerAdapter(fm);
        photoDetailPagerAdapter.mPhotoAdapter = photoadapter;
        return  photoDetailPagerAdapter;
    }


    @Override
    public Fragment getItem(int i) {
        return PhotoDetailFragment.newInstance(mPhotoAdapter.getItem(i), false);
    }

    @Override
    public int getCount() {
        return mPhotoAdapter.getCount();
    }
}
