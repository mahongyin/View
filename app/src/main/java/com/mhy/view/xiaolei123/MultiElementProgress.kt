package com.mhy.view.xiaolei123;

/**
 * 项目名 View
 * 所在包 com.mhy.view.xiaolei123
 * 作者 mahongyin
 * 时间 2020-03-08 10:09
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

import android.util.ArrayMap
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import com.mhy.view.utils.DensityUtil
import kotlin.math.min

/**
 * Created by https://www.jianshu.com/p/da349c7141c3
 */

class MultiElementProgress @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private var strokeWidth = 20f //画笔宽度
    private var startAngle = -45f //开始角度
    private val paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = this@MultiElementProgress.strokeWidth
        }
    }
    private val rectf by lazy {
        when {
            mWidth > mHeight -> RectF(mWidth / 2 - mHeight / 2 + strokeWidth / 2, 0f + strokeWidth / 2, mWidth / 2 - mHeight / 2 + mHeight - strokeWidth / 2, mHeight - strokeWidth / 2)
            mWidth == mHeight -> RectF(0f + strokeWidth / 2, 0f + strokeWidth / 2, min(mWidth, mHeight) - strokeWidth / 2, min(mWidth, mHeight) - strokeWidth / 2)
            else -> RectF(0f + strokeWidth / 2, mHeight / 2 - mWidth / 2 + strokeWidth / 2, mWidth - strokeWidth / 2, mHeight / 2 - mWidth / 2 + mWidth - strokeWidth / 2)
        }
    }
    private val multiElement by lazy { ArrayMap<Int, Float>() }
    private var mHeight: Float = 0f
    private var mWidth: Float = 0f

    init {
        multiElement.put(Color.parseColor("#eadc4b"), 50f)
        multiElement.put(Color.parseColor("#f9a844"), 50f)
        multiElement.put(Color.parseColor("#b5db39"), 50f)
        strokeWidth = DensityUtil.dp2px(context, 16f).toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mHeight = View.MeasureSpec.getSize(heightMeasureSpec).toFloat()
        mWidth = View.MeasureSpec.getSize(widthMeasureSpec).toFloat()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        var angle = startAngle
        paint.strokeWidth = strokeWidth;
        var allValue = 0f
        multiElement.all { entry ->
            allValue += entry.value
            true
        }
        for (entry in multiElement) {
            val sweepAngle = (entry.value.toFloat() / allValue) * 360
            paint.color = entry.key
            canvas?.drawArc(rectf, angle, sweepAngle, false, paint)
            angle += sweepAngle
        }
        super.onDraw(canvas)
    }

    /**
     * 设置每一个选项的 Item
     */
    public fun setProgress(elements: List<Element>) {
        multiElement.clear()
        for (element in elements) {
            multiElement.put(element.color, element.progress)
        }
        postInvalidate()
    }

    /**
     * 设置画笔宽度
     */
    public fun setStrokeWidth(strokeWidth: Float) {
        this.strokeWidth = strokeWidth
        postInvalidate()
    }

    /**
     * 设置开始画的角度
     */
    public fun setStartAngle(startAngle: Float) {
        this.startAngle = startAngle
        postInvalidate()
    }


    public class Element(@ColorInt val color: Int, val progress: Float)

}

