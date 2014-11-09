package org.nkoriyama.imagesearchwithvolley;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Preconditions;

import org.nkoriyama.imagesearchwithvolley.model.PhotoInfo;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PhotoDetailPagerFragment extends Fragment {
    @InjectView(R.id.detail_pager)
    ViewPager mViewPager;

    private int mPosition;

    private PhotoAdapter mPhotoAdapter;

    public static PhotoDetailPagerFragment newInstance(PhotoAdapter photoAdapter, int position) {
        Preconditions.checkNotNull(photoAdapter);
        Preconditions.checkElementIndex(position, photoAdapter.getItemCount());
        final PhotoDetailPagerFragment photoDetailPagerFragment = new PhotoDetailPagerFragment();
        photoDetailPagerFragment.mPhotoAdapter = photoAdapter;
        final Bundle bundle = new Bundle();
        setBundle(bundle, position);
        photoDetailPagerFragment.setArguments(bundle);
        return photoDetailPagerFragment;
    }

    private static void setBundle(Bundle bundle, int position) {
        Preconditions.checkNotNull(bundle);
        bundle.putInt("position", position);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private void setShareIntent(Intent shareIntent) {
        final MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        activity.setShareIntent(shareIntent);
    }

    private void updateShareIntent(PhotoInfo photoInfo) {
        Preconditions.checkNotNull(photoInfo);
        final Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, photoInfo.getShareSubject());
        shareIntent.putExtra(Intent.EXTRA_TEXT, photoInfo.getShareText());

        setShareIntent(shareIntent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_photodetailpager, container, false);
        ButterKnife.inject(this, view);

        final Bundle bundle;
        if (savedInstanceState != null) {
            bundle = savedInstanceState;
        } else {
            bundle = getArguments();
        }
        assert bundle != null;

        mPosition = bundle.getInt("position");

        final MainActivity activity = (MainActivity) getActivity();
        assert activity != null;

        final ActionBar actionBar = activity.getSupportActionBar();
        assert actionBar != null;

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mViewPager.setAdapter(PhotoDetailPagerAdapter.newInstance(
                getChildFragmentManager(),
                mPhotoAdapter
        ));
        mViewPager.setCurrentItem(mPosition);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());

        final PhotoInfo photoInfo = mPhotoAdapter.getItem(mPosition);
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
                        ((OnPhotoDetailLongPressedListener) MainActivity.getContext()).onPhotoDetailLongPressed(
                                mPhotoAdapter.getItem(mViewPager.getCurrentItem())
                        );
                        super.onLongPress(e);
                    }

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        ((OnPhotoDetailDoubleTappedListener) MainActivity.getContext()).onPhotoDetailDoubleTapped(
                                mPhotoAdapter.getItem(mViewPager.getCurrentItem())
                        );
                        return super.onDoubleTap(e);
                    }
                }
        );

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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setBundle(outState, mPosition);
    }

    public static interface OnPhotoDetailLongPressedListener {
        public abstract void onPhotoDetailLongPressed(PhotoInfo photoinfo);
    }

    public static interface OnPhotoDetailDoubleTappedListener {
        public abstract void onPhotoDetailDoubleTapped(PhotoInfo photoinfo);
    }
}
