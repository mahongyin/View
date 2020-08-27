package com.mhy.view.textview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.mhy.view.R;

/**
 * 带边框的TextView
 *  <com.mhy.view.textview.BorderTextView
 *         android:id="@+id/bordertv"
 *         android:layout_alignParentBottom="true"
 *         android:layout_centerHorizontal="true"
 *         android:layout_width="wrap_content"
 *         android:layout_height="wrap_content"
 *         android:gravity="center"
 *         app:strokeColor="@color/c_FE4E45"
 *         app:strokeWidth="0.5dp"
 *         app:cornerRadius="2dp"
 *         app:paddingLR="13dp"
 *         app:paddingTB="2dp"
 *         android:text="@string/app_name"/>
 */
public class BorderTextView extends androidx.appcompat.widget.AppCompatTextView {

    public static final float DEFAULT_STROKE_WIDTH = 0.5f;    // 默认边框宽度
    public static final float DEFAULT_CORNER_RADIUS = 2.0f;   // 默认圆角半径, 2dp
    public static final float DEFAULT_LR_PADDING = 3f;      // 默认左右内边距
    public static final float DEFAULT_TB_PADDING = 2f;      // 默认上下内边距

    private float strokeWidth;    // 边框线宽
    private int strokeColor;    // 边框颜色
    private float cornerRadius;   // 圆角半径
    private int paddingLR;//内边距
    private int paddingTB;
    private Paint mPaint = new Paint();     // 画边框所使用画笔对象
    private RectF mRectF;                   // 画边框要使用的矩形

    public BorderTextView(Context context) {
        this(context, null);
    }

    public BorderTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BorderTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRectF = new RectF();
//        cornerRadius = dip2px(getContext(), DEFAULT_CORNER_RADIUS);
//        strokeWidth = dip2px(getContext(), DEFAULT_STROKE_WIDTH);
//         paddingLR = dip2px(getContext(), DEFAULT_LR_PADDING);
//         paddingTB = dip2px(getContext(), DEFAULT_TB_PADDING);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BorderTextView);
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            //就是我们自定义的属性的资源id
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.BorderTextView_strokeColor:
                    strokeColor = typedArray.getColor(attr, Color.RED);
                    break;
                case R.styleable.BorderTextView_strokeWidth:
                    strokeWidth = dip2px(getContext(), typedArray.getDimension(attr, DEFAULT_STROKE_WIDTH));
                    break;
                case R.styleable.BorderTextView_cornerRadius:
                    cornerRadius = dip2px(getContext(), typedArray.getDimension(attr, DEFAULT_CORNER_RADIUS));
                    break;
                case R.styleable.BorderTextView_paddingLR:
                    paddingLR = dip2px(getContext(), typedArray.getDimension(attr, DEFAULT_LR_PADDING));
                    break;
                case R.styleable.BorderTextView_paddingTB:
                    paddingTB = dip2px(getContext(), typedArray.getDimension(attr, DEFAULT_TB_PADDING));
                    break;
            }
        }
        setTextColor(strokeColor);
        setPadding(paddingLR, paddingTB, paddingLR, paddingTB);
        typedArray.recycle();//销毁
    }

    private int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStyle(Paint.Style.STROKE);     // 空心效果
        mPaint.setAntiAlias(true);               // 设置画笔为无锯齿
        mPaint.setStrokeWidth(strokeWidth);      // 线宽
        mPaint.setColor(strokeColor);

        // 画空心圆角矩形
        mRectF.left = mRectF.top = 0.5f * strokeWidth;
        mRectF.right = getMeasuredWidth() - strokeWidth;
        mRectF.bottom = getMeasuredHeight() - strokeWidth;
        canvas.drawRoundRect(mRectF, cornerRadius, cornerRadius, mPaint);

    }

    public void setBorderCorlor(int corlor) {
        strokeColor = corlor;
        setTextColor(strokeColor);
    }
}
