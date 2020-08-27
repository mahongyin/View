package com.mhy.view.textview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.mhy.view.R;

/**
 * @author mahongyin
 * @Project View
 * @Package com.mhy.view.textview
 * @data 2020-04-08 11:32
 * @CopyRight mhy.work@qq.com
 * @description:旋转圈箭头
 */
//MyCircleView类
public class MyCircleView extends View {
    //当前画笔画圆的颜色
    private int CurrenCircleBoundColor;
    private Paint paint;
    ////从xml中获取的颜色
    private int circleBundColor;
    private float circleBoundWidth;
    private float pivotX;
    private float pivotY;
    private float radius=130;
    private float currentDegree=0;
    private int currentSpeed=1;
    private boolean isPause=false;
    public MyCircleView(Context context) {
        super(context);
        initView(context);
    }
    public MyCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyCircleView);
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            //就是我们自定义的属性的资源id
            int attr = typedArray.getIndex(i);
            switch (attr){
                case R.styleable.MyCircleView_circlr_bound_color:
                    circleBundColor = typedArray.getColor(attr, Color.RED);
                    CurrenCircleBoundColor=circleBundColor;
                    break;
                case R.styleable.MyCircleView_circlr_bound_width:
                    circleBoundWidth = typedArray.getDimension(attr, 3);
                    break;
            }
        }
        typedArray.recycle();//销毁
    }
    public MyCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }
    private void initView(Context context){
        paint = new Paint();
    }
    public void setColor(int color){
        if (CurrenCircleBoundColor!=color){
            CurrenCircleBoundColor=color;
        }else {
            CurrenCircleBoundColor=circleBundColor;
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setAntiAlias(true);
        paint.setColor(CurrenCircleBoundColor);
        paint.setStrokeWidth(circleBoundWidth);
        paint.setStyle(Paint.Style.STROKE);
        pivotX = getWidth() / 2;
        pivotY = getHeight() / 2;
        canvas.drawCircle(pivotX,pivotY,radius,paint);
        canvas.save();
        //旋转画布 , 如果旋转的的度数大的话,视觉上看着是旋转快的
        canvas.rotate(currentDegree,pivotX,pivotY);
        //提供了一些api可以用来画线(画路径)
        Path path = new Path();
        //从哪开始画 从A开始画
        path.moveTo(pivotX+radius,pivotY);
        //从A点画一个直线到D点
        path.lineTo(pivotX+radius-20,pivotY-20);
        //从D点画一个直线到B点
        path.lineTo(pivotX+radius,pivotY+20);
        //从B点画一个直线到C点
        path.lineTo(pivotX+radius+20,pivotY-20);
        //闭合 -- 从C点画一个直线到A点
        path.close();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        canvas.drawPath(path,paint);
        canvas.restore();
        //旋转的度数一个一个度数增加, 如果乘以一个速度的话,按一个速度速度增加
        currentDegree+=1*currentSpeed;
        if (!isPause){
            invalidate();
        }
    }
    public void speed(){
        ++currentSpeed;
        if (currentSpeed>=10){
            currentSpeed=10;
            Toast.makeText(getContext(),"我比闪电还快",Toast.LENGTH_SHORT).show();
        }
    }
    public void slowDown(){
        --currentSpeed;
        if (currentSpeed<=1){
            currentSpeed=1;
        }
    }
    public void pauseOrStart(){
        //如果是开始状态的话去重新绘制
        if (isPause){
            isPause=!isPause;
            invalidate();
        }else {
            isPause=!isPause;
        }
    }
}
