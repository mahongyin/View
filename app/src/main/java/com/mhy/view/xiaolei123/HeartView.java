package com.mhy.view.xiaolei123;

/**
 * 项目名 View
 * 所在包 com.mhy.view.sigin
 * 作者 mahongyin
 * 时间 2020-03-08 10:01
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;

import com.mhy.view.R;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingDeque;

    /**
     * 显示心电的控件
     */
    public class HeartView extends View
    {
        // 数据的最大值
        private int Max;
        // 数据的最小值
        private int Min;
        // 数据一秒钟采集频率，默认100个点一秒种
        private int hz;
        // 控件显示几秒钟的心跳,默认显示2秒钟的心跳
        private float showSeconds;
        // 要画的基准线
        private int baseLine;
        // 每个方格的行数
        private int grid_row;
        // 每个方格的高度
        private int grid_row_height;
        // 心率线条的颜色 默认红色
        private int heartColor;
        // 表格线条的颜色 默认灰色
        private int heart_grid_line_color;
        // 表格边框的颜色 默认灰色
        private int heart_grid_border_color;
        // 心电线的宽度
        private int heart_line_border;
        // 大表格的边框的宽度
        private int heart_grid_border;
        // 每个小格子的线的宽度
        private int heart_grid_line_border;
        // 速度控制
        private float heart_speed;

        private int viewHeight = 0;
        private int viewWidth = 0;

        // 画笔
        private Paint paint;
        // 需要画心电的路径
        private Path path = new Path();
        // 根据显示秒数,以及采样频率算出总共需要申请多少个内存的数据
        private int[] showTimeDatas;
        // 待显示的数据队列
        private LinkedBlockingDeque<Integer> dataQueue = new LinkedBlockingDeque<>();
        // 定时运行栈
        private HeartTask heartTask = null;
        // 精准定时器
        private Timer timer = new Timer();

        public HeartView(Context context)
        {
            this(context, null);
        }

        public HeartView(Context context, AttributeSet attrs)
        {
            this(context, attrs, 0);
        }

        public HeartView(Context context, AttributeSet attrs, int defStyleAttr)
        {
            super(context, attrs, defStyleAttr);
            paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            paint.setColor(Color.RED);
            // 线条交界处，钝化处理，看起来是圆点
            paint.setStrokeJoin(Paint.Join.ROUND);

            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HeartView);
            // 心电线的宽度
            heart_line_border = typedArray.getDimensionPixelSize(R.styleable.HeartView_heart_line_border, (int) dip2px(context, 1f));
            // 每个表格的行数（就是小格子数,默认5格
            grid_row = typedArray.getInt(R.styleable.HeartView_heart_grid_row, 5);
            // 大表格的边框的宽度
            heart_grid_border = typedArray.getDimensionPixelSize(R.styleable.HeartView_heart_grid_border, (int) dip2px(context, 2f));
            // 每个小格子的宽高
            grid_row_height = typedArray.getDimensionPixelSize(R.styleable.HeartView_heart_grid_row_height, (int) dip2px(context, 10f));
            // 每个小格子的线的宽度
            heart_grid_line_border = typedArray.getDimensionPixelSize(R.styleable.HeartView_heart_grid_line_border, (int) dip2px(context, 1f));
            // 基准线，默认2000
            baseLine = typedArray.getInteger(R.styleable.HeartView_heart_base_line, 2000);
            // 最大值，默认4000
            Max = typedArray.getInteger(R.styleable.HeartView_heart_max, 4096);
            // 最小值，默认0
            Min = typedArray.getInteger(R.styleable.HeartView_heart_min, 0);
            // 数据采集频率，默认100个点一秒钟
            hz = typedArray.getInteger(R.styleable.HeartView_heart_hz, 100);
            // 一个控件，可以显示的心率的时长 ,默认为2秒钟
            showSeconds = typedArray.getFloat(R.styleable.HeartView_heart_show_seconds, 2f);
            // 心率线条的颜色 默认红色
            heartColor = typedArray.getColor(R.styleable.HeartView_heart_color, Color.RED);
            // 表格线条的颜色 默认绿色
            heart_grid_line_color = typedArray.getColor(R.styleable.HeartView_heart_grid_line_color, Color.parseColor("#DBDBDB"));
            // 表格边框的颜色 默认绿色
            heart_grid_border_color = typedArray.getColor(R.styleable.HeartView_heart_grid_border_color, Color.parseColor("#DBDBDB"));
            // 播放速度的控制
            heart_speed = typedArray.getFloat(R.styleable.HeartView_heart_speed, 1.0f);
            typedArray.recycle();

            // 速度怎么可以小于0
            if (heart_speed < 0)
            {
                throw new RuntimeException("Attributes heart_speed Can Not < 0 ");
            }
            // 最小值怎么可以大于或等于最大值
            if (Min >= Max)
            {
                throw new RuntimeException("Attributes heart_min Can Not >= heart_max ");
            }

            showTimeDatas = new int[(int) (showSeconds * hz)];
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
        {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            viewHeight = measureHeight(heightMeasureSpec);
            viewWidth = measureWidth(widthMeasureSpec);
            path.moveTo(0, viewHeight);
        }

        /**
         * 重新部署发点任务
         */
        private synchronized void publishJob()
        {
            // 根据采集的频率，自动算出每一个点之间暂停的时间
            long yield = (int) (1000 / (hz * heart_speed));
            if (heartTask != null)
            {
                heartTask.cancel();
                heartTask = null;
            }
            heartTask = new HeartTask();
            timer.scheduleAtFixedRate(heartTask, 0, yield);
        }

        /**
         * 设置表格的行数
         *
         * @param grid_row
         */
        public void setGrid_row(int grid_row)
        {
            this.grid_row = grid_row;
        }

        /**
         * 设置每个小方格的高度
         *
         * @param height
         */
        public void setGrid_row_height(int height)
        {
            this.grid_row_height = height;
        }

        /**
         * 设置线条颜色
         *
         * @param color
         */
        public void setHeartColor(@ColorInt int color)
        {
            this.heartColor = color;
        }

        /**
         * 设置画小表格的颜色
         *
         * @param color
         */
        public void setHeartGridLineColor(@ColorInt int color)
        {
            this.heart_grid_line_color = color;
        }

        /**
         * 设置大表格边框颜色
         *
         * @param color
         */
        public void setHeartGridBorderColor(@ColorInt int color)
        {
            this.heart_grid_border_color = color;
        }

        /**
         * 设置线条宽度
         *
         * @param border
         */
        public void setHeartLineBorder(@ColorInt int border)
        {
            this.heart_line_border = border;
        }

        /**
         * 设置大格边框线宽
         *
         * @param border
         */
        public void setHeartGridBorder(int border)
        {
            this.heart_grid_border = border;
        }

        /**
         * 设置小格线宽
         *
         * @param border
         */
        public void setHeartGridLineBorder(int border)
        {
            this.heart_grid_line_border = border;
        }

        /**
         * 设置倍速
         *
         * @param speed
         */
        public void setHeartSpeed(@FloatRange(from = 0.0, to = Float.MAX_VALUE) float speed)
        {
            this.heart_speed = speed;
            // 速度怎么可以小于0
            if (heart_speed < 0)
            {
                throw new RuntimeException("Attributes heart_speed Can Not < 0 ");
            }
            publishJob();
        }

        /**
         * 添加一个点，会自动依据频率来动态显示
         *
         * @param point
         */
        public synchronized void offer(int point)
        {
            dataQueue.offer(point);
            if (heartTask == null)
            {
                publishJob();
            }
        }

        /**
         * 添加一组点，自动依据频率来动态显示
         *
         * @param points
         */
        public void offer(int[] points)
        {
            for (int i = 0; i < points.length; i++)
                offer(points[i]);
        }

        /**
         * 设置显示死数据，没有动态走动效果
         */
        public synchronized void setData(int[] points)
        {
            // 如果传过来的数据 比要显示的短，那么先根据数据长度替换，再将尾巴数据清空
            // 传递数据:[5，6]
            // 显示数据:[1,1,1]
            // 替换数据:[5,6,1]
            // 尾巴清空:[5,6,0]
            if (points.length <= showTimeDatas.length)
            {
                System.arraycopy(points, 0, showTimeDatas, 0, points.length);
                for (int i = points.length; i < showTimeDatas.length; i++)
                {
                    showTimeDatas[i] = 0;
                }
            } else
            {
                // 如果传过来的数据，比显示的要长，那么以显示的长度为依据进行数据替换
                // 传递数据:[5,6,7]
                // 显示数据:[1,2]
                // 替换数据:[5,6]
                System.arraycopy(points, 0, showTimeDatas, 0, showTimeDatas.length);
            }
            postInvalidate();
        }

        /**
         * 设置每秒的采集频率
         *
         * @param hz
         */
        public synchronized void setHz(int hz)
        {
            this.hz = hz;
            this.showTimeDatas = new int[(int) (showSeconds * hz)];
            publishJob();
        }

        /**
         * 设置最大值
         *
         * @param max
         */
        public synchronized void setMax(int max)
        {
            this.Max = max;
            // 最小值怎么可以大于或等于最大值
            if (Min >= Max)
            {
                throw new RuntimeException("Attributes heart_min Can Not >= heart_max ");
            }
        }

        /**
         * 设置最小值
         *
         * @param min
         */
        public void setMin(int min)
        {
            Min = min;
            // 最小值怎么可以大于或等于最大值
            if (Min >= Max)
            {
                throw new RuntimeException("Attributes heart_min Can Not >= heart_max ");
            }
        }

        /**
         * 设置控件显示几秒钟的数据
         *
         * @param showSeconds
         */
        public synchronized void setShowSeconds(float showSeconds)
        {
            this.showSeconds = showSeconds;
            this.showTimeDatas = new int[(int) (showSeconds * hz)];
        }

        /**
         * 清空图案
         */
        public synchronized void clear()
        {
            for (int i = 0; i < showTimeDatas.length; i++)
                showTimeDatas[i] = 0;
            postInvalidate();
        }

        /**
         * 设置基准线
         *
         * @param baseLine
         */
        public void setBaseLine(int baseLine)
        {
            this.baseLine = baseLine;
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);
            int[] showDatas = showTimeDatas;
            // 画表格
            int baseY = calculateY(baseLine - Min, Max - Min, viewHeight);
            // 基准线以上
            for (int y = baseY; y > 0; y -= grid_row_height)
            {
                if ((baseY - y) / grid_row_height % grid_row == 0)
                {
                    paint.setStrokeWidth(heart_grid_border);
                    paint.setColor(heart_grid_border_color);
                } else
                {
                    paint.setStrokeWidth(heart_grid_line_border);
                    paint.setColor(heart_grid_line_color);
                }
                canvas.drawLine(0, y, viewWidth, y, paint);
            }
            // 基准线以下
            for (int y = baseY; y < viewHeight; y += grid_row_height)
            {
                if ((y - baseY) / grid_row_height % grid_row == 0)
                {
                    paint.setStrokeWidth(heart_grid_border);
                    paint.setColor(heart_grid_border_color);
                } else
                {
                    paint.setStrokeWidth(heart_grid_line_border);
                    paint.setColor(heart_grid_line_color);
                }
                canvas.drawLine(0, y, viewWidth, y, paint);
            }
            // 中心线以右
            int centerX = viewWidth / 2;
            for (int x = centerX; x < viewWidth; x += grid_row_height)
            {
                if ((x - centerX) / grid_row_height % grid_row == 0)
                {
                    paint.setStrokeWidth(heart_grid_border);
                    paint.setColor(heart_grid_border_color);
                } else
                {
                    paint.setStrokeWidth(heart_grid_line_border);
                    paint.setColor(heart_grid_line_color);
                }
                canvas.drawLine(x, 0, x, viewHeight, paint);
            }
            // 中心线以左
            for (int x = centerX; x > 0; x -= grid_row_height)
            {
                if ((centerX - x) / grid_row_height % grid_row == 0)
                {
                    paint.setStrokeWidth(heart_grid_border);
                    paint.setColor(heart_grid_border_color);
                } else
                {
                    paint.setStrokeWidth(heart_grid_line_border);
                    paint.setColor(heart_grid_line_color);
                }
                canvas.drawLine(x, 0, x, viewHeight, paint);
            }


            // 画心电
            paint.setColor(heartColor);
            paint.setStrokeWidth(heart_line_border);
            int firstData = showDatas[0];
            int firstY = calculateY(firstData - Min, Max - Min, viewHeight);
            path.reset();
            path.moveTo(0, firstY);
            for (int i = 0; i < showDatas.length; i++)
            {
                int value = showDatas[i];
                int x = (int) (((float) i / showDatas.length) * viewWidth);
                int y = calculateY(value - Min, Max - Min, viewHeight);
                path.lineTo(x, y);
            }
            canvas.drawPath(path, paint);
        }


        /**
         * 根据最大值，控件高度，计算出当前值对应的控件的 Y 坐标
         *
         * @param value      参与计算的值
         * @param Region     最大值 - 最小值的区域
         * @param viewHeight 控件高度
         * @return
         */
        private static int calculateY(int value, int Region, int viewHeight)
        {
            return viewHeight - ((int) (((float) value / Region) * viewHeight));
        }

        /**
         * 测量自定义View的高度
         */
        private int measureHeight(int heightMeasureSpec)
        {
            int heightResult = 0;
            int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);
            switch (heightSpecMode)
            {
                case View.MeasureSpec.UNSPECIFIED:
                {
                    heightResult = heightSpecSize;
                }
                break;
                case View.MeasureSpec.AT_MOST:
                {
                    heightResult = View.MeasureSpec.getSize(heightMeasureSpec);
                }
                break;
                case View.MeasureSpec.EXACTLY:
                {
                    heightResult = View.MeasureSpec.getSize(heightMeasureSpec);
                }
            }
            return heightResult;
        }

        /**
         * 测量自定义View的宽度
         */
        private int measureWidth(int widthMeasureSpec)
        {
            int widthResult = 0;
            int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
            switch (widthSpecMode)
            {
                case View.MeasureSpec.UNSPECIFIED:
                {
                    widthResult = widthSpecSize;
                }
                break;
                case View.MeasureSpec.AT_MOST:
                {
                    widthResult = View.MeasureSpec.getSize(widthMeasureSpec);
                }
                break;
                case View.MeasureSpec.EXACTLY:
                {
                    widthResult = View.MeasureSpec.getSize(widthMeasureSpec);
                }
            }
            return widthResult;
        }


        /**
         * dp 转 px
         *
         * @param context  上下文
         * @param dipValue dp值
         * @return
         */
        private float dip2px(Context context, float dipValue)
        {
            float scale = context.getResources().getDisplayMetrics().density;
            return dipValue * scale + 0.5f;
        }


        /**
         * 释放资源
         */
        public synchronized void recycle()
        {
            if (heartTask != null)
            {
                heartTask.cancel();
            }
            timer.cancel();
        }

        /**
         * 发点的任务
         */
        private class HeartTask extends TimerTask
        {
            @Override
            public void run()
            {
                try
                {
                    Integer point = dataQueue.poll();
                    if (point != null)
                    {
                        for (int i = 0; i < showTimeDatas.length; i++)
                        {
                            if (i + 1 < showTimeDatas.length)
                            {
                                showTimeDatas[i] = showTimeDatas[i + 1];
                            } else
                            {
                                showTimeDatas[i] = point;
                            }
                        }
                        postInvalidate();
                    } else
                    {
                        cancel();
                        heartTask = null;
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

