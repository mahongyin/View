package com.mhy.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * 无关
 */
public class CustomView extends View {
    private int mColor = Color.RED;//默认为红色
    private String mText = "我是自定义view";//默认显示该文本
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//画笔
    private Path path=new Path();//绘制多边形的类
    public CustomView(Context context) {
        super(context);// 
        init();
    }

    public CustomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);//注意不是super(context,attrs,0);
         init();
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {//解析自定义属性
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        mColor = typedArray.getColor(R.styleable.CustomTextView_customColor, Color.RED);
    // 如果没有判断，当没有指定该属性而去加载该属性app便会崩溃掉
        if (typedArray.getText(R.styleable.CustomTextView_customText) != null) {
            mText = typedArray.getText(R.styleable.CustomTextView_customText).toString();
        }
        typedArray.recycle();//释放资源
        init();
    }

    private void init() {
        mPaint.setColor(mColor);// 为画笔添加颜色
//        mPaint.setColor(Color.RED);//为画笔设置颜色
        mPaint.setStrokeWidth(10);//为画笔设置粗细

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(mText, 100, 100, mPaint);



//        canvas.drawColor(Color.GREEN);//为画布设置颜色
        //设置等腰三角形的三点坐标
        mPaint.setStyle(Paint.Style.STROKE);//设置空心
        path.moveTo(100,100);//起始点
        path.lineTo(150,150);//右下角
        path.lineTo(50,150);//左下角
        path.close();//闭合图形
        //绘制三角形
        canvas.drawPath(path,mPaint);

    }
}

