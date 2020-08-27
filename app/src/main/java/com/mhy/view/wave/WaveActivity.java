package com.mhy.view.wave;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.mhy.view.R;


/**
 * 水波纹
 */

public class WaveActivity extends AppCompatActivity {

    private ImageView imageView;
    private WaveView waveView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wave);
        imageView = (ImageView) findViewById(R.id.image);
        waveView3 = (WaveView) findViewById(R.id.wave_view);

        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-2, -2);
        lp.gravity = Gravity.BOTTOM | Gravity.CENTER;
        waveView3.setOnWaveAnimationListener(new WaveView.OnWaveAnimationListener() {
            @Override
            public void OnWaveAnimation(float y) {
                lp.setMargins(0, 0, 0, (int) y + 2);
                imageView.setLayoutParams(lp);
            }
        });
    }
}
