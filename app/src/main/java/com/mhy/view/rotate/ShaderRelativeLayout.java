package com.mhy.view.rotate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.mhy.view.R;


/**
 * 渐变 底部 圆弧背景 签到背景用
 */
public class ShaderRelativeLayout extends RelativeLayout {

    private Paint mPaint;
    private Context mContext;

    public ShaderRelativeLayout(Context context) {
        super(context, null);
    }

    public ShaderRelativeLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mContext = context;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        @SuppressLint("DrawAllocation") RadialGradient radialGradient = new RadialGradient(0, 500, 1000,
                mContext.getResources().getColor(R.color.personalCenterHeadStart),
                mContext.getResources().getColor(R.color.personalCenterHeadEnd), Shader.TileMode.CLAMP);
        mPaint.setShader(radialGradient);
        int width = getWidth();
        int height = getHeight() - 50;
        int heightAdd = 50;
        canvas.drawRect(0, 0, width, height, mPaint);
        RectF rectF = new RectF(0, height - heightAdd, width, height + heightAdd);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawArc(rectF, 0, 180, true, mPaint);
    }

}
