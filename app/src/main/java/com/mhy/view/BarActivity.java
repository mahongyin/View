package com.mhy.view;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class BarActivity extends AppCompatActivity {

    View decorView;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar);
        hideBar();
    }
    private void hideSystemUI() {
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void hideBar(){

        //获取当前界面的decorView
        View decorView=getWindow().getDecorView();
//只隐藏状态栏
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);//fullsceeen表示全屏，即将状态栏隐藏

//隐藏状态栏 隐藏导航栏     fullsceeen表示全屏，即将状态栏隐藏;hide_navigation表示隐藏导航栏
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
//                |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


// 只隐藏状态栏 页面不随状态栏隐藏变化
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
//                |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);



//        //透明式状态栏以及导航栏
        if (Build.VERSION.SDK_INT>=21){
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    |View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getWindow().setStatusBarColor(Color.TRANSPARENT);                       //将状态栏设置成透明色
            getWindow().setNavigationBarColor(Color.TRANSPARENT);                   //将导航栏设置为透明色
        }


//        //真正的全屏体验
//        if (Build.VERSION.SDK_INT>=21){
//            View decorView=getWindow().getDecorView();
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//            getWindow().setNavigationBarColor(Color.TRANSPARENT);
//        }


        //上下全无
//        decorView=getWindow().getDecorView();
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
//                |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

//        getWindow().setStatusBarColor(Color.TRANSPARENT);
//        getWindow().setNavigationBarColor(Color.TRANSPARENT);
//        ImageView imageView=(ImageView)findViewById(R.id.imageView);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                hideSystemUI();
//            }
//        });

//       隐藏actionbar
//        ActionBar actionBar=getSupportActionBar();
//        actionBar.hide();  //隐藏actionbar
    }
}