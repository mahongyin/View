package com.mhy.view.rotate;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.mhy.view.R;
/*
参考
https://github.com/HeZaiJin/SlantedTextView
有人修改了一些代码，可参考下
https://github.com/ouyangpeng/SlantedTextView
 */
/**
 * 一个倾斜的TextView，可以自定义
 * </p>
 * <p>
 *     用法
 *     1、Layout文件中使用
 *     <pre>
 *      &lt;com.haozhang.lib.SlantedTextView
 *             android:layout_width="54dp"
 *             android:layout_height="54dp"
 *             android:layout_gravity="right|top"
 *             android:gravity="center"
 *             app:slantedBackgroundColor="#3497fd"
 *             app:slantedMode="right"
 *             app:slantedLength="31dp"
 *             app:slantedTextSize="11sp"
 *             app:slantedText="@string/talent_account_unbind_watch_corner_marker"
 *            /&gt;
 *    </pre>
 *   Java代码中可以动态设置属性
 *   <pre class="prettyprint">
 *       SlantedTextView stv = (SlantedTextView) findViewById(R.id.test);
 *       stv.setText("PHP")
 *             .setTextColor(Color.WHITE)
 *             .setSlantedBackgroundColor(Color.BLACK)
 *             .setTextSize(18)
 *             .setSlantedLength(50)
 *             .setMode(SlantedTextView.MODE_LEFT);
 *
 * </pre>
 * <p><img width="423" height="702" src="https://img-blog.csdnimg.cn/2019011116222126.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxNDQ2MjgyNDEy,size_16,color_FFFFFF,t_70" alt="效果"></p>
 * <p><img width="540" height="350" src="https://img-blog.csdnimg.cn/20190111211532561.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxNDQ2MjgyNDEy,size_16,color_FFFFFF,t_70" alt="自定义的属性"></p>
 * <p><img width="519" height="716" src="https://img-blog.csdnimg.cn/20190111211543623.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxNDQ2MjgyNDEy,size_16,color_FFFFFF,t_70" alt="斜边展示的模式"></p>
 * @author OuyangPeng
 */
public class SlantedTextView extends View {
    private static final String TAG = "SlantedTextView";

    /**
     * 倾斜模式
     */
    interface SlantedMode {
        /**
         * 左上角倾斜模式
         */
        int MODE_LEFT = 0;
        /**
         * 右上角倾斜模式
         */
        int MODE_RIGHT = 1;
        /**
         * 左下角倾斜模式
         */
        int MODE_LEFT_BOTTOM = 2;
        /**
         * 右下角倾斜模式
         */
        int MODE_RIGHT_BOTTOM = 3;
        /**
         * 左上角倾斜三角形模式
         */
        int MODE_LEFT_TRIANGLE = 4;
        /**
         * 右上角倾斜三角形模式
         */
        int MODE_RIGHT_TRIANGLE = 5;
        /**
         * 左下角倾斜三角形模式
         */
        int MODE_LEFT_BOTTOM_TRIANGLE = 6;
        /**
         * 右下角倾斜三角形模式
         */
        int MODE_RIGHT_BOTTOM_TRIANGLE = 7;
    }

    /**
     * 倾斜模式
     */
    private int mMode = SlantedMode.MODE_LEFT;

    /**
     * 默认倾斜角度
     */
    int DEFAULT_SLANTED_DEGREES = 45;

    /**
     * 倾斜角度
     */
    int slantedDegrees = 45;


    /**
     * 背景画笔
     */
    private Paint mPaint;
    /**
     * 文字画笔
     */
    private TextPaint mTextPaint;
    /**
     * 倾斜背景 边长距离
     */
    private float mSlantedLength = 40;
    /**
     * 倾斜背景颜色
     */
    private int mSlantedBackgroundColor = Color.TRANSPARENT;
    /**
     * 文字大小
     */
    private float mTextSize = 16;
    /**
     * 文字颜色
     */
    private int mTextColor = Color.WHITE;
    /**
     * 文字内容
     */
    private String mSlantedText = "";


    public SlantedTextView(Context context) {
        this(context, null);
    }

    public SlantedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SlantedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SlantedTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public void init(AttributeSet attrs) {
        initAttrs(attrs);
        initPaints();
    }

    /**
     * 初始化自定义属性
     */
    private void initAttrs(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.SlantedTextView);
        mTextSize = array.getDimension(R.styleable.SlantedTextView_slantedTextSize, mTextSize);
        mTextColor = array.getColor(R.styleable.SlantedTextView_slantedTextColor, mTextColor);
        mSlantedLength = array.getDimension(R.styleable.SlantedTextView_slantedLength, mSlantedLength);
        mSlantedBackgroundColor = array.getColor(R.styleable.SlantedTextView_slantedBackgroundColor, mSlantedBackgroundColor);
        if (array.hasValue(R.styleable.SlantedTextView_slantedText)) {
            mSlantedText = array.getString(R.styleable.SlantedTextView_slantedText);
        }
        if (array.hasValue(R.styleable.SlantedTextView_slantedMode)) {
            mMode = array.getInt(R.styleable.SlantedTextView_slantedMode, 0);
        }
        slantedDegrees = array.getInteger(R.styleable.SlantedTextView_slantedDegree, DEFAULT_SLANTED_DEGREES);
        array.recycle();
    }

    /**
     * 初始化 画笔
     */
    private void initPaints() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        /*
         *  参考链接
         *  https://www.jianshu.com/p/78c36742d50f
         *  https://www.cnblogs.com/libertycode/p/6290497.html
         *
         *  在API中Android为我们提供了18种模式：
         *     ADD:饱和相加,对图像饱和度进行相加,不常用
         * 　　CLEAR:清除图像
         * 　　DARKEN:变暗,较深的颜色覆盖较浅的颜色，若两者深浅程度相同则混合
         * 　　DST:只显示目标图像
         * 　　DST_ATOP:在源图像和目标图像相交的地方绘制【目标图像】，在不相交的地方绘制【源图像】，相交处的效果受到源图像和目标图像alpha的影响
         * 　　DST_IN:只在源图像和目标图像相交的地方绘制【目标图像】，绘制效果受到源图像对应地方透明度影响
         * 　　DST_OUT:只在源图像和目标图像不相交的地方绘制【目标图像】，在相交的地方根据源图像的alpha进行过滤，源图像完全不透明则完全过滤，完全透明则不过滤
         * 　　DST_OVER:将目标图像放在源图像上方
         * 　　LIGHTEN:变亮，与DARKEN相反，DARKEN和LIGHTEN生成的图像结果与Android对颜色值深浅的定义有关
         * 　　MULTIPLY:正片叠底，源图像素颜色值乘以目标图像素颜色值除以255得到混合后图像像素颜色值
         * 　　OVERLAY:叠加
         * 　　SCREEN:滤色，色调均和,保留两个图层中较白的部分，较暗的部分被遮盖
         * 　　SRC:只显示源图像
         * 　　SRC_ATOP:在源图像和目标图像相交的地方绘制【源图像】，在不相交的地方绘制【目标图像】，相交处的效果受到源图像和目标图像alpha的影响
         * 　　SRC_IN:只在源图像和目标图像相交的地方绘制【源图像】
         * 　　SRC_OUT:只在源图像和目标图像不相交的地方绘制【源图像】，相交的地方根据目标图像的对应地方的alpha进行过滤，目标图像完全不透明则完全过滤，完全透明则不过滤
         * 　　SRC_OVER:将源图像放在目标图像上方
         * 　　XOR:在源图像和目标图像相交的地方之外绘制它们，在相交的地方受到对应alpha和色值影响，如果完全不透明则相交处完全不绘制
         */
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        //抗锯齿
        mPaint.setAntiAlias(true);
        mPaint.setColor(mSlantedBackgroundColor);

        /*
            Paint.ANTI_ALIAS_FLAG ：抗锯齿标志
            Paint.FILTER_BITMAP_FLAG : 使位图过滤的位掩码标志
            Paint.DITHER_FLAG : 使位图进行有利的抖动的位掩码标志
            Paint.UNDERLINE_TEXT_FLAG : 下划线
            Paint.STRIKE_THRU_TEXT_FLAG : 中划线
            Paint.FAKE_BOLD_TEXT_FLAG : 加粗
            Paint.LINEAR_TEXT_FLAG : 使文本平滑线性扩展的油漆标志
            Paint.SUBPIXEL_TEXT_FLAG : 使文本的亚像素定位的绘图标志
            Paint.EMBEDDED_BITMAP_TEXT_FLAG : 绘制文本时允许使用位图字体的绘图标志
         */
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        drawText(canvas);
    }

    /**
     * 绘制背景
     *
     * @param canvas 画布
     */
    private void drawBackground(Canvas canvas) {
        Path path = new Path();
        int w = getWidth();
        int h = getHeight();
        if (w != h) {
            throw new IllegalStateException("SlantedTextView's width must equal to height");
        }
        switch (mMode) {
            case SlantedMode.MODE_LEFT:
                path = getModeLeftPath(path, w, h);
                break;
            case SlantedMode.MODE_RIGHT:
                path = getModeRightPath(path, w, h);
                break;
            case SlantedMode.MODE_LEFT_BOTTOM:
                path = getModeLeftBottomPath(path, w, h);
                break;
            case SlantedMode.MODE_RIGHT_BOTTOM:
                path = getModeRightBottomPath(path, w, h);
                break;
            case SlantedMode.MODE_LEFT_TRIANGLE:
                path = getModeLeftTrianglePath(path, w, h);
                break;
            case SlantedMode.MODE_RIGHT_TRIANGLE:
                path = getModeRightTrianglePath(path, w, h);
                break;
            case SlantedMode.MODE_LEFT_BOTTOM_TRIANGLE:
                path = getModeLeftBottomTrianglePath(path, w, h);
                break;
            case SlantedMode.MODE_RIGHT_BOTTOM_TRIANGLE:
                path = getModeRightBottomTrianglePath(path, w, h);
                break;
            default:
                Log.d(TAG, "mode is error!");
                break;
        }
        path.close();
        canvas.drawPath(path, mPaint);
        canvas.save();
    }

    private Path getModeLeftPath(Path path, int w, int h) {
        path.moveTo(w, 0);
        path.lineTo(0, h);
        path.lineTo(0, h - mSlantedLength);
        path.lineTo(w - mSlantedLength, 0);
        return path;
    }

    private Path getModeRightPath(Path path, int w, int h) {
        path.lineTo(w, h);
        path.lineTo(w, h - mSlantedLength);
        path.lineTo(mSlantedLength, 0);
        return path;
    }

    private Path getModeLeftBottomPath(Path path, int w, int h) {
        path.lineTo(w, h);
        path.lineTo(w - mSlantedLength, h);
        path.lineTo(0, mSlantedLength);
        return path;
    }

    private Path getModeRightBottomPath(Path path, int w, int h) {
        path.moveTo(0, h);
        path.lineTo(mSlantedLength, h);
        path.lineTo(w, mSlantedLength);
        path.lineTo(w, 0);
        return path;
    }

    private Path getModeLeftTrianglePath(Path path, int w, int h) {
        path.lineTo(0, h);
        path.lineTo(w, 0);
        return path;
    }

    private Path getModeRightTrianglePath(Path path, int w, int h) {
        path.lineTo(w, 0);
        path.lineTo(w, h);
        return path;
    }

    private Path getModeLeftBottomTrianglePath(Path path, int w, int h) {
        path.lineTo(w, h);
        path.lineTo(0, h);
        return path;
    }

    private Path getModeRightBottomTrianglePath(Path path, int w, int h) {
        path.moveTo(0, h);
        path.lineTo(w, h);
        path.lineTo(w, 0);
        return path;
    }


    /**
     * 绘制文字
     *
     * @param canvas 画布
     */
    private void drawText(Canvas canvas) {
        int w = (int) (canvas.getWidth() - mSlantedLength / 2);
        int h = (int) (canvas.getHeight() - mSlantedLength / 2);
        float[] xy = calculateXY(canvas, w, h);
        float toX = xy[0];
        float toY = xy[1];
        float centerX = xy[2];
        float centerY = xy[3];
        float angle = xy[4];

        canvas.rotate(angle, centerX, centerY);

        canvas.drawText(mSlantedText, toX, toY, mTextPaint);
    }

    /**
     * 计算 绘制文字所需要的一些参数
     * <p>xy[0] 文字的x坐标</p>
     * <p>xy[1] 文字的y坐标</p>
     * <p>xy[2] 轴点的X坐标（旋转不变）</p>
     * <p>xy[3] 轴点的y坐标（旋转不变）</p>
     * <p>xy[4] 选择角度</p>
     *
     * @param canvas 画布
     * @param w      宽度
     * @param h      高度
     * @return 绘制文字所需要的一些参数
     */
    private float[] calculateXY(Canvas canvas, int w, int h) {
        float[] xy = new float[5];
        Rect rect = null;
        RectF rectF = null;
        int offset = (int) (mSlantedLength / 2);
        switch (mMode) {
            // 左上角的
            case SlantedMode.MODE_LEFT_TRIANGLE:
            case SlantedMode.MODE_LEFT:
                rect = new Rect(0, 0, w, h);
                rectF = new RectF(rect);
                rectF.right = mTextPaint.measureText(mSlantedText, 0, mSlantedText.length());
                /*
                 * 1.基准点是baseline
                 * 2.ascent：是baseline之上至字符最高处的距离
                 * 3.descent：是baseline之下至字符最低处的距离
                 * 4.leading：是上一行字符的descent到下一行的ascent之间的距离，也就是相邻行间的空白距离
                 * 5.top：是指的是最高字符到baseline的值，即ascent的最大值
                 * 6.bottom：是指最低字符到baseline的值，即descent的最大值
                 */
                rectF.bottom = mTextPaint.descent() - mTextPaint.ascent();
                rectF.left += (rect.width() - rectF.right) / 2.0f;
                rectF.top += (rect.height() - rectF.bottom) / 2.0f;
                xy[0] = rectF.left;
                xy[1] = rectF.top - mTextPaint.ascent();
                xy[2] = w / 2;
                xy[3] = h / 2;
                xy[4] = -slantedDegrees;
                break;

            // 右上角的
            case SlantedMode.MODE_RIGHT_TRIANGLE:
            case SlantedMode.MODE_RIGHT:
                rect = new Rect(offset, 0, w + offset, h);
                rectF = new RectF(rect);
                rectF.right = mTextPaint.measureText(mSlantedText, 0, mSlantedText.length());
                rectF.bottom = mTextPaint.descent() - mTextPaint.ascent();
                rectF.left += (rect.width() - rectF.right) / 2.0f;
                rectF.top += (rect.height() - rectF.bottom) / 2.0f;
                xy[0] = rectF.left;
                xy[1] = rectF.top - mTextPaint.ascent();
                xy[2] = w / 2 + offset;
                xy[3] = h / 2;
                xy[4] = slantedDegrees;
                break;

            // 左下角的
            case SlantedMode.MODE_LEFT_BOTTOM_TRIANGLE:
            case SlantedMode.MODE_LEFT_BOTTOM:
                rect = new Rect(0, offset, w, h + offset);
                rectF = new RectF(rect);
                rectF.right = mTextPaint.measureText(mSlantedText, 0, mSlantedText.length());
                rectF.bottom = mTextPaint.descent() - mTextPaint.ascent();
                rectF.left += (rect.width() - rectF.right) / 2.0f;
                rectF.top += (rect.height() - rectF.bottom) / 2.0f;

                xy[0] = rectF.left;
                xy[1] = rectF.top - mTextPaint.ascent();
                xy[2] = w / 2;
                xy[3] = h / 2 + offset;
                xy[4] = slantedDegrees;
                break;

            // 右下角的
            case SlantedMode.MODE_RIGHT_BOTTOM_TRIANGLE:
            case SlantedMode.MODE_RIGHT_BOTTOM:
                rect = new Rect(offset, offset, w + offset, h + offset);
                rectF = new RectF(rect);
                rectF.right = mTextPaint.measureText(mSlantedText, 0, mSlantedText.length());
                rectF.bottom = mTextPaint.descent() - mTextPaint.ascent();
                rectF.left += (rect.width() - rectF.right) / 2.0f;
                rectF.top += (rect.height() - rectF.bottom) / 2.0f;
                xy[0] = rectF.left;
                xy[1] = rectF.top - mTextPaint.ascent();
                xy[2] = w / 2 + offset;
                xy[3] = h / 2 + offset;
                xy[4] = -slantedDegrees;
                break;
            default:
                Log.d(TAG, "mode is error!");
                break;
        }
        return xy;
    }

    /**
     * 设置文字
     *
     * @param str 文字
     * @return 重新绘制后的SlantedTextView
     */
    public SlantedTextView setText(String str) {
        mSlantedText = str;
        postInvalidate();
        return this;
    }

    /**
     * 设置文字
     *
     * @param res 文字资源id
     * @return 重新绘制后的SlantedTextView
     */
    public SlantedTextView setText(int res) {
        String str = getResources().getString(res);
        if (!TextUtils.isEmpty(str)) {
            setText(str);
        }
        return this;
    }

    public String getText() {
        return mSlantedText;
    }

    /**
     * 设置背景颜色
     *
     * @param color 背景颜色值
     * @return 重新绘制后的SlantedTextView
     */
    public SlantedTextView setSlantedBackgroundColor(int color) {
        mSlantedBackgroundColor = color;
        mPaint.setColor(mSlantedBackgroundColor);
        postInvalidate();
        return this;
    }

    /**
     * 设置文字颜色
     *
     * @param color 文字颜色
     * @return 重新绘制后的SlantedTextView
     */
    public SlantedTextView setTextColor(int color) {
        mTextColor = color;
        mTextPaint.setColor(mTextColor);
        postInvalidate();
        return this;
    }


    /**
     * 设置文字大小
     *
     * @param size 文字大小
     * @return 重新绘制后的SlantedTextView
     */
    public SlantedTextView setTextSize(int size) {
        this.mTextSize = size;
        mTextPaint.setTextSize(mTextSize);
        postInvalidate();
        return this;
    }

    /**
     * 设置Slanted倾斜背景长度
     *
     * @param length Slanted倾斜背景长度
     * @return 重新绘制后的SlantedTextView
     */
    public SlantedTextView setSlantedLength(int length) {
        mSlantedLength = length;
        postInvalidate();
        return this;
    }

    /**
     * 设置倾斜模式
     *
     * @param mode 倾斜模式
     * @return 重新绘制后的SlantedTextView
     */
    public SlantedTextView setMode(int mode) {
        if (mMode > SlantedMode.MODE_RIGHT_BOTTOM_TRIANGLE || mMode < 0) {
            throw new IllegalArgumentException(mode + "is illegal argument ,please use right value");
        }
        this.mMode = mode;
        postInvalidate();
        return this;
    }

    public int getMode() {
        return mMode;
    }
}
