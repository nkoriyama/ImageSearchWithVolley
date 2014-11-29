package org.nkoriyama.imagesearchwithvolley;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
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
    private PhotoDetailPagerAdapter mPhotoDetailPagerAdapter;

    public static PhotoDetailPagerFragment newInstance(PhotoAdapter photoAdapter, int position) {
        Preconditions.checkNotNull(photoAdapter);
        Preconditions.checkElementIndex(position, photoAdapter.getItemCount());
        final PhotoDetailPagerFragment photoDetailPagerFragment = new PhotoDetailPagerFragment();
        photoDetailPagerFragment.mPhotoAdapter = photoAdapter;
        final Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        photoDetailPagerFragment.setArguments(bundle);
        return photoDetailPagerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mPhotoDetailPagerAdapter = PhotoDetailPagerAdapter.newInstance(getChildFragmentManager(), mPhotoAdapter);

        final Bundle bundle = getArguments();
        assert bundle != null;

        mPosition = bundle.getInt("position");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_photodetailpager, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    private void updateActivity(PhotoInfo photoInfo) {
        final MainActivity activity = (MainActivity) getActivity();
        assert activity != null;

        activity.updateActionBar(photoInfo);
        activity.castPhoto(photoInfo);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewPager.setAdapter(mPhotoDetailPagerAdapter);
        mViewPager.setCurrentItem(mPosition);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());

        updateActivity(mPhotoAdapter.getItem(mPosition));

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mPosition = position;
                updateActivity(mPhotoAdapter.getItem(mPosition));
            }
        });

        final GestureDetector gestureDetector = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public void onLongPress(MotionEvent e) {
                        ((OnPhotoDetailLongPressedListener) getActivity()).onPhotoDetailLongPressed(
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
                        ((OnPhotoDetailDoubleTappedListener) getActivity()).onPhotoDetailDoubleTapped(
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

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    public static interface OnPhotoDetailLongPressedListener {
        public abstract void onPhotoDetailLongPressed(PhotoInfo photoinfo);
    }

    public static interface OnPhotoDetailDoubleTappedListener {
        public abstract void onPhotoDetailDoubleTapped(PhotoInfo photoinfo);
    }
}
