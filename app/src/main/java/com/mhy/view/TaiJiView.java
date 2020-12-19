package com.mhy.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * Created By Mahongyin
 * Date    2020/12/19 12:43
 *         app:leftcolor="@color/colorAccent"
 *         app:rightcolor="@color/colorPrimaryDark"
 *         app:animaltime="3000"
 */
    public class TaiJiView extends View {
        private Paint mPaint;
        private int mWidth;
        private int mHeight;
        private int useWidth;
        private int leftcolor;
        private int rightcolor;
        private ObjectAnimator objectAnimator;
        private int animaltime;
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public TaiJiView(Context context) {
            this(context,null);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public TaiJiView(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs,0);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public TaiJiView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            this(context, attrs, defStyleAttr,0);
            initCustomAttrs(context,attrs);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public TaiJiView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            init();

        }
        private void init() {
            initPaint();
        }


        /**
         获取自定义属性
         */
        private void initCustomAttrs(Context context, AttributeSet attrs) {
            //获取自定义属性。
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TaiJiView);
            //获取太极颜色
            leftcolor = ta.getColor(R.styleable.TaiJiView_leftcolor, Color.BLACK);
            rightcolor=ta.getColor(R.styleable.TaiJiView_rightcolor, Color.WHITE);
            animaltime=ta.getInt(R.styleable.TaiJiView_animaltime,1000);

            //回收
            ta.recycle();

        }
        /**
         * 初始化画笔
         */
        private void initPaint() {
            mPaint = new Paint();        //创建画笔对象
            mPaint.setColor(Color.BLACK);    //设置画笔颜色
            mPaint.setStyle(Paint.Style.FILL); //设置画笔模式为填充
            mPaint.setStrokeWidth(10f);     //设置画笔宽度为10px
            mPaint.setAntiAlias(true);     //设置抗锯齿
            mPaint.setAlpha(255);        //设置画笔透明度
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mWidth = w;
            mHeight = h;
            useWidth=mWidth;
            if (mWidth>mHeight){
                useWidth=mHeight;
            }
        }


        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            mPaint.setColor(leftcolor);
            canvas.drawArc(new RectF(0, 0, useWidth, useWidth), 270, -180, true, mPaint);

            mPaint.setColor(rightcolor);
            canvas.drawArc(new RectF(0, 0, useWidth, useWidth), 270, 180, true, mPaint);

            mPaint.setColor(leftcolor);
            canvas.drawArc(new RectF(useWidth / 4, 0, useWidth / 2 +useWidth / 4, useWidth / 2),
                    270, 360, true, mPaint);

            mPaint.setColor(rightcolor);
            canvas.drawArc(new RectF(useWidth / 4, useWidth / 2, useWidth / 2 + useWidth / 4,useWidth),
                    270, 360, true, mPaint);
            mPaint.setColor(leftcolor);
            canvas.drawCircle(useWidth/ 2, useWidth * 3 / 4, useWidth/16, mPaint);

            mPaint.setColor(rightcolor);
            canvas.drawCircle(useWidth / 2, useWidth / 4, useWidth/16, mPaint);

        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public void createAnimation() {
            if (objectAnimator==null){
                objectAnimator = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f);//添加旋转动画，旋转中心默认为控件中点
                objectAnimator.setDuration(animaltime);//设置动画时间
                objectAnimator.setInterpolator(new LinearInterpolator());//动画时间线性渐变
                objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
                objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
                objectAnimator.start();//动画开始
            }else{
                objectAnimator.resume();//动画继续开始
            }


        }
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public  void stopAnimation(){
            if (objectAnimator!=null){
                objectAnimator.pause();//动画暂停  .end()结束动画
            }
        }
        public  void cleanAnimation(){
            if (objectAnimator!=null){
                objectAnimator.end(); //结束动画
            }
        }
    }
