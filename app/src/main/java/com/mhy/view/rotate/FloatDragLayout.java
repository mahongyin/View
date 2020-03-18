package com.mhy.view.rotate;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * @author maple
 *   FloatDragLayout fdlView;
 *   fdlView.setDrag(true); //拖动
 *   fdlView.setNearScreenEdge(true);//贴边
 * @time 2019/1/9
 *         <FloatDragLayout
 *             android:layout_centerVertical="true"
 *             android:layout_alignParentBottom="true"
 *             android:layout_alignParentEnd="true"
 *             android:id="@+id/fdl_view"
 *             android:layout_width="wrap_content"
 *             android:paddingLeft="@dimen/dp_15"
 *             android:paddingRight="@dimen/dp_15"
 *             android:layout_height="@dimen/dp_41"
 *             android:layout_marginBottom="@dimen/dp_30"
 *             android:background="@drawable/shap_corner">

 *         </FloatDragLayout>
 */
public class FloatDragLayout extends FrameLayout {
    private final int OFFSET_ALLOW_DISTANCE = 10;
    private boolean isNearScreenEdge = false;// 是否自动贴边
    private boolean isDrag = true;// 是否可拖拽
    private boolean isMoving;// 正在移动
    RectF paddingRect = new RectF(25, 25, 25, 25);// 距离四周的边距
    PointF startPosition = new PointF();
    PointF lastTouchPoint = new PointF();

    public FloatDragLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public FloatDragLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatDragLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // nothing
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // set default location point
        // View parent = (View) getParent();
        // setLocation(parent.getWidth(), parent.getHeight() / 2);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                startPosition.x = getX() - event.getRawX();
                startPosition.y = getY() - event.getRawY();
                // save last touch point
                lastTouchPoint.x = event.getRawX();
                lastTouchPoint.y = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDrag) {
                    float distanceX = event.getRawX() - lastTouchPoint.x;
                    float distanceY = event.getRawY() - lastTouchPoint.y;
                    if (Math.sqrt(distanceX * distanceX + distanceY * distanceY) > OFFSET_ALLOW_DISTANCE) {
                        isMoving = true;
                        setX(event.getRawX() + startPosition.x);
                        setY(event.getRawY() + startPosition.y);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isMoving) {
                    setPressed(false);
                    // save last touch point
                    lastTouchPoint.x = event.getRawX();
                    lastTouchPoint.y = event.getRawY();

                    if (isNearScreenEdge) {
                        animatorMove(getNearPoint(), 300);
                    } else {
                        animatorMove(fixedValue(new PointF(getX(), getY())), 100);
                    }
                    isMoving = false;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (isMoving) {
                    isMoving = false;
                }
                break;
            default:
                break;
        }
        super.onTouchEvent(event);
        return true;
    }

    // 计算贴边位置
    private PointF getNearPoint() {
        View parent = (View) getParent();
        int parentWidth = parent.getWidth();
        int parentHeight = parent.getHeight();

        float rightDistance = parentWidth - getX();// 距离右侧距离
        float bottomDistance = parentHeight - getY();// 距离底部距离

        float xMinDistance = getX() <= rightDistance ? getX() : rightDistance;
        float yMinDistance = getY() <= bottomDistance ? getY() : bottomDistance;

        float xValue = 0;
        float yValue = 0;
        if (xMinDistance <= yMinDistance) {// 向X边靠拢
            yValue = getY();
            if (getX() > parentWidth / 2) {// 向X右边靠拢
                xValue = parentWidth - getWidth();
            }
        } else {// 向Y边靠拢
            xValue = getX();
            if (getY() > parentHeight / 2) {// 向Y底边靠拢
                yValue = parentHeight - getHeight();
            }
        }
        // 修正值
        return fixedValue(new PointF(xValue, yValue));
    }


    // 修正值
    private PointF fixedValue(PointF point) {
        View parent = (View) getParent();
        return fixedValue(point,
                0 + paddingRect.left, parent.getWidth() - getWidth() - paddingRect.right,
                0 + paddingRect.top, parent.getHeight() - getHeight() - paddingRect.bottom
        );
    }

    // 修正值
    private PointF fixedValue(PointF point, float minX, float maxX, float minY, float maxY) {
        // xValue -> [ minX , maxX ]
        point.x = point.x < minX ? minX : point.x;
        point.x = point.x > maxX ? maxX : point.x;
        // yValue -> [ minY , maxY ]
        point.y = point.y < minY ? minY : point.y;
        point.y = point.y > maxY ? maxY : point.y;
        return point;
    }

    /**
     * 从当前位置A 动画移动到 某个位置B
     *
     * @param targetPoint 目标位置
     * @param duration    动画时间
     */
    private void animatorMove(PointF targetPoint, long duration) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(this, "x", getX(), targetPoint.x),
                ObjectAnimator.ofFloat(this, "y", getY(), targetPoint.y)
        );
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                updateLayoutParams();
            }
        });
        animatorSet.setDuration(duration);
        animatorSet.start();
    }

    private void updateLayoutParams() {
//        LayoutParams layoutParams = new LayoutParams(getWidth(), getHeight());
//        layoutParams.setMargins(left, top, right, bottom);
//        setLayoutParams(layoutParams);
    }

    //------------------------------ public method ------------------------------

    // 是否自动贴边
    public FloatDragLayout setNearScreenEdge(boolean nearScreenEdge) {
        this.isNearScreenEdge = nearScreenEdge;
        if (isNearScreenEdge) {
            animatorMove(getNearPoint(), 300);
        }
        return this;
    }

    // 是否可拖拽
    public FloatDragLayout setDrag(boolean isDrag) {
        this.isDrag = isDrag;
        return this;
    }

    // 设置四周边距
    public FloatDragLayout setPaddingRect(RectF rect) {
        this.paddingRect = rect;
        updateLocation(getX(), getY());
        return this;
    }

    // 更新位置
    public void updateLocation(float x, float y) {
        updateLocation(new PointF(x, y));
    }

    // 更新位置
    public void updateLocation(PointF point) {
        point = fixedValue(point);
        this.setX(point.x);
        this.setY(point.y);
    }

    //------------------------------ view state save ------------------------------

    @Override
    protected Parcelable onSaveInstanceState() {
        SavedViewState state = new SavedViewState(super.onSaveInstanceState());
        state.lastPoint.x = lastTouchPoint.x;
        state.lastPoint.y = lastTouchPoint.y;
        state.isMoving = isMoving;
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);

        if (state instanceof SavedViewState) {
            SavedViewState ss = (SavedViewState) state;
            lastTouchPoint.x = ss.lastPoint.x;
            lastTouchPoint.y = ss.lastPoint.y;
            isMoving = ss.isMoving;
        }
    }

    static class SavedViewState extends BaseSavedState {
        PointF lastPoint = new PointF();
        boolean isMoving;

        SavedViewState(Parcelable superState) {
            super(superState);
        }

        private SavedViewState(Parcel source) {
            super(source);
            lastPoint.x = source.readFloat();
            lastPoint.y = source.readFloat();
            isMoving = source.readByte() == (byte) 1;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(lastPoint.x);
            out.writeFloat(lastPoint.y);
            out.writeByte(isMoving ? (byte) 1 : (byte) 0);
        }

        public static final Creator<SavedViewState> CREATOR = new Creator<SavedViewState>() {
            @Override
            public SavedViewState createFromParcel(Parcel source) {
                return new SavedViewState(source);
            }

            @Override
            public SavedViewState[] newArray(int size) {
                return new SavedViewState[size];
            }
        };

    }

}
