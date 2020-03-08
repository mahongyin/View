package android.signin.com.myapplication.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.signin.com.myapplication.R;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2017/5/31.
 * //
 //                       .::::.
 //                     .::::::::.
 //                    :::::::::::
 //                 ..:::::::::::'
 //              '::::::::::::'
 //                .::::::::::
 //           '::::::::::::::..
 //                ..::::::::::::.
 //              ``::::::::::::::::
 //               ::::``:::::::::'        .:::.
 //              ::::'   ':::::'       .::::::::.
 //            .::::'      ::::     .:::::::'::::.
 //           .:::'       :::::  .:::::::::' ':::::.
 //          .::'        :::::.:::::::::'      ':::::.
 //         .::'         ::::::::::::::'         ``::::.
 //     ...:::           ::::::::::::'              ``::.
 //    ```` ':.          ':::::::::'                  ::::..
 //                       '.:::::'                    ':'````..
 */

public class Signin extends View {
    private static final int DEF_HEIGHT = 85; //默认高度
    private static final int DEF_PADDING = 10; //默认padding值
    private static final int TEXT_MARGIN_TOP = 13; // 文字距离团的marginTop值
    private static final float SECTION_SCALE = 1.2F / 2; //截面的缩放值
    private static final float SIGN_IN_BALL_SCALE = 1F / 6; //签到 六边形的缩放值
    private static final float SIGN_BG_RECT_SCALE = 1F / 4; //横线的 缩放值
    private static final int BITMAP_W_H = 34;   //没用到
    private int CALCULATE_BITMAP_W_H = 0;   //没用到


    private int signInBgColor;  //签到背景颜色
    private int signInPbColor;  //签到横线颜色
    private int signInCheckColor;  //签到放大六边形的颜色
    private int signInTextColor;  //第一天  第二天.....字体颜色
    private int signInTextSize;  //字体大小

    private Paint signInBgPaint;  //签到背景  画笔
    private Paint signInPbPatin;    //签到画笔
    private Paint signInCheckPaint;  //大的六边形画笔
    private Paint signInTextPaint;  //字体画笔

    private int viewHeight;  //控件高度
    private int viewWidth; //控件宽度
    private int viewPadding; //padding值
    private int signInBallRadio; //签到六边形的 半径
    private int signInRectHeight; //横线高度

    private RectF signInBgRectF; //整个屏幕宽度的一条黑色的直线

    private int circleY;   //因为每个六边形的 Y坐标是不变的    这个是Y左边的值
    private int descY; //这个是文字的Y坐标

    private int currentSignInTag; //第几天的标识

    private List<String> viewData;  //存放 第一天 第二天......第七天
    private List<Point> circlePoints; //画六边形的 各个中心点坐标
    private List<Path> signInPaths;   //没用到 不知道为啥写的 - -
    private List<Path> signInDoublePaths;
    private List<Path> sexanglePaths;  //签到(六边形)路径   --->这个List 存放的是七个六边形的路径
    private List<Path> sexangleDoublePaths; //放大二倍六边形 路径
    private List<Point> descPoints; //矩形的点坐标
    private List<RectF> signInPbRectFs;  //签到的矩形
    private List<Path> selectLinePath;  //签到的矩形 做动画效果

    private float mSexanglePercent;          //以下三个 是矩形  六边形  对号  的值动画(顺序不固定)    使用方法可以百度一下 - -
    private ValueAnimator mSexangleAnimator;
    private PathMeasure mPathMeasure;
    private Path mSexangleDest;
    private Boolean isAnamitorStart = false;

    private float mSelectRectPercent;
    private ValueAnimator mSelectRectAnimator;
    private PathMeasure mRectPathMeasure;
    private Path mRectDest;
    private boolean isRectAnimatorStart = false;


    private float mSelectSignInPercent;
    private ValueAnimator mSelectSignInAnimator;
    private PathMeasure mSignInMeasure;
    private Path mSignInDest;
    private Boolean isSignInStaer;

    private Bitmap bitmap;   //礼物图标
    private Rect srcBitmap;
    private Rect desBitmap;

    public Signin(Context context) {
        this(context, null);
    }

    public Signin(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Signin(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化颜色字体大小
        initAttrs(context, attrs, defStyleAttr);
        //初始化一些工具
        initToolsAndData();
        initAnimator();
    }

    /**
     * 当View  发小发生改变的时候调用这个方法
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEF_PADDING, getResources().getDisplayMetrics());
        int textMarginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TEXT_MARGIN_TOP, getResources().getDisplayMetrics());

        viewWidth = w;
        viewHeight = h;

        signInBallRadio = (int) (viewHeight * SIGN_IN_BALL_SCALE / 2);
        signInRectHeight = (int) (signInBallRadio * SIGN_BG_RECT_SCALE);

        signInBgRectF = new RectF(0, viewHeight * SECTION_SCALE - signInBallRadio - signInRectHeight, viewWidth, viewHeight * SECTION_SCALE - signInBallRadio);

        circleY = (int) (signInBgRectF.top + signInRectHeight / 2);
        descY = (int) (viewHeight * SECTION_SCALE + textMarginTop);

        //计算各个点 图形的位置
        calcucateCirclePoints(viewData);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //签到横线
        drawSignInBgRect(canvas);
        //black circle
//        drawSignInNormalCircle(canvas);
        //绘制正常的签到六边形
        drawSignInNormalSexangle(canvas);
        //选择第几天之前的矩形
        drawSignInPbRect(canvas);
        //绘制旧的矩形
        drawSignInPbOldRect(canvas);
        //select circle
//        drawSignInCheck(canvas);
        //选择的六边形
        drawSignInSexangle(canvas);
        //签到之前的六边形
        drawSignOldSignInSexangle(canvas);
        //绘制文字
        drawTextDesc(canvas);
        //绘制礼物图标  如果不用  可以注释掉
        drawBitmap(canvas);
    }


    private void drawSignInBgRect(Canvas canvas) {
        canvas.drawRect(signInBgRectF, signInBgPaint);
    }

    private void drawSignInNormalCircle(Canvas canvas) {

        if (null != circlePoints && circlePoints.size() > 0) {
            for (Point circlePoint : circlePoints) {
                canvas.drawCircle(circlePoint.x, circlePoint.y, signInBallRadio, signInBgPaint);
            }
        }
    }

    private void drawSignInNormalSexangle(Canvas canvas) {
        for (int i = currentSignInTag == -1 ? 0 : currentSignInTag; i < sexanglePaths.size(); i++) {
            canvas.drawPath(sexanglePaths.get(i), signInBgPaint);
        }
//        canvas.save();
//        if (currentSignInTag!=-1){
//            canvas.scale(1.75F,1.75F,circlePoints.get(currentSignInTag).x,circlePoints.get(currentSignInTag).y);
//            canvas.restore();
//        }

    }

    private void drawSignInPbRect(Canvas canvas) {
        if (isNeedReturn()) {
            return;
        }
        signInPbPatin = createPaint(signInPbColor, 0, Paint.Style.FILL_AND_STROKE, 5);
//        canvas.drawRect(currentSignInTag == viewData.size() - 1 ? signInBgRectF : signInPbRectFs.get(currentSignInTag), signInPbPatin);

        mRectPathMeasure.setPath(selectLinePath.get(currentSignInTag), false);
        mRectPathMeasure.getSegment(0, mSelectRectPercent * mRectPathMeasure.getLength(), mRectDest, true);
        canvas.drawPath(mRectDest, signInPbPatin);
        if (!isRectAnimatorStart) {
            signInPbPatin = createPaint(signInPbColor, 0, Paint.Style.FILL, 0);
            canvas.drawRect(currentSignInTag == viewData.size() - 1 ? signInBgRectF : signInPbRectFs.get(currentSignInTag), signInPbPatin);
        }

    }

    private void drawSignInPbOldRect(Canvas canvas) {
        if (isNeedReturn()) {
            return;
        }
        if (currentSignInTag - 1 >= 0) {
            signInPbPatin = createPaint(signInPbColor, 0, Paint.Style.FILL, 0);
            canvas.drawRect(currentSignInTag == viewData.size() - 1 ? signInBgRectF : signInPbRectFs.get(currentSignInTag - 1), signInPbPatin);
        }
    }


    private void drawSignInCheck(Canvas canvas) {
        if (isNeedReturn()) {
            return;
        }
        for (int i = 0; i <= currentSignInTag; i++) {
            canvas.drawCircle(circlePoints.get(i).x, circlePoints.get(i).y, signInBallRadio, signInPbPatin);
        }
    }

    private void drawSignInSexangle(Canvas canvas) {
        if (isNeedReturn() || isRectAnimatorStart) {
            return;
        }
        mPathMeasure.setPath(sexangleDoublePaths.get(currentSignInTag), true);
        mPathMeasure.getSegment(0, mSexanglePercent * mPathMeasure.getLength(), mSexangleDest, true);
        signInPbPatin = createPaint(signInPbColor, 0, Paint.Style.STROKE, 2);
        canvas.drawPath(mSexangleDest, signInPbPatin);


        mSignInMeasure.setPath(signInDoublePaths.get(currentSignInTag), true);
        mSignInMeasure.getSegment(0, mSelectSignInPercent * mSignInMeasure.getLength(), mSignInDest, true);
        signInCheckPaint = createPaint(signInCheckColor, 0, Paint.Style.STROKE, 2);
        canvas.drawPath(mSignInDest, signInCheckPaint);

        if (!isAnamitorStart) {
            signInPbPatin = createPaint(signInPbColor, 0, Paint.Style.FILL, 0);
            canvas.drawPath(sexangleDoublePaths.get(currentSignInTag), signInPbPatin);
            signInCheckPaint = createPaint(signInCheckColor, 0, Paint.Style.FILL, 3);
            canvas.drawPath(signInDoublePaths.get(currentSignInTag), signInCheckPaint);
        }
    }

    private void drawSignOldSignInSexangle(Canvas canvas) {
        if (isNeedReturn()) {
            return;
        }
        for (int i = 0; i < currentSignInTag; i++) {
            signInPbPatin = createPaint(signInPbColor, 0, Paint.Style.FILL, 0);
            canvas.drawPath(sexanglePaths.get(i), signInPbPatin);

        }

    }

    private void drawTextDesc(Canvas canvas) {
        if (null != viewData && viewData.size() > 0) {
            for (int i = 0; i < viewData.size(); i++) {
                Point point = descPoints.get(i);
                canvas.drawText(viewData.get(i), point.x, point.y + 21, signInTextPaint);

            }
        }
    }

    private void drawBitmap(Canvas canvas) {
        if (null == bitmap || null == srcBitmap || null == desBitmap || null == signInTextPaint)
            return;
        canvas.drawBitmap(bitmap, srcBitmap, desBitmap, signInTextPaint);
    }

    private Boolean isNeedReturn() {
        return currentSignInTag < 0 || currentSignInTag >= viewData.size();
    }

    private void calcucateCirclePoints(List<String> viewData) {
        if (null != viewData) {
            //横向平分屏幕  计算每段距离大小
            int intervalSize = viewData.size() + 1;
            int onePiece = (viewWidth - signInBallRadio * 2 * viewData.size()) / intervalSize;


            for (int i = 0; i < viewData.size(); i++) {
                //每个六边形的 位置
                Point circlePoint = new Point((i + 1) * onePiece + ((i + 1) * 2 - 1) * signInBallRadio, circleY);
                //矩形的位置
                Point descPoint = new Point((int) ((i + 1) * onePiece + ((i + 1) * 2 - 1) * signInBallRadio -
                        signInTextPaint.measureText(viewData.get(i)) / 2), descY);
                //签到的矩形
                RectF rectF = new RectF(0, viewHeight * SECTION_SCALE - signInBallRadio - signInRectHeight, circlePoint.x - signInBallRadio + 3, viewHeight * SECTION_SCALE - signInBallRadio);
                //签到时候矩形的路径
                Path selectPath = new Path();//选择矩形
                selectPath.moveTo(i == 0 ? 0 : i * onePiece + (2 * i - 1) * signInBallRadio, circlePoint.y);
                if (i == viewData.size() - 1) {
                    selectPath.lineTo(viewWidth, circlePoint.y);
                } else {
                    selectPath.lineTo(circlePoint.x - signInBallRadio, circlePoint.y);
                }

                //没用到
                Path signInPath = new Path();
                signInPath.moveTo(circlePoint.x - signInBallRadio / 2, circlePoint.y);
                signInPath.lineTo(circlePoint.x, circlePoint.y + signInBallRadio / 2);
                signInPath.lineTo(circlePoint.x + signInBallRadio / 2, circlePoint.y - signInBallRadio + signInBallRadio / 2);

                //大的六边形 路径
                Path signInDoublePath = new Path();
                signInDoublePath.moveTo((float) (circlePoint.x - signInBallRadio * 1.75 + 10), circlePoint.y);
                signInDoublePath.lineTo((float) (circlePoint.x - signInBallRadio * 1.75 / 4), (float) (circlePoint.y + 1.65 * signInBallRadio / 2 - 5));
                signInDoublePath.lineTo((float) (circlePoint.x + signInBallRadio * 1.75 / 2 + 5), (float) (circlePoint.y - 1.65 * signInBallRadio / 3 - 5));
                signInDoublePath.lineTo((float) (circlePoint.x - signInBallRadio * 1.75 / 4), (float) (circlePoint.y + 1.65 * signInBallRadio / 4 * 3));
//                signInDoublePath.lineTo(circlePoint.x + signInBallRadio , circlePoint.y - signInBallRadio + signInBallRadio);
                signInDoublePath.close();

                //小得六边形
                Path sexanglePath = new Path();
                sexanglePath.moveTo(circlePoint.x - signInBallRadio, circlePoint.y);
                sexanglePath.lineTo(circlePoint.x - signInBallRadio / 2, circlePoint.y - 4 * signInBallRadio / 4);
                sexanglePath.lineTo(circlePoint.x + signInBallRadio / 2, circlePoint.y - 4 * signInBallRadio / 4);
                sexanglePath.lineTo(circlePoint.x + signInBallRadio, circlePoint.y);
                sexanglePath.lineTo(circlePoint.x + signInBallRadio / 2, circlePoint.y + 4 * signInBallRadio / 4);
                sexanglePath.lineTo(circlePoint.x - signInBallRadio / 2, circlePoint.y + 4 * signInBallRadio / 4);
                sexanglePath.close();

                // 大的六边形  做动画
                Path sexangleDoublePath = new Path();
                sexangleDoublePath.moveTo((float) (circlePoint.x - signInBallRadio * 1.75), circlePoint.y);
                sexangleDoublePath.lineTo(circlePoint.x - signInBallRadio, (float) (circlePoint.y - (1.65 * signInBallRadio)));
                sexangleDoublePath.lineTo(circlePoint.x + signInBallRadio, (float) (circlePoint.y - 1.65 * signInBallRadio));
                sexangleDoublePath.lineTo((float) (circlePoint.x + signInBallRadio * 1.75), circlePoint.y);
                sexangleDoublePath.lineTo(circlePoint.x + signInBallRadio, (float) (circlePoint.y + 1.65 * signInBallRadio));
                sexangleDoublePath.lineTo(circlePoint.x - signInBallRadio, (float) (circlePoint.y + 1.65 * signInBallRadio));
                sexangleDoublePath.close();

                circlePoints.add(circlePoint);
                descPoints.add(descPoint);
                signInPbRectFs.add(rectF);
                signInPaths.add(signInPath);
                signInDoublePaths.add(signInDoublePath);
                sexangleDoublePaths.add(sexangleDoublePath);
                sexanglePaths.add(sexanglePath);
                selectLinePath.add(selectPath);
            }

            //设置礼物图标
            int new_W_H = circlePoints.get(viewData.size() - 1).y - signInBallRadio * 2;

            desBitmap = new Rect(circlePoints.get(viewData.size() - 1).x - new_W_H / 2,
                    circlePoints.get(viewData.size() - 1).y - signInBallRadio * 2 - new_W_H,
                    circlePoints.get(viewData.size() - 1).x + new_W_H / 2,
                    circlePoints.get(viewData.size() - 1).y - signInBallRadio * 2);
        }
    }

    /**
     * 测量
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int newHeight;
        //如果不是精准模式   就使用默认的高度      具体用法请百度 MeasureSpec.getMode()
        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            newHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEF_HEIGHT, getResources().getDisplayMetrics());
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(newHeight, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    private void initToolsAndData() {
        //存放路径 点 ....的一些集合
        circlePoints = new ArrayList<>();
        descPoints = new ArrayList<>();
        signInPaths = new ArrayList<>();
        signInPbRectFs = new ArrayList<>();
        sexanglePaths = new ArrayList<>();
        signInDoublePaths = new ArrayList<>();
        sexangleDoublePaths = new ArrayList<>();
        selectLinePath = new ArrayList<>();
        //默认设置成 -1
        currentSignInTag = -1;

        //初始化  画笔  抽取一个工具类
        signInBgPaint = createPaint(signInBgColor, 0, Paint.Style.FILL, 0);
        signInPbPatin = createPaint(signInPbColor, 0, Paint.Style.FILL, 0);
        signInCheckPaint = createPaint(signInCheckColor, 0, Paint.Style.FILL, 3);
        signInTextPaint = createPaint(signInTextColor, signInTextSize, Paint.Style.FILL, 0);

//        img_signpage_gift  没用到
        CALCULATE_BITMAP_W_H = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BITMAP_W_H, getResources().getDisplayMetrics());
//        礼物图标  使用方法 可以百度一下//
        bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.img_signpage_gift);
//        bitmap = zoomImg(bitmap,CALCULATE_BITMAP_W_H,CALCULATE_BITMAP_W_H);
        srcBitmap = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * 处理图片
     *
     * @param bm 所要转换的bitmap
     * @return 指定宽高的bitmap
     */
    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    private void initAnimator() {
        mPathMeasure = new PathMeasure();
        mSexangleDest = new Path();
        mSexangleAnimator = ValueAnimator.ofFloat(0, 1);
        mSexangleAnimator.setInterpolator(new LinearInterpolator());
        mSexangleAnimator.setDuration(1000);
        mSexangleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSexanglePercent = (float) animation.getAnimatedValue();
                if (mSexanglePercent == 1) {
                    isAnamitorStart = false;
                    mSexangleDest.reset();
                }
                invalidate();
            }
        });

        mSelectRectAnimator = ValueAnimator.ofFloat(0, 1);
        mRectPathMeasure = new PathMeasure();
        mRectDest = new Path();
        mSelectRectAnimator.setDuration(1000);
        mSelectRectAnimator.setInterpolator(new LinearInterpolator());
        mSelectRectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSelectRectPercent = (float) animation.getAnimatedValue();
                if (currentSignInTag == viewData.size() - 1) {
                    if (mSelectRectPercent > 0.5) {
                        mRectDest.reset();
                        isRectAnimatorStart = false;
                        isAnamitorStart = true;
                        mSexangleAnimator.start();
                        mSelectSignInAnimator.start();
                    }
                } else if (mSelectRectPercent > 0.85) {
                    mRectDest.reset();
                    isRectAnimatorStart = false;
                    isAnamitorStart = true;
                    mSexangleAnimator.start();
                    mSelectSignInAnimator.start();
                }
                invalidate();
            }
        });


        mSignInMeasure = new PathMeasure();
        mSignInDest = new Path();
        mSelectSignInAnimator = ValueAnimator.ofFloat(0, 1);
        mSelectSignInAnimator.setDuration(1000);
        mSelectSignInAnimator.setInterpolator(new LinearInterpolator());
        mSelectSignInAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSelectSignInPercent = (float) animation.getAnimatedValue();
                if (mSelectSignInPercent >= 1) {
                    isSignInStaer = false;
                    mSignInDest.reset();
                }
                invalidate();
            }
        });

    }

    private Paint createPaint(int paintColor, int textSize, Paint.Style style, int linWidth) {
        Paint p = new Paint();
        p.setColor(paintColor);
        p.setAntiAlias(true);
        p.setStrokeWidth(linWidth);
        p.setDither(true);
        p.setTextSize(textSize);
        p.setStyle(style);
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeJoin(Paint.Join.ROUND);
        return p;
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        //参数2   定义的颜色 字体,    参数 4    默认字体颜色 字体大小
        //参数2  和参数4 的值 需要一一对应 要不然报错.....
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Signin, defStyleAttr, R.style.def_sign);
        int indexCount = typedArray.getIndexCount();
        //循环  如果有值则赋值   没有 则使用默认的
        for (int i = 0; i < indexCount; i++) {
            int index = typedArray.getIndex(i);
            switch (index) {
                case R.styleable.Signin_sign_in_bg_color:
                    signInBgColor = typedArray.getColor(index, Color.BLACK);
                    break;
                case R.styleable.Signin_sign_in_pb_color:
                    signInPbColor = typedArray.getColor(index, Color.BLACK);
                    break;
                case R.styleable.Signin_sign_in_check_color:
                    signInCheckColor = typedArray.getColor(index, Color.BLACK);
                    break;
                case R.styleable.Signin_sign_in_text_color:
                    signInTextColor = typedArray.getColor(index, Color.BLACK);
                    break;
                case R.styleable.Signin_sign_in_text_siz:
                    signInTextSize = typedArray.getDimensionPixelSize(index, 0);
                    break;
            }
        }
        typedArray.recycle();
    }

    public void setSignInData(List<String> data) {
        if (null != data) {
            viewData = data;
        }
    }

    public void setSignInEvent() {
        currentSignInTag++;
        if (currentSignInTag >= viewData.size()) {
            return;
        }
//        isAnamitorStart = true;
        isRectAnimatorStart = true;
        mSexangleDest.reset();
        mRectDest.reset();
        mSignInDest.reset();
//        mSexangleAnimator.start();
        mSelectRectAnimator.start();
    }

    public void setSignInClear() {
        currentSignInTag = -1;
        mRectDest.reset();
        mSexangleDest.reset();
        mSignInDest.reset();
        invalidate();
    }

    public void setCurretn(int i) {
        currentSignInTag = i - 1;
        if (currentSignInTag >= viewData.size() || currentSignInTag < 0) {
            return;
        }
        mSignInDest.reset();
        mRectDest.reset();
        mSexangleDest.reset();
        invalidate();
    }
}














