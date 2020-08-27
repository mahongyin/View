package com.mhy.view.dragView;

/**
 * Created By Mahongyin
 * Date    2020/8/21 11:46
 */

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import androidx.core.view.ViewCompat;
import androidx.core.view.ViewConfigurationCompat;

public class DragViewGroup extends FrameLayout {

    private static final String TAG = "TestViewGroup";

    // 记录手指上次触摸的坐标
    private float mLastPointX;
    private float mLastPointY;

    //用于识别最小的滑动距离
    private int mSlop;

    // 用于标识正在被拖拽的 child，为 null 时表明没有 child 被拖拽
    private View mDragView;

    // 状态分别空闲、拖拽两种
    enum State {
        IDLE,
        DRAGGING
    }

    State mCurrentState;

    public DragViewGroup(Context context) {
        this(context, null);
    }

    public DragViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public DragViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastPointX = ev.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                float diff = Math.abs(ev.getRawX() - mLastPointX);
                // 当手指拖动值大于TouchSlop值时，认为应该进行滚动，拦截子控件的事件
                if (diff > mSlop) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (isPointOnViews(event)) {
                    //标记状态为拖拽，并记录上次触摸坐标
                    mCurrentState = State.DRAGGING;
                    mLastPointX = event.getX();
                    mLastPointY = event.getY();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) (event.getX() - mLastPointX);
                int deltaY = (int) (event.getY() - mLastPointY);
                if (mCurrentState == State.DRAGGING && mDragView != null) {
                    //如果符合条件则对被拖拽的 child 进行位置移动
                    ViewCompat.offsetLeftAndRight(mDragView, deltaX);
                    ViewCompat.offsetTopAndBottom(mDragView, deltaY);
                    mLastPointX = event.getX();
                    mLastPointY = event.getY();
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mCurrentState == State.DRAGGING) {
                    // 标记状态为空闲，并将 mDragView 变量置为 null
                    mCurrentState = State.IDLE;
                    mDragView = null;
                }
                break;
        }
        return true;
    }

    /**
     * 判断触摸的位置是否落在 child 身上
     * 但是有个细节需要优化，当 3 个 child 显示重叠时，触摸它的公共区域，总是最底层的 child 被响应，这有点反人类，正常的操作应该是最上层的最先被响应。那么怎么优化呢？
     * 由于 FrameLayout 的特性，最上面的 child 其实在 ViewGroup 的索引位置最靠后。
     * 因此，我们可以做一小小改动就能修正这个问题，那就是遍历 children 的时候,逆序进行。这样先从顶层检查找到最适配触摸位置的地方
     */
    private boolean isPointOnViews(MotionEvent ev) {
        boolean result = false;
        Rect rect = new Rect();
        for (int i = getChildCount() - 1;i >= 0;i--) {
            View view = getChildAt(i);
            rect.set((int)view.getX(),(int)view.getY(),(int)view.getX()+(int)view.getWidth()
                    ,(int)view.getY()+view.getHeight());

            if (rect.contains((int)ev.getX(),(int)ev.getY())){
                //标记被拖拽的child
                mDragView = view;
                result = true;
                break;
            }
        }

        return  result && mCurrentState != State.DRAGGING;
    }
    @Deprecated
    private boolean isPointOnViews0(MotionEvent ev) {
        boolean result = false;
        Rect rect = new Rect();
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            rect.set((int) view.getX(), (int) view.getY(),
                    (int) view.getX() + view.getWidth(), (int) view.getY() + view.getHeight());

            if (rect.contains((int) ev.getX(), (int) ev.getY())) {
                //标记被拖拽的child
                mDragView = view;
                result = true;
                break;
            }
        }

        return result && mCurrentState != State.DRAGGING;
    }
}
