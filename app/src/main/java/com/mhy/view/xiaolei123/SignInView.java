package com.mhy.view.xiaolei123;

/**
 * 项目名 View
 * 所在包 com.mhy.view.sigin
 * 作者 mahongyin
 * 时间 2020-03-08 9:33
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.mhy.view.R;
import com.mhy.view.utils.DensityUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by xiaolei on 2017/11/6.
 * CopyBy https://www.jianshu.com/p/9707821281a8  签到足迹控件S
 */

public class SignInView extends View {
    private int width, height;
    private int monthDays = 31;//本月有31天
    private Paint paint;
    private RectF oval = new RectF();
    private float strokeWidth = 10;
    private Bitmap checkBitmap, uncheckBitmap, closeGiftBitmap, openGiftBitmap;
    private int backColor = Color.parseColor("#C3DEEA"),
            rashColor = Color.parseColor("#B2CADB"),
            textColor = Color.parseColor("#60ADE5");
    private List<Bitmap> bitmapList = new LinkedList<>();
    private int signInCount = 9;

    public SignInView(Context context) {
        this(context, null);
    }

    public SignInView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SignInView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        paint = new Paint();
        paint.setAntiAlias(true);
        strokeWidth = DensityUtil.dp2px(context, 6);
        checkBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_cg_pro);
        uncheckBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_cg);
        closeGiftBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_gift);
        openGiftBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_gift_open);
    }

    //计算
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        height = MeasureSpec.getSize(heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        Log.e("计算", height + "X" + width);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 设置本月天数
     *
     * @param monthDays
     */
    public void setMonthDays(int monthDays) {
        this.monthDays = monthDays;
        if (monthDays == 0) {
            this.monthDays = 31;
        }
        postInvalidate();
    }

    /**
     * 设置一共签到了几天
     *
     * @param days
     */
    public void setProgress(int days) {
        this.signInCount = days;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(backColor);
        paint.setStrokeWidth(strokeWidth);
        int rowCount = (monthDays % 7 == 0 ? monthDays / 7 : monthDays / 7 + 1);
        int rowHeigh = height / (rowCount);
        int startX = 0 + rowHeigh / 2;
        int endX = width - rowHeigh / 2;
        int days = 0;

        for (int a = 0; a < rowCount; a++) {
            if (a + 1 == rowCount) {
                endX = (endX - startX) / 7 * (monthDays % 7 == 0 ? 7 : (monthDays % 7)) + checkBitmap.getWidth() / 2;
            }
            paint.setStrokeWidth(strokeWidth);
            int y = rowHeigh * a + rowHeigh / 2;
            canvas.drawLine(startX, y, endX, y, paint);

            paint.setColor(rashColor);
            paint.setStrokeWidth(1);
            canvas.drawLine(startX, y, endX, y, paint);
            // 这里是来判断，是否需要画出左半边还是右半边的半圆弧度？
            if (a % 2 != 0) {
                if (a + 1 != rowCount) {
                    drawLeftOrRightArc(true, canvas, 0 + strokeWidth, y, 0 + rowHeigh + strokeWidth, y + rowHeigh);
                }
            } else {
                if (a + 1 != rowCount) {
                    drawLeftOrRightArc(false, canvas, endX - rowHeigh / 2 - strokeWidth, y, endX + rowHeigh / 2 - strokeWidth, y + rowHeigh);
                }
            }

            // 这里是来判断，本次这根线上画出的礼物的点，以及顺序是顺画，还是倒画出。
            bitmapList.clear();
            int lastDay = (monthDays % 7) == 0 ? 7 : (monthDays % 7);
            for (int b = 0; b < (a + 1 == rowCount ? (lastDay) : 7); b++) {
                days++;
                if (days <= signInCount) {
                    if (days == 3 || days == 8 || days == 14 || days == 21 || days == monthDays) {
                        bitmapList.add(a % 2 != 0 ? 0 : bitmapList.size(), openGiftBitmap);
                    } else {
                        bitmapList.add(a % 2 != 0 ? 0 : bitmapList.size(), checkBitmap);
                    }
                } else {
                    if (days == 3 || days == 8 || days == 14 || days == 21 || days == monthDays) {
                        bitmapList.add(a % 2 != 0 ? 0 : bitmapList.size(), closeGiftBitmap);
                    } else {
                        bitmapList.add(a % 2 != 0 ? 0 : bitmapList.size(), uncheckBitmap);
                    }
                }
            }

            drawImgs(bitmapList, startX, endX, y, canvas);
        }
        super.onDraw(canvas);
    }

    /**
     * 画出的按路线上的图片，勾选，礼物
     *
     * @param bitmapList
     * @param startX
     * @param endX
     * @param y
     * @param canvas
     */
    private void drawImgs(List<Bitmap> bitmapList, float startX, float endX, float y, Canvas canvas) {
        if (!bitmapList.isEmpty()) {
            startX = startX - bitmapList.get(0).getWidth() / 2;
            int count = bitmapList.size();
            float bitmap_width = (endX - startX) / (count - 1);
            for (int a = 0; a < count; a++) {
                Bitmap bitmap = bitmapList.get(a);
                canvas.drawBitmap(bitmap, startX + (bitmap_width * a), y - bitmap.getHeight() / 2, paint);
            }
        }
    }

    /**
     * 这里画出左边半圆弧，还是右边半圆弧
     *
     * @param isLeft
     * @param canvas
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    private void drawLeftOrRightArc(boolean isLeft, Canvas canvas, float left, float top, float right, float bottom) {
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(backColor);

        if (isLeft) {
            paint.setStyle(Paint.Style.STROKE);
            oval.setEmpty();
            oval.set(left, top, right, bottom);
            canvas.drawArc(oval, 90, 180, false, paint);
            paint.setStrokeWidth(1);
            paint.setColor(rashColor);
            canvas.drawArc(oval, 90, 180, false, paint);
        } else {
            paint.setStyle(Paint.Style.STROKE);
            oval.setEmpty();
            oval.set(left, top, right, bottom);
            canvas.drawArc(oval, 270, 180, false, paint);

            paint.setStrokeWidth(1);
            paint.setColor(rashColor);

            canvas.drawArc(oval, 270, 180, false, paint);
        }
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(backColor);
    }
}
