package com.mhy.view.dragView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.mhy.view.R;

public class DragActivity extends AppCompatActivity {

    private Button mBtnTest;
    private TestViewGroup mTestDragViewGroup;
   private int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag);
        mBtnTest = findViewById(R.id.btnTest);
        mTestDragViewGroup = findViewById(R.id.testDragViewGroup);

        mBtnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i==1) {
                    i=0;
                    mTestDragViewGroup.testSmoothSlide(true);
                }else {
                    i=1;
                    mTestDragViewGroup.testSmoothSlide(false);
                }
            }
        });
    }
}