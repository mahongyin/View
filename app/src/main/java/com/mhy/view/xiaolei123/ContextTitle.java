package com.mhy.view.xiaolei123;

/**
 * 项目名 View
 * 所在包 com.mhy.view.xiaolei123
 * 作者 mahongyin
 * 时间 2020-03-08 10:25
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.mhy.view.R;
import com.mhy.view.utils.DensityUtil;

/**
 * 统一一个标题栏
 * Created by xiaolei on 2017/3/13.
 */

public class ContextTitle extends FrameLayout
{
    private int leftTextVisible = View.VISIBLE;
    private CharSequence leftText = "";
    private float leftTextSize = 14;
    private int leftTextColor = 0xff000000;

    private int leftImage = R.mipmap.ic_launcher;
    private int leftImageVisible = View.VISIBLE;

    private CharSequence titleText = "";
    private float titleTextSize = 14;
    private int titleTextColor = 0xff000000;
    private int titleVisible = View.VISIBLE;
    private int titleRightImg = -1;

    private int rightImage = R.mipmap.ic_launcher;
    private int rightImageVisible = View.VISIBLE;

    private int rightTextVisible = View.VISIBLE;
    private CharSequence rightText = "";
    private float rightTextSize = 14;
    private int rightTextColor = 0xff000000;

    private ImageView leftImageview;//左边图片
    private TextView leftTextview;//左边文字
    private TextView titleTextview;//标题文字
    private TextView rightTextview;//右边文字
    private ImageView rightImageview;//右边图片

    public ContextTitle(Context context)
    {
        this(context, null);
    }

    public ContextTitle(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public ContextTitle(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.ContextTitle);

        leftTextVisible = array.getBoolean(R.styleable.ContextTitle_leftTextVisible, false) ? VISIBLE : GONE;
        leftText = array.getText(R.styleable.ContextTitle_leftText);
        leftTextSize = array.getInt(R.styleable.ContextTitle_leftTextSize, 14);
        leftTextColor = array.getColor(R.styleable.ContextTitle_leftTextColor, 0xff000000);

        leftImageVisible = array.getBoolean(R.styleable.ContextTitle_leftImgVisible, false) ? VISIBLE : GONE;
        leftImage = array.getResourceId(R.styleable.ContextTitle_leftImg, R.mipmap.ic_launcher);

        titleText = array.getText(R.styleable.ContextTitle_titleText);
        titleTextSize = array.getInt(R.styleable.ContextTitle_titleTextSize, 14);
        titleTextColor = array.getColor(R.styleable.ContextTitle_titleTextColor, 0xff000000);
        titleVisible = array.getBoolean(R.styleable.ContextTitle_titleVisible, true) ? VISIBLE : GONE;
        titleRightImg = array.getResourceId(R.styleable.ContextTitle_titleRightImg, -1);

        rightImageVisible = array.getBoolean(R.styleable.ContextTitle_rightImgVisible, false) ? VISIBLE : GONE;
        rightImage = array.getResourceId(R.styleable.ContextTitle_rightImg, R.mipmap.ic_launcher);

        rightTextVisible = array.getBoolean(R.styleable.ContextTitle_rightTextVisible, false) ? VISIBLE : GONE;
        rightText = array.getText(R.styleable.ContextTitle_rightText);
        rightTextSize = array.getInt(R.styleable.ContextTitle_rightTextSize, 14);
        rightTextColor = array.getColor(R.styleable.ContextTitle_rightTextColor, 0xff000000);

        array.recycle();
        InitUI(context);
    }

    public void InitUI(Context context)
    {

        LinearLayout linearLayout = new LinearLayout(context);

        LayoutParams layoutparams1 = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(layoutparams1);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);


        leftImageview = new ImageView(context);
        LinearLayout.LayoutParams leftImageparams = new LinearLayout.LayoutParams(DensityUtil.dp2px(context, 40), LayoutParams.MATCH_PARENT);
        leftImageparams.setMargins(DensityUtil.dp2px(context, 8), DensityUtil.dp2px(context, 0), DensityUtil.dp2px(context, 0), DensityUtil.dp2px(context, 0));
        leftImageview.setLayoutParams(leftImageparams);
        leftImageview.setImageResource(leftImage);
        leftImageview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        leftImageview.setVisibility(leftImageVisible);
        linearLayout.addView(leftImageview);

        leftTextview = new TextView(context);
        LinearLayout.LayoutParams leftTextparams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        leftTextparams.setMargins(DensityUtil.dp2px(context, 8), DensityUtil.dp2px(context, 0), DensityUtil.dp2px(context, -8), DensityUtil.dp2px(context, 0));
        leftTextview.setLayoutParams(leftTextparams);
        leftTextview.setText(leftText);
        leftTextview.setGravity(Gravity.CENTER);
        leftTextview.setTextColor(leftTextColor);
        leftTextview.setTextSize(TypedValue.COMPLEX_UNIT_SP, leftTextSize);
        leftTextview.setVisibility(leftTextVisible);
        linearLayout.addView(leftTextview);

        titleTextview = new TextView(context);
        LayoutParams titleTextparams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        titleTextview.setLayoutParams(titleTextparams);
        titleTextview.setText(titleText);
        titleTextview.setGravity(Gravity.CENTER);
        titleTextview.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleTextSize);
        titleTextview.setVisibility(titleVisible);
        titleTextview.setTextColor(titleTextColor);
        if (titleRightImg > -1)
        {
            Drawable rightDrawable = context.getResources().getDrawable(titleRightImg);
            rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
            titleTextview.setCompoundDrawables(null, null,rightDrawable, null);
            titleTextview.setCompoundDrawablePadding(DensityUtil.dp2px(context,5));
        }
        this.addView(titleTextview);

        LinearLayout.LayoutParams lineparams = new LinearLayout.LayoutParams(DensityUtil.dp2px(context, 0), DensityUtil.dp2px(context, 1), 1);
        View lineView = new View(context);
        lineView.setLayoutParams(lineparams);
        linearLayout.addView(lineView);

        rightTextview = new TextView(context);
        LinearLayout.LayoutParams rightTextParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        rightTextParams.setMargins(DensityUtil.dp2px(context, -8), DensityUtil.dp2px(context, 0), DensityUtil.dp2px(context, 8), DensityUtil.dp2px(context, 0));
        rightTextview.setLayoutParams(rightTextParams);
        rightTextview.setText(rightText);
        rightTextview.setGravity(Gravity.CENTER);
        rightTextview.setTextColor(rightTextColor);
        rightTextview.setVisibility(rightTextVisible);
        rightTextview.setTextSize(TypedValue.COMPLEX_UNIT_SP, rightTextSize);
        linearLayout.addView(rightTextview);

        rightImageview = new ImageView(context);
        LinearLayout.LayoutParams rightImageparams = new LinearLayout.LayoutParams(DensityUtil.dp2px(context, 40), LayoutParams.MATCH_PARENT);
        rightImageparams.setMargins(DensityUtil.dp2px(context, 0), DensityUtil.dp2px(context, 0), DensityUtil.dp2px(context, 8), DensityUtil.dp2px(context, 0));
        rightImageview.setLayoutParams(rightImageparams);
        rightImageview.setImageResource(rightImage);
        rightImageview.setVisibility(rightImageVisible);
        rightImageview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        linearLayout.addView(rightImageview);

        this.addView(linearLayout);
    }

    public void setLeftTextVisible(int leftTextVisible)
    {
        this.leftTextVisible = leftTextVisible;
    }

    public void setLeftText(CharSequence leftText)
    {
        this.leftText = leftText;
    }

    /**
     * 默认单位SP
     *
     * @param leftTextSize
     */
    public void setLeftTextSize(float leftTextSize)
    {
        this.leftTextSize = leftTextSize;
    }

    public void setLeftTextColor(int leftTextColor)
    {
        this.leftTextColor = leftTextColor;
    }

    public void setLeftImage(int leftImage)
    {
        this.leftImage = leftImage;
    }

    public void setLeftImageVisible(int leftImageVisible)
    {
        this.leftImageVisible = leftImageVisible;
    }

    public void setTitleText(CharSequence titleText)
    {
        this.titleText = titleText;
        titleTextview.setText(titleText);
    }

    /**
     * 默认单位SP
     *
     * @param titleTextSize
     */
    public void setTitleTextSize(float titleTextSize)
    {
        this.titleTextSize = titleTextSize;
    }

    public void setTitleTextColor(int titleTextColor)
    {
        this.titleTextColor = titleTextColor;
    }

    public void setTitleVisible(int titleVisible)
    {
        this.titleVisible = titleVisible;
    }

    public void setRightImage(int rightImage)
    {
        this.rightImage = rightImage;
    }

    public void setRightImageVisible(int rightImageVisible)
    {
        this.rightImageVisible = rightImageVisible;
    }

    public void setRightTextVisible(int rightTextVisible)
    {
        this.rightTextVisible = rightTextVisible;
    }

    public void setRightText(CharSequence rightText)
    {
        this.rightText = rightText;
    }

    /**
     * 默认单位SP
     *
     * @param rightTextSize
     */
    public void setRightTextSize(float rightTextSize)
    {
        this.rightTextSize = rightTextSize;
    }

    public void setRightTextColor(int rightTextColor)
    {
        this.rightTextColor = rightTextColor;
    }


    public void setOnLeftImageClick(OnClickListener listener)
    {
        leftImageview.setOnClickListener(listener);
    }

    public void setOnLeftTextClick(OnClickListener listener)
    {
        leftTextview.setOnClickListener(listener);
    }

    public void setOnTitleClick(OnClickListener listener)
    {
        titleTextview.setOnClickListener(listener);
    }

    public void setOnRightTextClick(OnClickListener listener)
    {
        rightTextview.setOnClickListener(listener);
    }

    public void setOnRightImageClick(OnClickListener listener)
    {
        rightImageview.setOnClickListener(listener);
    }

    public void setTitleRightImg(@DrawableRes int titleRightImg)
    {
        this.titleRightImg = titleRightImg;
        if (titleRightImg > -1)
        {
            Drawable rightDrawable = getContext().getResources().getDrawable(titleRightImg);
            rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
            titleTextview.setCompoundDrawables(null, null,rightDrawable, null);
            titleTextview.setCompoundDrawablePadding(DensityUtil.dp2px(getContext(),5));
        }
    }

    /**
     * 获取标题文字
     * @return
     */
    public String getTitleText()
    {
        return titleTextview.getText().toString();
    }

    public void setLeftTextTag(Object tag)
    {
        leftTextview.setTag(tag);
    }
    public <T> T getLeftTextTag()
    {
        return (T)leftTextview.getTag();
    }

    public void setLeftImgTag(Object tag)
    {
        leftImageview.setTag(tag);
    }
    public <T> T getLeftImgTag()
    {
        return (T)leftImageview.getTag();
    }

    public void setTitleTextTag(Object tag)
    {
        titleTextview.setTag(tag);
    }
    public <T> T getTitleTextTag()
    {
        return (T)titleTextview.getTag();
    }

    public void setRightTextTag(Object tag)
    {
        rightTextview.setTag(tag);
    }
    public <T> T getRightTextTag()
    {
        return (T)rightTextview.getTag();
    }

    public void setRightImgTag(Object tag)
    {
        rightImageview.setTag(tag);
    }
    public <T> T getRightImgTag()
    {
        return (T)rightImageview.getTag();
    }
}


