package org.nkoriyama.imagesearchwithvolley;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.android.volley.toolbox.NetworkImageView;

public class PinchZoomNetworkImageView extends NetworkImageView {

    private final GestureDetector mGestureDetector;
    private final ScaleGestureDetector mScaleGestureDetector;

    public PinchZoomNetworkImageView(Context context, AttributeSet attributeset) {
        super(context, attributeset);
        final Matrix baseMatrix = new Matrix();
        final Matrix imageMatrix = new Matrix();
        setScaleType(ScaleType.MATRIX);
        mScaleGestureDetector = new ScaleGestureDetector(context,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        imageMatrix.set(baseMatrix);
                        imageMatrix.postScale(
                                detector.getScaleFactor(),
                                detector.getScaleFactor(),
                                detector.getFocusX(),
                                detector.getFocusY());
                        setImageMatrix(imageMatrix);
                        return super.onScale(detector);
                    }

                    @Override
                    public boolean onScaleBegin(ScaleGestureDetector detector) {
                        baseMatrix.set(imageMatrix);
                        return super.onScaleBegin(detector);
                    }
                });
        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                            float distanceX, float distanceY) {
                        imageMatrix.postTranslate(-distanceX, -distanceY);
                        setImageMatrix(imageMatrix);
                        return true;
                    }

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        imageMatrix.reset();
                        setImageMatrix(imageMatrix);
                        return super.onDoubleTap(e);
                    }
                });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        boolean flag = mScaleGestureDetector.isInProgress();
        if (!flag)
            flag = mGestureDetector.onTouchEvent(event);
        if (!flag)
            flag = super.onTouchEvent(event);

        return flag;
    }
}
