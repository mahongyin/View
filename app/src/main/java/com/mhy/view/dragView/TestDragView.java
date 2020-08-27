package com.mhy.view.dragView;

/**
 * Created By Mahongyin
 * Date    2020/8/21 11:51
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.customview.widget.ViewDragHelper;

public class TestDragView extends FrameLayout {
    private static final String TAG = "TestViewGroup";

    private ViewDragHelper mDragHelper;
    private int mDragOriLeft;
    private int mDragOriTop;

    public TestDragView(Context context) {
        this(context, null);
    }

    public TestDragView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestDragView(Context context, AttributeSet attrs, int defStyleAttr) {
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
                mDragHelper.settleCapturedViewAt(mDragOriLeft, mDragOriTop);
                invalidate();
            }

        });

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

}
