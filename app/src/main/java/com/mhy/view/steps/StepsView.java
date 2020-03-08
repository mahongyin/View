package com.mhy.view.steps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;


import com.mhy.view.R;

import java.util.ArrayList;
import java.util.List;

import com.mhy.view.utils.DensityUtil;

/**
 * 签到打卡View
 */
public class StepsView extends View {

    /**
     * 动画执行的时间 230毫秒
     */
    private final static int ANIMATION_TIME = 230;
    /**
     * 动画执行的间隔次数
     */
    private final static int ANIMATION_INTERVAL = 10;

    /**
     * 线段的高度
     */
    private float mCompletedLineHeight = DensityUtil.dp2px(getContext(), 2f);

    /**
     * 图标宽度
     */
    private float mIconWidth = DensityUtil.dp2px(getContext(), 17f);
    /**
     * 图标的高度
     */
    private float mIconHeight = DensityUtil.dp2px(getContext(), 32f);
    /**
     * UP宽度
     */
    private float mUpWidth = DensityUtil.dp2px(getContext(), 32f);
    /**
     * up的高度
     */
    private float mUpHeight = DensityUtil.dp2px(getContext(), 24f);

    /**
     * 线段长度
     */
    private float mLineWidth;// = dp2px(getContext(), 32f);

    /**
     * 已经完成的图标圈 已完成
     */
    private Drawable mCompleteIcon;
    /**
     * 正在进行的图标圈 待签 补签
     */
    private Drawable mAttentionIcon;
    /**
     * 默认的图标圈 未签
     */
    private Drawable mDefaultIcon;
    /**
     * UP图标
     */
    private Drawable mUpIcon, mUpIcon7, mLostUpIcon;
    /**
     * 图标中心点Y
     */
    private float mCenterY;
    /**
     * 线段的左上方的Y
     */
    private float mLeftY;
    /**
     * 线段的右下方的Y
     */
    private float mRightY;

    /**
     * 数据源
     */
    private List<StepBean> mStepBeanList;
    private int mStepNum = 0;

    /**
     * 图标中心点位置
     */
    private List<Float> mCircleCenterPointPositionList;
    /**
     * Bitmap图Paint
     */
    private Paint mBitmapPaint;
    /**
     * 未完成的线段Paint
     */
    private Paint mUnCompletedPaint;
    /**
     * 完成的线段paint
     */
    private Paint mCompletedPaint;
    /**
     * Up上面完成文字 白色字
     */
    private Paint mTextNumberPaint;
    /**
     * Up上面未完文字 补签 红色字
     */
    private Paint mTextUnNumberPaint;
    /**
     * Up上面文字 待签 +5 黄色字
     */
    private Paint mTextTodoNumberPaint;
    /**
     * 周几下面文字
     */
    private Paint mTextDayPaint;
    /**
     * 未完成线颜色
     */
    private int mUnCompletedLineColor = ContextCompat.getColor(getContext(), R.color.c_D8D8D8);
    /**
     * 完成线的颜色
     */
    private int mCompletedLineColor = ContextCompat.getColor(getContext(), R.color.c_FE4E45);
    /**
     * up Text待签颜色
     */
    private int mTodoCompletedTextColor = ContextCompat.getColor(getContext(), R.color.c_d5a872);
    /**
     * up Text完成颜色
     */
    private int mCompletedTextColor = ContextCompat.getColor(getContext(), R.color.white);
    /**
     * up Text补签 颜色 红色
     */
    private int mLostTextColor = ContextCompat.getColor(getContext(), R.color.c_FE4E45);

    /**
     * up积分值颜色 up Text完成颜色
     */
    private int mCurrentTextColor = ContextCompat.getColor(getContext(), R.color.white);
    /**
     * 周几天数颜色
     */
    private int mUnCompletedDayTextColor = ContextCompat.getColor(getContext(), R.color.cp_color_gray_dark);

    /**
     * 是否执行动画
     */
    private boolean isAnimation = false;//false

    /**
     * 记录重绘次数
     */
    private int mCount = 0;

    /**
     * 执行动画线段每次绘制的长度，线段的总长度除以总共执行的时间乘以每次执行的间隔时间
     */
    private float mAnimationWidth = (mLineWidth / ANIMATION_TIME) * ANIMATION_INTERVAL;

    /**
     * 执行动画的位置
     */
    private int mPosition;
    private int[] mMax;

    public StepsView(Context context) {
        this(context, null);
        //add 设置获取焦点点击
        setFocusable(true);
    }

    public StepsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StepsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.StepsView);
        //周几文字颜色
        mUnCompletedDayTextColor = array.getColor(R.styleable.StepsView_downColor, 0xff000000);
        mTodoCompletedTextColor = array.getColor(R.styleable.StepsView_todoCompletedTextColor, ContextCompat.getColor(getContext(), R.color.c_d5a872));
//是否显示动画
        isAnimation = array.getBoolean(R.styleable.StepsView_isAnimation, false);
        mLostTextColor = array.getColor(R.styleable.StepsView_lostTextColor, ContextCompat.getColor(getContext(), R.color.c_FE4E45));
        mCompletedTextColor = array.getColor(R.styleable.StepsView_completedTextColor, ContextCompat.getColor(getContext(), R.color.white));
        array.recycle();


        init2();//计算屏幕宽度 适配
        init();
    }

    private int displayMetricsWidth;//当前屏幕的宽度
    private int displayMetricsHeight;//当前屏幕的高度

    private float scaleX;//屏幕宽度缩放倍数
//    private float scaleY;//屏幕高度缩放倍数

//    private boolean flag;//是否已经测量，该变量是个标识，在RelativeLayout中，当子控件不满足父容器分配的宽高大小时，会再次进行测量。

    //标准值  这里以UI设计师给的设计标准为准  目前市面上主流分辨率为 1080x1920
    public static float STANDARD_WIDTH = 1080f;
    public static float STANDARD_HEIGHT = 1920f;

    private void init2() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        //注意：获取屏幕的高度是除了底部虚拟按键栏的高度
        wm.getDefaultDisplay().getMetrics(displayMetrics);//忽略Navigationbar高度
        //wm.getDefaultDisplay().getRealMetrics(displayMetrics);//获取整个屏幕宽高

        displayMetricsWidth = displayMetrics.widthPixels;
        displayMetricsHeight = displayMetrics.heightPixels;

        scaleX = displayMetricsWidth / STANDARD_WIDTH;//计算屏幕宽度缩放因子
//        scaleY = displayMetricsHeight / STANDARD_HEIGHT;//计算屏幕高度缩放因子

//        mLineWidth = dp2px(getContext(), 32f * scaleX*440/dpi(getContext()));
//        mLineWidth = dp2px(getContext(), 26.6f *480/dpi(getContext()));
        //1080px 480dpi 360dp  对应   6段线
//       float desx= 1080/density(getContext())/360;//当前屏幕宽度dp 比 360
//        mLineWidth=dp2px(getContext(),(desx)*26.6f);
//        mLineWidth=dp2px(getContext(),(desx==1?26.6f:desx<1?26.6f-desx*6:desx*6+26.6f));

//if (dpi(getContext())>470&&dpi(getContext())<490){
//    mLineWidth=dp2px(getContext(),26.6f);
//}else if (dpi(getContext())>430&&dpi(getContext())<455){
//    mLineWidth=dp2px(getContext(),32f);
//}
        mLineWidth = DensityUtil.dp2px(getContext(), (displayMetricsWidth / DensityUtil.density(getContext()) - 200) / 6);
        //    //获取屏幕宽高
//        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
//        int width = manager.getDefaultDisplay().getWidth();
//        int heigh = manager.getDefaultDisplay().getHeight();
//        Log.e("屏幕大小" + width + "," + heigh + "," + displayMetricsWidth + "," + displayMetricsHeight + "scaleX系数" + scaleX);
//        //使比例宽度  1080屏幕宽度dp
//        mLineWidth=dp2px(getContext(), 32f*width/1080);
    }

//    public void getSreenDpi(){
//        DisplayMetrics metrics = new DisplayMetrics();
//        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        // 屏幕的分辨率
//        int width = metrics.widthPixels;//本屏幕&1080
//        int height = metrics.heightPixels;//本屏幕&1920

//    //获取屏幕宽高
//    WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//    int width = manager.getDefaultDisplay().getWidth();
//    int heigh = manager.getDefaultDisplay().getHeight();

//    }

    /**
     * init
     */
    private void init() {
        mStepBeanList = new ArrayList<>();

        mCircleCenterPointPositionList = new ArrayList<>();

        //未完成-线画笔
        mUnCompletedPaint = new Paint();
        mUnCompletedPaint.setAntiAlias(true);
        mUnCompletedPaint.setColor(mUnCompletedLineColor);
        mUnCompletedPaint.setStrokeWidth(2);
        mUnCompletedPaint.setStyle(Paint.Style.FILL);

        //已完成-线画笔
        mCompletedPaint = new Paint();
        mCompletedPaint.setAntiAlias(true);
        mCompletedPaint.setColor(mCompletedLineColor);
        mCompletedPaint.setStrokeWidth(2);
        mCompletedPaint.setStyle(Paint.Style.FILL);

        // up未签补签 text paint 上  红色
        mTextUnNumberPaint = new Paint();
        mTextUnNumberPaint.setAntiAlias(true);
        mTextUnNumberPaint.setColor(mLostTextColor);
        mTextUnNumberPaint.setStyle(Paint.Style.FILL);
        //从x轴居中
        mTextUnNumberPaint.setTextAlign(Paint.Align.CENTER);
        mTextUnNumberPaint.setTextSize(DensityUtil.sp2px(getContext(), 12f));

        // up 已完成text paint  上  白色
        mTextNumberPaint = new Paint();
        mTextNumberPaint.setAntiAlias(true);
        mTextNumberPaint.setColor(mCompletedTextColor);
        mTextNumberPaint.setStyle(Paint.Style.FILL);
        //从x轴居中
        mTextNumberPaint.setTextAlign(Paint.Align.CENTER);
        mTextNumberPaint.setTextSize(DensityUtil.sp2px(getContext(), 12f));

        //up待签  text paint  上 黄色
        mTextTodoNumberPaint = new Paint();
        mTextTodoNumberPaint.setAntiAlias(true);
        mTextTodoNumberPaint.setColor(mTodoCompletedTextColor);
        mTextTodoNumberPaint.setStyle(Paint.Style.FILL);
        //从x轴居中
        mTextTodoNumberPaint.setTextAlign(Paint.Align.CENTER);
        mTextTodoNumberPaint.setTextSize(DensityUtil.sp2px(getContext(), 12f));

        //周week text paint 下
        mTextDayPaint = new Paint();
        mTextDayPaint.setAntiAlias(true);
        mTextDayPaint.setColor(mUnCompletedDayTextColor);
        mTextDayPaint.setStyle(Paint.Style.FILL);
        mTextDayPaint.setTextSize(DensityUtil.sp2px(getContext(), 12f));

        //已经完成的icon
        mCompleteIcon = ContextCompat.getDrawable(getContext(), R.drawable.ring_sigin);
        //正在进行的icon
        mAttentionIcon = ContextCompat.getDrawable(getContext(), R.drawable.ring_unsigin);
        //未完成的icon
        mDefaultIcon = ContextCompat.getDrawable(getContext(), R.drawable.ring_unsigin);
        //UP的icon 红
        mUpIcon = ContextCompat.getDrawable(getContext(), R.drawable.shape_aval);
        //待签 和 补签  黄
        mLostUpIcon = ContextCompat.getDrawable(getContext(), R.drawable.shape_aval_yellow);
        //最会一天
        mUpIcon7 =DensityUtil.bitmap2Drawable(getContext(),
                imageScale(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_gift),
                        DensityUtil.dp2px(getContext(), 25f),
                        DensityUtil.dp2px(getContext(), 24f)));
        //画图bitmap画笔
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setColor(mUnCompletedLineColor);
        mBitmapPaint.setStyle(Paint.Style.STROKE);
//        mBitmapPaint.setStrokeWidth(2);
        //其绘制bitmap
//        Bitmap bitmap=imageScale(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_gift),dp2px(getContext(),25f),dp2px(getContext(),24f));
//        canvas.drawBitmap(bitmap,preComplectedXPosition, mLeftY,mBitmapPaint);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setChange();
    }

    private void setChange() {
        //图标的中中心Y点
        mCenterY = DensityUtil.dp2px(getContext(), 32f) + mIconHeight / 2;
        //获取左上方Y的位置，获取该点的意义是为了方便画矩形左上的Y位置
        mLeftY = mCenterY - (mCompletedLineHeight / 2);
        //获取右下方Y的位置，获取该点的意义是为了方便画矩形右下的Y位置
        mRightY = mCenterY + mCompletedLineHeight / 2;

        //计算图标中心点
        mCircleCenterPointPositionList.clear();
        //第一个点距离父控件左边14.5dp
        float size = mIconWidth / 2 + DensityUtil.dp2px(getContext(), 23f);
        mCircleCenterPointPositionList.add(size);

        for (int i = 1; i < mStepNum; i++) {
            //从第二个点开始，每个点距离上一个点为图标的宽度加上线段的23dp的长度
            size = size + mIconWidth + mLineWidth;
            mCircleCenterPointPositionList.add(size);
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mStepBeanList.size() != 0) {
            if (isAnimation) {
                drawSign(canvas);
            } else {
                drawUnSign(canvas);
            }
        }
    }

    /**
     * 调整图片大小
     *
     * @param bitmap 源
     * @param dst_w  输出宽度
     * @param dst_h  输出高度
     * @return
     */
    public static Bitmap imageScale(Bitmap bitmap, int dst_w, int dst_h) {
        int src_w = bitmap.getWidth();
        int src_h = bitmap.getHeight();
        float scale_w = ((float) dst_w) / src_w;
        float scale_h = ((float) dst_h) / src_h;
        Matrix matrix = new Matrix();
        matrix.postScale(scale_w, scale_h);
        Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix, true);
        return dstbmp;
        //精确缩放到指定大小
//        Bitmap thumbImgNow = Bitmap.createScaledBitmap(bitmap,dst_w,dst_h, true);

    }

    String upText;

    /**
     * 绘制签到(伴随签到动画)
     */
    @SuppressLint("DrawAllocation")
    private void drawSign(Canvas canvas) {
        for (int i = 0; i < mCircleCenterPointPositionList.size(); i++) {
            //绘制线段
            float preComplectedXPosition = mCircleCenterPointPositionList.get(i) + mIconWidth / 2;
            if (i != mCircleCenterPointPositionList.size() - 1) {
                //最后一条不需要绘制
                if (mStepBeanList.get(i + 1).getState() == StepBean.STEP_COMPLETED) {
                    //下一个是已完成，当前才需要绘制线
                    canvas.drawRect(preComplectedXPosition, mLeftY, preComplectedXPosition + mLineWidth,
                            mRightY, mCompletedPaint);
                } else {
                    //当前位置执行动画
                    if (i == mPosition - 1) {
                        //红线色开始绘制的地方,
                        float endX = preComplectedXPosition + mAnimationWidth * (mCount / ANIMATION_INTERVAL);
                        //绘制
                        canvas.drawRect(preComplectedXPosition, mLeftY, endX,
                                mRightY, mCompletedPaint);
                        //绘制
                        canvas.drawRect(endX, mLeftY, preComplectedXPosition + mLineWidth,
                                mRightY, mUnCompletedPaint);
                    } else {
                        canvas.drawRect(preComplectedXPosition, mLeftY, preComplectedXPosition + mLineWidth,
                                mRightY, mUnCompletedPaint);
                    }
                }
            }

            //绘制图标圈
            float currentComplectedXPosition = mCircleCenterPointPositionList.get(i);
            Rect rect = new Rect((int) (currentComplectedXPosition - mIconWidth / 2),
                    (int) (mCenterY - mIconHeight / 2),
                    (int) (currentComplectedXPosition + mIconWidth / 2),
                    (int) (mCenterY + mIconHeight / 2));

            StepBean stepsBean = mStepBeanList.get(i);
//圈
            if (i == mPosition && mCount == ANIMATION_TIME) {
                //当前需要绘制
                mCompleteIcon.setBounds(rect);
                mCompleteIcon.draw(canvas);
            } else {
                if (stepsBean.getState() == StepBean.STEP_UNDO || stepsBean.getState() == StepBean.STEP_TODO) {
                    mDefaultIcon.setBounds(rect);
                    mDefaultIcon.draw(canvas);
                } else if (stepsBean.getState() == StepBean.STEP_CURRENT) {
                    mAttentionIcon.setBounds(rect);
                    mAttentionIcon.draw(canvas);
                } else if (stepsBean.getState() == StepBean.STEP_COMPLETED) {
                    mCompleteIcon.setBounds(rect);
                    mCompleteIcon.draw(canvas);
                }
            }

            //绘制 up Text 颜色
            if (stepsBean.getState() == 1 || stepsBean.getState() == -1 || (i == mPosition && mCount == ANIMATION_TIME)) {
                //已经完成了-->+5 或者是当前动画完成并且需要当前位置需要改变
                //是up的需要 白色
                mTextNumberPaint.setColor(mCompletedTextColor);
            } else {
                if (stepsBean.getState() == 2) {
                    //补签颜色红  待签颜色黄
                    mTextUnNumberPaint.setColor(mTodoCompletedTextColor);
                }
                if (stepsBean.getState() == 0) {
                    //补签颜色红
                    mTextUnNumberPaint.setColor(mLostTextColor);
                }
            }

            //绘制UP draw背景
            if (i != mCircleCenterPointPositionList.size() - 1) {
                //最后一个用bitmap
                if (stepsBean.getState() == 1 || stepsBean.getState() == -1) {
                    //已签 红背景 白+5  //今未签 红背景 白字签到
                    Rect rectUp =
                            new Rect((int) (currentComplectedXPosition - mUpWidth / 2),
                                    (int) (mCenterY - mIconHeight / 2 - DensityUtil.dp2px(getContext(), 8f) - mUpHeight),
                                    (int) (currentComplectedXPosition + mUpWidth / 2),
                                    (int) (mCenterY - mIconHeight / 2 - DensityUtil.dp2px(getContext(), 1f)));
                    mUpIcon.setBounds(rectUp);
                    mUpIcon.draw(canvas);
                } else if (stepsBean.getState() == 0 || stepsBean.getState() == 2) {
                    //黄背景 未签补签、+5 //后边待签
                    Rect rectUp = new Rect(
                            /*左*/
                            (int) (currentComplectedXPosition - mUpWidth / 2),
                            /*上*/
                            (int) (mCenterY - mIconHeight / 2 - DensityUtil.dp2px(getContext(), 8f) - mUpHeight),
                            /*右*/
                            (int) (currentComplectedXPosition + mUpWidth / 2),
                            /*下*/
                            (int) (mCenterY - mIconHeight / 2 - DensityUtil.dp2px(getContext(), 1f)));

                    mLostUpIcon.setBounds(rectUp);
                    mLostUpIcon.draw(canvas);

                }
                //1表示已签到 0表示未 -1今天(只有未签才有-1今签了就变1了) 2待签
                //up text 内容
                if (stepsBean.getState() == 1 || stepsBean.getState() == -1) {
//                    已签 //UP积分文字 白色字 签到
                    canvas.drawText(stepsBean.getNumber(),
//                            currentComplectedXPosition - dp2px(getContext(), 8f),
                            currentComplectedXPosition,
//                            mCenterY / 2 - dp2px(getContext(), 0.5f),
                            mCenterY - mIconHeight / 2 - DensityUtil.dp2px(getContext(), 4f) - mUpHeight / 2 + Math.abs(mTextNumberPaint.ascent() + mTextNumberPaint.descent()) / 2,
                            mTextNumberPaint);
                } else if (stepsBean.getState() == 0) {
                    //未签到 补签 红色字
                    canvas.drawText(stepsBean.getNumber(),
//                            currentComplectedXPosition - dp2px(getContext(), 8f),
                            currentComplectedXPosition,
//                            mCenterY / 2 - dp2px(getContext(), 0.5f),
                            mCenterY - mIconHeight / 2 - DensityUtil.dp2px(getContext(), 4f) - mUpHeight / 2 + Math.abs(mTextNumberPaint.ascent() + mTextNumberPaint.descent()) / 2,
                            mTextUnNumberPaint);
                } else if (stepsBean.getState() == 2) {
//                    待签 黄色字
                    canvas.drawText(stepsBean.getNumber(),
//                            currentComplectedXPosition - dp2px(getContext(), 8f),
                            currentComplectedXPosition,
//                            mCenterY / 2 - dp2px(getContext(), 0.5f),
                            mCenterY - mIconHeight / 2 - DensityUtil.dp2px(getContext(), 4f) - mUpHeight / 2 + Math.abs(mTextNumberPaint.ascent() + mTextNumberPaint.descent()) / 2,
                            mTextTodoNumberPaint);
                }
            } else {
                //最后一天已签到
                if (stepsBean.getState() == 1) {
                    //最后一天签到啦 就显示积分
                    //绘制图标圈
//                    float currentComplectedXPosition = mCircleCenterPointPositionList.get(i);
                    Rect rect7 = new Rect((int) (currentComplectedXPosition - mIconWidth / 2),
                            (int) (mCenterY - mIconHeight / 2),
                            (int) (currentComplectedXPosition + mIconWidth / 2),
                            (int) (mCenterY + mIconHeight / 2));
//圈
                    if (i == mPosition && mCount == ANIMATION_TIME) {
                        //当前需要绘制
                        mCompleteIcon.setBounds(rect7);
                        mCompleteIcon.draw(canvas);
                    } else {
                        if (stepsBean.getState() == StepBean.STEP_COMPLETED) {
                            mCompleteIcon.setBounds(rect7);
                            mCompleteIcon.draw(canvas);
                        }
                    }

                    //绘制 up Text 颜色
                    if (stepsBean.getState() == 1 || (i == mPosition && mCount == ANIMATION_TIME)) {
                        //已经完成了-->+5 或者是当前动画完成并且需要当前位置需要改变
                        //是up的需要 白色
                        mTextNumberPaint.setColor(mCompletedTextColor);
                    }

                    //绘制UP draw背景
                    //已签 红背景 白+5  //今未签 红背景 白字签到
                    Rect rectUp =
                            new Rect((int) (currentComplectedXPosition - mUpWidth / 2),
                                    (int) (mCenterY - mIconHeight / 2 - DensityUtil.dp2px(getContext(), 8f) - mUpHeight),
                                    (int) (currentComplectedXPosition + mUpWidth / 2),
                                    (int) (mCenterY - mIconHeight / 2 - DensityUtil.dp2px(getContext(), 1f)));
                    mUpIcon.setBounds(rectUp);
                    mUpIcon.draw(canvas);

                    //Up文字颜色
                    canvas.drawText(stepsBean.getNumber(),
//                            currentComplectedXPosition - dp2px(getContext(), 8f),
                            currentComplectedXPosition,
//                            mCenterY / 2 - dp2px(getContext(), 0.5f),
                            mCenterY - mIconHeight / 2 - DensityUtil.dp2px(getContext(), 4f) - mUpHeight / 2 + Math.abs(mTextNumberPaint.ascent() + mTextNumberPaint.descent()) / 2,
                            mTextNumberPaint);
                } else {
                    //最后一天bitmap
                    Rect rectUp =
                            new Rect((int) (currentComplectedXPosition - mUpWidth / 2),
                                    (int) (mCenterY - mIconHeight / 2 - DensityUtil.dp2px(getContext(), 8f) - mUpHeight),
                                    (int) (currentComplectedXPosition + mUpWidth / 2),
                                    (int) (mCenterY - mIconHeight / 2 - DensityUtil.dp2px(getContext(), 1f)));
                    mUpIcon7.setBounds(rectUp);
                    //大小
                    mUpIcon7.draw(canvas);
                }
            }
            //周几天数文字
            canvas.drawText(stepsBean.getDay(),
                    currentComplectedXPosition - DensityUtil.dp2px(getContext(), 12f),
                    mCenterY + DensityUtil.dp2px(getContext(), 30f),
                    mTextDayPaint);
        }

        //记录重绘次数
        mCount = mCount + ANIMATION_INTERVAL;
        if (mCount <= ANIMATION_TIME) {
            //引起重绘
            postInvalidate();
        } else {
            //重绘完成
            isAnimation = false;
            mCount = 0;
        }
    }

    /**
     * 绘制初始状态的view 无动画
     */
    @SuppressLint("DrawAllocation")
    private void drawUnSign(Canvas canvas) {

        for (int i = 0; i < mCircleCenterPointPositionList.size(); i++) {
            //绘制线段
            float preComplectedXPosition = mCircleCenterPointPositionList.get(i) + mIconWidth / 2;
            if (i != mCircleCenterPointPositionList.size() - 1) {
                //最后一条不需要绘制
                if (mStepBeanList.get(i + 1).getState() == StepBean.STEP_COMPLETED) {
                    //下一个是已完成，当前才需要绘制
                    canvas.drawRect(preComplectedXPosition, mLeftY, preComplectedXPosition + mLineWidth,
                            mRightY, mCompletedPaint);
                } else {
                    //其余绘制灰色
                    canvas.drawRect(preComplectedXPosition, mLeftY, preComplectedXPosition + mLineWidth,
                            mRightY, mUnCompletedPaint);
                }
            }

            //绘制图标
            float currentComplectedXPosition = mCircleCenterPointPositionList.get(i);
            Rect rect = new Rect((int) (currentComplectedXPosition - mIconWidth / 2),
                    (int) (mCenterY - mIconHeight / 2),
                    (int) (currentComplectedXPosition + mIconWidth / 2),
                    (int) (mCenterY + mIconHeight / 2));


            StepBean stepsBean = mStepBeanList.get(i);
//签到的圈圈
            if (stepsBean.getState() == StepBean.STEP_UNDO) {//0 未签
                mDefaultIcon.setBounds(rect);
                mDefaultIcon.draw(canvas);
            } else if (stepsBean.getState() == StepBean.STEP_CURRENT) {//今未
                mAttentionIcon.setBounds(rect);
                mAttentionIcon.draw(canvas);
            } else if (stepsBean.getState() == StepBean.STEP_COMPLETED) {//已签
                mCompleteIcon.setBounds(rect);
                mCompleteIcon.draw(canvas);
            } else if (stepsBean.getState() == StepBean.STEP_TODO) {//待签
                mDefaultIcon.setBounds(rect);
                mDefaultIcon.draw(canvas);
            }

            //绘制Up变化的text 颜色
            if (stepsBean.getState() == StepBean.STEP_COMPLETED || stepsBean.getState() == StepBean.STEP_CURRENT) {//1 -1
                //已经完成了 //是up text的需要色
                mTextNumberPaint.setColor(mCompletedTextColor);//白
            } else if (stepsBean.getState() == StepBean.STEP_UNDO) {//0
                //还没签到的，补签 text 红
                mTextNumberPaint.setColor(mLostTextColor);
            } else if (stepsBean.getState() == StepBean.STEP_TODO) {//2
                mTextNumberPaint.setColor(mTodoCompletedTextColor);
            }

            //绘制UP draw背景
            if (i != mCircleCenterPointPositionList.size() - 1) {
                //最后一个用bitmap
                if (stepsBean.getState() == 1 || stepsBean.getState() == -1) {
                    //已签 红背景 +5  //今 未签 红 签到
                    Rect rectUp =
                            new Rect((int) (currentComplectedXPosition - mUpWidth / 2),
                                    (int) (mCenterY - mIconHeight / 2 - DensityUtil.dp2px(getContext(), 8f) - mUpHeight),
                                    (int) (currentComplectedXPosition + mUpWidth / 2),
                                    (int) (mCenterY - mIconHeight / 2 - DensityUtil.dp2px(getContext(), 1f)));
                    mUpIcon.setBounds(rectUp);
                    mUpIcon.draw(canvas);
                } else if (stepsBean.getState() == 0 || stepsBean.getState() == 2) {
                    //黄背景 未签补签、+5 //后边待签
                    Rect rectUp =
                            new Rect((int) (currentComplectedXPosition - mUpWidth / 2),
                                    (int) (mCenterY - mIconHeight / 2 - DensityUtil.dp2px(getContext(), 8f) - mUpHeight),
                                    (int) (currentComplectedXPosition + mUpWidth / 2),
                                    (int) (mCenterY - mIconHeight / 2 - DensityUtil.dp2px(getContext(), 1f)));
                    mLostUpIcon.setBounds(rectUp);
                    mLostUpIcon.draw(canvas);
                }

                //1表示已签到 0表示未 -1今天(只有未签才有-1今签了就变1了) 2待签
                //up text 内容
                if (stepsBean.getState() == 1 || stepsBean.getState() == -1) {
//                    已签 //UP积分文字 白色字
                    canvas.drawText(stepsBean.getNumber(),
                            currentComplectedXPosition,
//                            currentComplectedXPosition - dp2px(getContext(), 8f),
//                            mCenterY / 2 - dp2px(getContext(), 0.5f),
                            mCenterY - mIconHeight / 2 - DensityUtil.dp2px(getContext(), 4f) - mUpHeight / 2 + Math.abs(mTextNumberPaint.ascent() + mTextNumberPaint.descent()) / 2,
                            mTextNumberPaint);
                } else if (stepsBean.getState() == 0) {
                    //未签到 补签 红色字
                    canvas.drawText(stepsBean.getNumber(),
//                            currentComplectedXPosition - dp2px(getContext(), 8f),
                            currentComplectedXPosition,
//                            mCenterY / 2 - dp2px(getContext(), 0.5f),
                            mCenterY - mIconHeight / 2 - DensityUtil.dp2px(getContext(), 4f) - mUpHeight / 2 + Math.abs(mTextNumberPaint.ascent() + mTextNumberPaint.descent()) / 2,
                            mTextUnNumberPaint);
                } else if (stepsBean.getState() == 2) {
//                    待签 黄色字
                    canvas.drawText(stepsBean.getNumber(),
//                            currentComplectedXPosition - dp2px(getContext(), 8f),
                            currentComplectedXPosition,
//                            mCenterY / 2 - dp2px(getContext(), 0.5f),
                            mCenterY - mIconHeight / 2 - DensityUtil.dp2px(getContext(), 4f) - mUpHeight / 2 + Math.abs(mTextNumberPaint.ascent() + mTextNumberPaint.descent()) / 2,
                            mTextTodoNumberPaint);
                }
            } else {
                //最后一天已签到
                if (stepsBean.getState() == 1) {
                    //最后一天签到啦 就显示积分
                    //绘制图标圈
                    Rect rect7 = new Rect((int) (currentComplectedXPosition - mIconWidth / 2),
                            (int) (mCenterY - mIconHeight / 2),
                            (int) (currentComplectedXPosition + mIconWidth / 2),
                            (int) (mCenterY + mIconHeight / 2));
//圈
                    if (i == mPosition && mCount == ANIMATION_TIME) {
                        //当前需要绘制
                        mCompleteIcon.setBounds(rect7);
                        mCompleteIcon.draw(canvas);
                    } else {
                        if (stepsBean.getState() == StepBean.STEP_COMPLETED) {
                            mCompleteIcon.setBounds(rect7);
                            mCompleteIcon.draw(canvas);
                        }
                    }

                    //绘制 up Text 颜色
                    if (stepsBean.getState() == 1 || (i == mPosition && mCount == ANIMATION_TIME)) {
                        //已经完成了-->+5 或者是当前动画完成并且需要当前位置需要改变
                        //是up的需要 白色
                        mTextNumberPaint.setColor(mCompletedTextColor);
                    }

                    //绘制UP draw背景
                    //已签 红背景 白+5  //今未签 红背景 白字签到
                    Rect rectUp =
                            new Rect((int) (currentComplectedXPosition - mUpWidth / 2),
                                    (int) (mCenterY - mIconHeight / 2 - DensityUtil.dp2px(getContext(), 8f) - mUpHeight),
                                    (int) (currentComplectedXPosition + mUpWidth / 2),
                                    (int) (mCenterY - mIconHeight / 2 - DensityUtil.dp2px(getContext(), 1f)));
                    mUpIcon.setBounds(rectUp);
                    mUpIcon.draw(canvas);

                    //Up文字颜色
                    canvas.drawText(stepsBean.getNumber(),
//                            currentComplectedXPosition - dp2px(getContext(), 8f),
                            currentComplectedXPosition,
//                            mCenterY / 2 - dp2px(getContext(), 0.5f),
                            mCenterY - mIconHeight / 2 - DensityUtil.dp2px(getContext(), 4f) - mUpHeight / 2 + Math.abs(mTextNumberPaint.ascent() + mTextNumberPaint.descent()) / 2,
                            mTextNumberPaint);
                } else {
                //最后一bitmap
                Rect rectUp =
                        new Rect((int) (currentComplectedXPosition - mUpWidth / 2),
                                (int) (mCenterY - mIconHeight / 2 - DensityUtil.dp2px(getContext(), 8f) - mUpHeight),
                                (int) (currentComplectedXPosition + mUpWidth / 2),
                                (int) (mCenterY - mIconHeight / 2 - DensityUtil.dp2px(getContext(), 1f)));
                mUpIcon7.setBounds(rectUp);
                mUpIcon7.draw(canvas);
            }
            }
            //周几天数文字
            canvas.drawText(stepsBean.getDay(),
                    currentComplectedXPosition - DensityUtil.dp2px(getContext(), 12f),
                    mCenterY + DensityUtil.dp2px(getContext(), 30f),
                    mTextDayPaint);
        }
    }

    /**
     * 设置流程步数据
     *
     * @param stepsBeanList 流程数据
     */
    public void setStepNum(List<StepBean> stepsBeanList) {

        if (stepsBeanList == null && stepsBeanList.size() == 0) {
            return;
        }
        mStepBeanList = stepsBeanList;
        mStepNum = mStepBeanList.size();
        setChange();//重新绘制

        //引起重绘
        postInvalidate();
    }

    /**
     * 执行签到动画
     *
     * @param position 执行的位置
     */
    public void startSignAnimation(int position) {
        //线条从灰色变为红色
        isAnimation = true;
        mPosition = position;
        //引起重绘
        postInvalidate();
    }

    /**
     * 确定点击的点在哪个区域
     *
     * @param x 点击的x
     * @param y 点击的y
     */
    int x1, x2, y1, y2;

    private void whichCircle(float x, float y) {
        for (int i = 0; i < mCircleCenterPointPositionList.size(); i++) {
            float currentComplectedXPosition = mCircleCenterPointPositionList.get(i);
            x1 = (int) (currentComplectedXPosition - mUpWidth / 2);//左
            y1 = (int) (mCenterY - mIconHeight / 2 - DensityUtil.dp2px(getContext(), 8f) - mUpHeight);//上
            x2 = (int) (currentComplectedXPosition + mUpWidth / 2);//右
            y2 = (int) (mCenterY - mIconHeight / 2 - DensityUtil.dp2px(getContext(), 1f));//下
            if (x < x2 && x > x1 && y < y2 && y > y1) {
//                Log.e("点击view锁定范围" + i);
                if (mViewClick != null) {
                    Log.e("像素" , mLineWidth + "," + DensityUtil.dpi(getContext()) + "屏幕大小" + displayMetricsWidth + "," + displayMetricsHeight + "scaleX系数" + scaleX);
                    Log.e("dpi" , DensityUtil.dpi(getContext()) + "%" + DensityUtil.density(getContext()));
                    mViewClick.onViewClick(i);
                }
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获取点击屏幕时的点的坐标
        float x = event.getX();
        float y = event.getY();
        whichCircle(x, y);

//        switch (event.getAction()) {
//            case MotionEvent.ACTION_MOVE:
//            case MotionEvent.ACTION_DOWN:
//                Log.e("按下"+x+"#"+y);
//                //如果坐标在我们的文字区域内，则将点击的文字改颜色
//                Random ran = new Random();
//                int i = ran.nextInt(1000) + 1000;
//                String mText = i + "";
//                invalidate();
//                break;
//            case MotionEvent.ACTION_UP:
//                Log.e("抬起");
//                //点击抬起后，回复初始位置。
//                invalidate();//更新视图
//                return true;
//            default:
//                break;
//        }

        //这句话不要修改
        return super.onTouchEvent(event);

        //手指移动的模糊范围，手指移动超出该范围则取消事件处理
//        int length = getWidth() / MOHUFANWEI;
//        final int indexX = (int) (y / length);
//        final int indexY = (int) (x / length);
//
//        if (event.getAction() == MotionEvent.ACTION_DOWN
//                && event.getPointerCount() == 1) {
//            //长按计时器
//            timer = new Timer();
//            timerTask = new TimerTask() {
//                @Override
//                public void run() {
//                    //长按逻辑触发，isClick置为false，手指移开后，不触发点击事件
//                    isCick = false;
//                    doLongPress(indexX, indexY);
//                }
//            };
//            isCick = true;
//            timer.schedule(timerTask, LONGPRESSTIME, 1000 * 60 * 60 * 24);
//        }
//
//        if (event.getAction() == MotionEvent.ACTION_UP
//                && event.getPointerCount() == 1) {
//            //没有触发长按逻辑，进行点击事件
//            if (isCick == true) {
//                doClick(indexX, indexY);
//            }
//            //取消计时
//            timerTask.cancel();
//            timer.cancel();
//        }
//
//        //出现移动，取消点击和长按事件
//        if (event.getAction() == MotionEvent.ACTION_MOVE) {
//            //如果在一定范围内移动，不处理移动事件
//            if (lastX == indexX && lastY == indexY) {
//                return true;
//            }
//            isCick = false;
//            timerTask.cancel();
//            timer.cancel();
//        }
//
//        //一旦触发事件，即改变上次触发事件的坐标
//        lastY = indexY;
//        lastX = indexX;
//        return true;
    }


  /*
   private void doLongPress(int x, int y) {
        Log.e("CAM", "长按了" + x + "   " + y);
    }

    private void doClick(int x, int y) {
        Log.e("CAM", "点击了" + x + "   " + y);
    }

    //计时器，计时点击时长
    Timer timer;
    TimerTask timerTask;

    boolean isCick = true;//判断是否进行点击
    private static final int LONGPRESSTIME = 300;//长按超过0.3秒，触发长按事件

    //记录上次点击的位置，用来进行移动的模糊处理
    int lastX = 0;
    int lastY = 0;

    //此处可以视为将View划分为10行10列的方格，在方格内移动看作没有移动。
    private static final int MOHUFANWEI = 20;
    
    */

    /**
     * 点击事件接口
     */
    public interface MyViewClick {
        void onViewClick(int postion);
    }

    public static MyViewClick mViewClick;

    public void setViewClick(MyViewClick myViewClick) {
        mViewClick = myViewClick;
    }
}
