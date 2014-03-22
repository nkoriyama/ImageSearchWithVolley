package org.nkoriyama.imagesearchwithvolley;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.nkoriyama.imagesearchwithvolley.model.PhotoInfo;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PhotoDetailPagerFragment extends Fragment {
    @InjectView(R.id.detail_pager)
    ViewPager mViewPager;

    private OnPhotoDetailLongPressedListener mOnPhotoDetailLongPressedListener;
    private PhotoAdapter mPhotoAdapter;

    public static interface OnPhotoDetailLongPressedListener
    {
        public abstract void onPhotoDetailLongPressed(PhotoInfo photoinfo);
    }

    public static PhotoDetailPagerFragment newInstance(PhotoAdapter photoAdapter, int position) {
        final PhotoDetailPagerFragment photoDetailPagerFragment = new PhotoDetailPagerFragment();
        photoDetailPagerFragment.mPhotoAdapter = photoAdapter;
        final Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        photoDetailPagerFragment.setArguments(bundle);
        return photoDetailPagerFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mOnPhotoDetailLongPressedListener = (OnPhotoDetailLongPressedListener) activity;
    }

    private void setShareIntent(Intent shareIntent) {
        final MainActivity activity = (MainActivity) getActivity();
        if (activity == null) {
            return;
        }
        activity.setShareIntent(shareIntent);
    }

    private void updateShareIntent(PhotoInfo photoInfo) {
        final Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, photoInfo.getShareSubject());
        shareIntent.putExtra(Intent.EXTRA_TEXT, photoInfo.getShareText());

        setShareIntent(shareIntent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_photodetailpager, container, false);
        if (view == null) {
            return null;
        }

        final Bundle bundle;
        if (savedInstanceState != null)
        {
            bundle = savedInstanceState;
        } else
        {
            bundle = getArguments();
        }
        if (bundle == null) {
            return null;
        }

        final int position = bundle.getInt("position") ;

        ButterKnife.inject(this, view);

        mViewPager.setAdapter(PhotoDetailPagerAdapter.newInstance(
                getChildFragmentManager(),
                mPhotoAdapter
        ));
        mViewPager.setCurrentItem(position);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());

        final MainActivity activity = (MainActivity) getActivity();
        if (activity == null) {
            return null;
        }

        final ActionBar actionBar = activity.getActionBar();
        if (actionBar == null) {
            return null;
        }

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        final PhotoInfo photoInfo = mPhotoAdapter.getItem(position);
        actionBar.setTitle(photoInfo.getTitle());
        updateShareIntent(photoInfo);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                final PhotoInfo photoInfo = mPhotoAdapter.getItem(position);
                actionBar.setTitle(photoInfo.getTitle());
                updateShareIntent(photoInfo);
            }
        });

        final GestureDetector gestureDetector = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public void onLongPress(MotionEvent e) {
                        mOnPhotoDetailLongPressedListener.onPhotoDetailLongPressed(
                                mPhotoAdapter.getItem(mViewPager.getCurrentItem())
                        );
                        super.onLongPress(e);
                    }

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }
                });

        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
