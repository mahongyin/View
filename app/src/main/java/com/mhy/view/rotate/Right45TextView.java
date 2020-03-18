package com.mhy.view.rotate;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * @author mahongyin
 * time: 2019/9/12 14:02
 * email: mhy.work@qq.com
 * 描述 说明:右上角45度   text45.setText("xxx")
 */
public class Right45TextView extends AppCompatTextView {


    public Right45TextView(Context context) {
        super(context);
    }

    public Right45TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //倾斜度45,上下左右居中
        canvas.rotate(45, getMeasuredWidth()/2, getMeasuredHeight()/2);
        super.onDraw(canvas);
    }

}
/* 背景图为45度三角
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/white" >

    <包名.RotateTextView
        android:layout_width="54dip"
        android:layout_height="54dip"
        android:layout_alignParentRight="true"
        android:background="@drawable/e_rotate_bg2"
        android:gravity="center"
        android:paddingBottom="17dp"
        android:text="进行中"
        android:textColor="#fff"
        android:textSize="12sp" />

</RelativeLayout>
 */