package com.mhy.view.textview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * @author mahongyin
 * @Project View
 * @Package com.mhy.view.textview
 * @data 2020-04-08 11:28
 * @CopyRight mhy.work@qq.com
 * @description:
 */
public class ArrowTextView extends AppCompatTextView {

    public ArrowTextView(Context context) {
        super(context);
    }

    public ArrowTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ArrowTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);   //设置画笔抗锯齿
        paint.setStrokeWidth(2);    //设置线宽
        paint.setColor(Color.WHITE);  //设置线的颜色

        int height = getHeight();   //获取View的高度
        int width = getWidth();     //获取View的宽度

        //框定文本显示的区域
        canvas.drawRoundRect(new RectF(getPaddingLeft() - 20,getPaddingTop() - 20,width - getPaddingRight() + 20,height - getPaddingBottom()+20),30,30,paint);

        Path path = new Path();

        //以下是绘制文本的那个箭头
        path.moveTo(width / 2, height);// 三角形顶点
        path.lineTo(width / 2 - 20, height - getPaddingBottom());   //三角形左边的点
        path.lineTo(width / 2 + 20, height - getPaddingBottom());   //三角形右边的点

        path.close();
        canvas.drawPath(path, paint);
        super.onDraw(canvas);
    }
    /*
<?xml version="1.0" encoding="utf-8"?>
<ConstraintLayout
xmlns:android="http://schemas.android.com/apk/res/android"
xmlns:tools="http://schemas.android.com/tools"
android:layout_width="match_parent"
android:layout_height="match_parent"
android:background="#000000"
tools:context="com.example.testview.Activity.ArrowTextViewActivity">

<ArrowTextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:textColor="#000000"
    android:padding="20dp"
    android:text="aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    android:background="#666666"
    />

 </ConstraintLayout>

    */
}