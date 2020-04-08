package com.mhy.view.rocker;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Display;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gcssloop.widget.RockerView;
import com.mhy.view.R;
import com.rockerlibrary.view.MyRockerView;

import org.jetbrains.annotations.NotNull;

/**
 * 摇杆
 */
public class MainActivity extends AppCompatActivity {
    //横屏
    private TextView mLogLeft;
    private TextView mLogRight;
    //横屏
    /**
     * 1:竖屏   2:横屏 判断屏幕以旋转的方向
     */
    private int orientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rocker);
        orientation = getResources().getConfiguration().orientation;
        //横屏
        mLogLeft = (TextView) findViewById(R.id.log_left);
        mLogRight = (TextView) findViewById(R.id.log_right);

        MyRockerView rockerViewLeft = (MyRockerView) findViewById(R.id.rockerView_left);
        if (rockerViewLeft != null) {
            rockerViewLeft.setCallBackMode(MyRockerView.CallBackMode.CALL_BACK_MODE_STATE_CHANGE);
            rockerViewLeft.setOnShakeListener(MyRockerView.DirectionMode.DIRECTION_8, new MyRockerView.OnShakeListener() {
                @Override
                public void onStart() {
                    mLogLeft.setText(null);
                }

                @Override
                public void direction(MyRockerView.Direction direction) {
                    mLogLeft.setText("摇动方向 : " + getDirection(direction));
                }

                @Override
                public void onFinish() {
                    mLogLeft.setText(null);
                }
            });
        }

        MyRockerView rockerViewRight = (MyRockerView) findViewById(R.id.rockerView_right);
        if (rockerViewRight != null) {
            rockerViewRight.setOnAngleChangeListener(new MyRockerView.OnAngleChangeListener() {
                @Override
                public void onStart() {
                    mLogRight.setText(null);
                }

                @Override
                public void angle(double angle) {
                    mLogRight.setText("摇动角度 : " + angle);
                }

                @Override
                public void onFinish() {
                    mLogRight.setText(null);
                }
            });
        }

//竖屏
        try {
            RockerView rocker = (RockerView) findViewById(R.id.rocker);
            if (null != rocker) {
                rocker.setListener(new RockerView.RockerListener() {

                    @Override
                    public void callback(int eventType, int currentAngle, float currentDistance) {
                        switch (eventType) {
                            case RockerView.EVENT_ACTION:
                                // 触摸事件回调
                                Log.e("EVENT_ACTION-------->", "angle=" + currentAngle + " - distance" + currentDistance);
                                break;
                            case RockerView.EVENT_CLOCK:
                                // 定时回调
                                Log.e("EVENT_CLOCK", "angle=" + currentAngle + " - distance" + currentDistance);
                                break;
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//竖屏

    }

    //横屏
    private String getDirection(MyRockerView.Direction direction) {
        String message = null;
        switch (direction) {
            case DIRECTION_LEFT:
                message = "左";
                break;
            case DIRECTION_RIGHT:
                message = "右";
                break;
            case DIRECTION_UP:
                message = "上";
                break;
            case DIRECTION_DOWN:
                message = "下";
                break;
            case DIRECTION_UP_LEFT:
                message = "左上";
                break;
            case DIRECTION_UP_RIGHT:
                message = "右上";
                break;
            case DIRECTION_DOWN_LEFT:
                message = "左下";
                break;
            case DIRECTION_DOWN_RIGHT:
                message = "右下";
                break;
            default:
                break;
        }
        return message;
    }
////这个是 反转屏幕 重走生命周期用来保存数据 然后 在oncreta 里判断
//// savedInstanceState!=null取key对应
//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
//        super.onSaveInstanceState(outState, outPersistentState);
//        outState.putString("key","value");
//    }

    //横竖屏切换
    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // land do nothing is ok
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // port do nothing is ok
        }

        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        if (width > height) {
            orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE; // 横屏
        } else {
            orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT; // 竖屏
        }

        this.setRequestedOrientation(orientation);
    }

    @Override
    protected void onResume() {
        orientation = ActivityInfo.SCREEN_ORIENTATION_USER;
        this.setRequestedOrientation(orientation);
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        if (width > height) {
            orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        } else {
            orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }
        super.onResume();
    }
}