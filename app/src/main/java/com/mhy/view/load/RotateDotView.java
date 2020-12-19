package com.mhy.view.load;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.mhy.view.R;

import java.util.ArrayList;

/**
 * Created By Mahongyin
 * Date    2020/12/19 21:26
 * <com.zh.cavas.sample.RotateDotView
 *     android:id="@+id/rotate_dot_view"
 *     android:layout_width="28dp"
 *     android:layout_height="28dp"
 *     app:layout_constraintBottom_toBottomOf="parent"
 *     app:layout_constraintLeft_toLeftOf="parent"
 *     app:layout_constraintRight_toRightOf="parent"
 *     app:layout_constraintTop_toTopOf="parent"
 *     app:rdv_dot_mode="scale"
 *     app:rdv_dot_radius="3.8dp"
 *     app:rdv_start_color="@android:color/darker_gray" />
 */
public class RotateDotView extends View implements Runnable {
    /**
     * 普通模式
     */
    private static final int MODE_NORMAL = 1;
    /**
     * 缩放模式
     */
    private static final int MODE_SCALE = 2;

    /**
     * 总旋转角度
     */
    private static final int TOTAL_ROTATION_ANGLE = 360;
    /**
     * 间隔时间
     */
    private static final int INTERVAL_TIME = 65;
    /**
     * View默认最小宽度
     */
    private static final int DEFAULT_MIN_WIDTH = 70;

    /**
     * 控件宽
     */
    private int mViewWidth;
    /**
     * 控件高
     */
    private int mViewHeight;

    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 外接圆的半径
     */
    private float mCircleRadius;
    /**
     * 起始点的颜色
     */
    private int mStartColor;
    /**
     * 终止点的颜色
     */
    private int mEndColor;
    /**
     * 一共多少个点
     */
    private int mDotCount;
    /**
     * 圆点半径
     */
    private float mDotRadius;
    /**
     * 平均角度
     */
    private int mAngle;
    /**
     * 旋转角度，默认和平均角度一样
     */
    private int mRotateAngle;
    /**
     * 每个点的数据
     */
    private ArrayList<Dot> mDots;
    /**
     * 当前旋转到的角度
     */
    private int mCurrentAngle = 0;
    /**
     * 是否自动开始
     */
    private boolean isAutoStart;
    /**
     * 点的模式
     */
    private int mDotMode;

    public RotateDotView(Context context) {
        this(context, null);
    }

    public RotateDotView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotateDotView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        initAttr(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setColor(mStartColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mDotRadius);
    }

    private void initAttr(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RotateDotView, defStyleAttr, 0);
        mStartColor = array.getColor(R.styleable.RotateDotView_rdv_start_color, Color.argb(255, 180, 180, 180));
        //如果不设置endColor，默认取startColor的30%透明度作为endColor
        mEndColor = array.getColor(R.styleable.RotateDotView_rdv_end_color, Color.argb(76, Color.red(mStartColor), Color.green(mStartColor), Color.blue(mStartColor)));
        mDotCount = array.getInt(R.styleable.RotateDotView_rdv_dot_count, 8);
        mDotRadius = array.getDimension(R.styleable.RotateDotView_rdv_dot_radius, dip2px(context, 2.6f));
        isAutoStart = array.getBoolean(R.styleable.RotateDotView_rdv_auto_start, true);
        //计算平均角度，默认是360 / 点的数量，例如8个点，算出来的平均角度就是45度
        mAngle = TOTAL_ROTATION_ANGLE / mDotCount;
        mRotateAngle = array.getInt(R.styleable.RotateDotView_rdv_rotate_angle, mAngle);
        //获取模式
        mDotMode = array.getInt(R.styleable.RotateDotView_rdv_dot_mode, MODE_NORMAL);
        array.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        mCircleRadius = (Math.min(mViewHeight, mViewWidth) / 2f) * 0.8f;
        mDots = generateDot();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //将坐标系原点移动到画布正中心
        canvas.translate(mViewWidth / 2, mViewHeight / 2);
        canvas.rotate(mCurrentAngle);
        for (Dot dot : mDots) {
            mPaint.setColor(dot.color);
            canvas.drawCircle(dot.x, dot.y, dot.dotRadius, mPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(handleMeasure(widthMeasureSpec), handleMeasure(heightMeasureSpec));
    }

    /**
     * 处理MeasureSpec
     */
    private int handleMeasure(int measureSpec) {
        int result = DEFAULT_MIN_WIDTH;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            //处理wrap_content的情况
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isAutoStart) {
            postDelayed(this, INTERVAL_TIME);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(this);
    }

    @Override
    public void run() {
        if (mCurrentAngle >= TOTAL_ROTATION_ANGLE) {
            mCurrentAngle = mCurrentAngle - TOTAL_ROTATION_ANGLE;
        } else {
            //每次叠加一个圆点的角度，就不会觉得在圆圈转动，而是点在切换
            mCurrentAngle += mRotateAngle;
        }
        invalidate();
        postDelayed(this, INTERVAL_TIME);
    }

    /**
     * 生成点
     */
    private ArrayList<Dot> generateDot() {
        //创建颜色估值器
        ArgbEvaluator argbEvaluator = new ArgbEvaluator();
        ArrayList<Dot> points = new ArrayList<>();
        for (int i = 0; i < mDotCount; i++) {
            float currentAngle = i * mAngle;
            //三角函数，计算坐标，注意这里Math的三角函数方法，传入的是弧长，需要乘以Math.PI来将角度换算为弧长，再进行计算
            float x = (float) (mCircleRadius * Math.cos((currentAngle / 180) * Math.PI));
            float y = (float) (mCircleRadius * Math.sin((currentAngle / 180) * Math.PI));
            //估算颜色，计算每个点的颜色
            float fraction = currentAngle / TOTAL_ROTATION_ANGLE;
            int color = (int) argbEvaluator.evaluate(fraction, mEndColor, mStartColor);
            float dotRadius;
            //是否按比例缩放点
            if (mDotMode == MODE_SCALE) {
                dotRadius = (int) (fraction * mDotRadius);
            } else {
                dotRadius = mDotRadius;
            }
            points.add(new Dot(x, y, color, dotRadius));
        }
        return points;
    }

    private static class Dot {
        /**
         * x坐标
         */
        float x;
        /**
         * y坐标
         */
        float y;
        /**
         * 颜色
         */
        int color;
        /**
         * 点的半径，可以一个点一个半径
         */
        float dotRadius;

        Dot(float x, float y, int color, float dotRadius) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.dotRadius = dotRadius;
        }
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}