package com.mhy.view.textview;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.appcompat.app.AppCompatActivity;

import com.mhy.view.R;

/**
 * @author mahongyin
 * @Project View
 * @Package com.mhy.view.textview
 * @data 2020-04-08 11:34
 * @CopyRight mhy.work@qq.com
 * @description:
 */ //主页面
public class MainActivity extends AppCompatActivity {
    //全局变量
    private MyCircleView my_view;
    BorderTextView borderTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycircle);
        //找控件
        my_view = (MyCircleView) findViewById(R.id.my_view);
        borderTextView = findViewById(R.id.bordertv);
    }
    public void onClick(View view){
        my_view.setColor(Color.BLUE);
    }
    public void add(View view){
        my_view.speed();
    }
    public void slow(View view){
        my_view.slowDown();
    }
    public void pauseOrStart(View view){
        my_view.pauseOrStart();
        borderTextView.setBorderCorlor(getResources().getColor(R.color.black));
    }
}
