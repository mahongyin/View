package com.mhy.view.dragView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.customview.widget.ViewDragHelper;

/**
 * Created By Mahongyin
 * Date    2020/8/21 11:53
 */
public class TestViewGroup extends FrameLayout {
    private static final String TAG = "TestViewGroup";

    private ViewDragHelper mDragHelper;
    private int mDragOriLeft;
    private int mDragOriTop;

    public TestViewGroup(Context context) {
        this(context, null);
    }

    public TestViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return true;
            }

            @Override
            public void onViewCaptured(View capturedChild, int activePointerId) {
                super.onViewCaptured(capturedChild, activePointerId);
                mDragOriLeft = capturedChild.getLeft();
                mDragOriTop = capturedChild.getTop();
                Log.d(TAG, "onViewCaptured: left:" + mDragOriLeft
                        + " top:" + mDragOriTop);
            }

            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {
                super.onEdgeDragStarted(edgeFlags, pointerId);
                Log.d(TAG, "onEdgeDragStarted: " + edgeFlags);
                mDragHelper.captureChildView(getChildAt(getChildCount() - 1), pointerId);
            }

            @Override
            public void onEdgeTouched(int edgeFlags, int pointerId) {
                super.onEdgeTouched(edgeFlags, pointerId);
                Log.d(TAG, "onEdgeTouched: " + edgeFlags);
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return left;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return top;
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);

                View child = getChildAt(0);
                if ( child != null && child == releasedChild ) {
                    mDragHelper.flingCapturedView(getPaddingLeft(),getPaddingTop(),
                            getWidth()-getPaddingRight()-child.getWidth(),
                            getHeight()-getPaddingBottom()-child.getHeight());
                } else {

                    mDragHelper.settleCapturedViewAt((int)mDragOriLeft,(int)mDragOriTop);
                }
                invalidate();
            }
        });

        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_ALL);

    }
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper != null && mDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    public void testSmoothSlide(boolean isReverse) {
        if ( mDragHelper != null ) {
            View child = getChildAt(1);
            if ( child != null ) {
                if ( isReverse ) {
                    mDragHelper.smoothSlideViewTo(child,
                            getLeft(),getTop());
                } else {
                    mDragHelper.smoothSlideViewTo(child,
                            getRight()-child.getWidth(),
                            getBottom()-child.getHeight());
                }
                invalidate();
            }
        }
    }

}
