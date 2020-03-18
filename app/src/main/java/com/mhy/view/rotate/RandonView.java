package com.mhy.view.rotate;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.mhy.view.R;

import java.util.Random;

/**
 * @author: mahongyin
 * @description:
 * @projectName: RandonView01 随机验证码
 * @date: 2018-06-12
 * @time: 10:26
 */
public class RandonView extends View {
    private static final char[] CHARS = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
            'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z'};
    /**
     * 初始化生成随机数的类
     */
    private Random mRandom = new Random();
    
    /**
     * 初始化可变字符串
     */
    private StringBuffer sb = new StringBuffer();
    /**
     * 文本
     */
    private String mText;
    /**
     * 文本的颜色
     */
    private int mTextColor;
    /**
     * 文本的大小
     */
    private int mTextSize;
    /**
     * 文本的背景颜色
     */
    private int mBgCplor;
    private Rect mBound;
    private Paint mPaint;

    public RandonView(Context context) {
        this(context, null);
    }

    public RandonView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RandonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        /**
         * 获得我们所定义的自定义样式属性
         */
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RandonView, defStyleAttr, 0);
        for (int i = 0; i < a.getIndexCount(); i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.RandonView_rtext:
                    mText = a.getString(attr);
                    break;
                case R.styleable.RandonView_rtextColor:
                    // 默认文本颜色设置为黑色
                    mTextColor = a.getColor(R.styleable.RandonView_rtextColor, Color.BLACK);
                    break;
                case R.styleable.RandonView_rbgColor:
                    // 默认文本背景颜色设置为蓝色
                    mBgCplor = a.getColor(R.styleable.RandonView_rbgColor, Color.BLUE);
                    break;
                case R.styleable.RandonView_rtextSize:
                    // 默认设置为16sp，TypeValue也可以把sp转化为px
                    mTextSize = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
                    break;
            }
        }
        a.recycle();
        // 获得绘制文本的宽和高
        mPaint = new Paint();
        mPaint.setTextSize(mTextSize);

        mBound = new Rect();
        mPaint.getTextBounds(mText, 0, mText.length(), mBound);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mText = createCode();
                mTextColor = randomColor();
                mBgCplor = randomColor();
                //View重新调用一次draw过程,以起到界面刷新的作用
                postInvalidate();
            }
        });
    }

    /**
     * 生成验证码
     */
    public String createCode() {
        sb.delete(0, sb.length()); // 使用之前首先清空内容
        for (int i = 0; i < 6; i++) {
            sb.append(CHARS[mRandom.nextInt(CHARS.length)]);
        }
       Log.e("mhy生成验证码", sb.toString());
        return sb.toString();
    }

    /**
     * 随机颜色
     */
    private int randomColor() {
        sb.delete(0, sb.length()); // 使用之前首先清空内容
        String haxString;
        for (int i = 0; i < 3; i++) {
            haxString = Integer.toHexString(mRandom.nextInt(0xFF));
            if (haxString.length() == 1) {
                haxString = "0" + haxString;
            }
            sb.append(haxString);
        }
       Log.e("mhy随机颜色", "#" + sb.toString());
        return Color.parseColor("#" + sb.toString());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heighMode = MeasureSpec.getMode(heightMeasureSpec);
        int heighSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : getPaddingLeft() + getPaddingRight() + mBound.width(), heighMode == MeasureSpec.EXACTLY ? heighSize : getPaddingTop() + getPaddingBottom() + mBound.height());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mBgCplor);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);

        mPaint.setColor(mTextColor);
        canvas.drawText(mText, getWidth() / 2 - mBound.width() / 2, getHeight() / 2 + mBound.height() / 2, mPaint);
    }
}

