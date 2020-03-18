package com.mhy.view.xiaolei123;

/**
 * 项目名 View
 * 所在包 com.mhy.view.xiaolei123
 * 作者 mahongyin
 * 时间 2020-03-09 0:29
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.ColorInt;

import com.mhy.view.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 九宫格控件
 * Created by xiaolei on 2017/3/16.
 */

public class LockView extends View
{
    private int pointCount = 3;
    private Cell cells[][];
    private Paint mPaint;
    private int ScreenWidth;//屏幕宽度
    private int ScreenHeight;//屏幕高度
    private int RADIUS;//半径
    private int OFFSET;//点与点之间的距离
    private int startX, startY;
    private int lastTouchX = 0;
    private int lastTouchY = 0;
    private List<Cell> selectCells = new ArrayList<>();

    private int normalCricleColor = Color.WHITE;//正常的宫格的颜色
    private int selectCricleColor = Color.RED;//选中的宫格的颜色

    private int normalCriclePointColor = Color.WHITE;//正常的宫格内点的颜色
    private int selectCriclePointColor = Color.RED;//选中的宫格内点的颜色

    private int lineColor = Color.RED;//线的颜色
    private int topLineColor = Color.RED;//线的顶端的颜色
    private int circleStrokeWidth = 5;//圆的环的宽度
    private int lineStrokeWidth = 4;//线的宽度

    private boolean showInLastCircle = true;//画线的时候，是否在最后的那个圈内显示
    private int minSeleted = 4;//最小选中数量
    private OnSelectListener onSelectListener;

    public LockView(Context context)
    {
        this(context, null);
    }

    public LockView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public LockView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.LockView);

        pointCount = array.getInt(R.styleable.LockView_pointCount,3);
        showInLastCircle = array.getBoolean(R.styleable.LockView_showInLastCircle,true);
        lineStrokeWidth = array.getInt(R.styleable.LockView_lineStrokeWidth,4);
        circleStrokeWidth = array.getInt(R.styleable.LockView_circleStrokeWidth,4);

        topLineColor = array.getColor(R.styleable.LockView_topLineColor,Color.RED);
        lineColor = array.getColor(R.styleable.LockView_lineColor,Color.RED);
        selectCriclePointColor = array.getColor(R.styleable.LockView_selectCriclePointColor,Color.RED);

        normalCriclePointColor = array.getColor(R.styleable.LockView_normalCriclePointColor,Color.WHITE);
        normalCricleColor = array.getColor(R.styleable.LockView_normalCricleColor,Color.WHITE);
        selectCricleColor = array.getColor(R.styleable.LockView_selectCricleColor,Color.RED);

        array.recycle();
        init(context);
    }

    private void init(Context context)
    {
        cells = new Cell[pointCount][pointCount];
        mPaint = new Paint();
        mPaint.setStrokeWidth(5);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStyle(Paint.Style.STROKE);
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        ScreenWidth = dm.widthPixels;
        ScreenHeight = dm.heightPixels;
        Log.e("LockView", "ScreenWidth:" + ScreenWidth + " ScreenHeight:" + ScreenHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        // 父容器传过来的宽度方向上的模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        // 父容器传过来的高度方向上的模式
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        // 父容器传过来的宽度的值
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()
                - getPaddingRight();
        // 父容器传过来的高度的值
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingLeft()
                - getPaddingRight();
        Log.e("LockView","onMeasure(width:"+width+",height:"+height+")");

        //计算半径
        RADIUS = Math.min(width, height) / ((pointCount * 2) + (pointCount + 1));
        OFFSET = RADIUS;
        //开始计算左上角的X,Y轴
        if (ScreenWidth < ScreenHeight)//竖屏
        {
            Log.d("LockView", "竖屏");
            startX = RADIUS * 2;
            startY = RADIUS * 2;
        } else//横屏
        {
            Log.d("LockView", "横屏");
            startX = (width - height)/2 + (RADIUS * 2);
            startY = RADIUS * 2;
        }
        initCells();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void initCells()//计算每个点的位置
    {
        for (int i = 0; i < cells.length; i++)
        {
            for (int j = 0; j < cells[i].length; j++)
            {
                if(cells[i][j] == null)
                {
                    cells[i][j] = new Cell();
                }
                cells[i][j].x = (((j + 1) * (3 * RADIUS)) - RADIUS) + (startX - RADIUS * 2);
                cells[i][j].y = (((i + 1) * (3 * RADIUS)) - RADIUS);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                selectCells.clear();
                Cell selectCell = inWhichCircle((int) event.getX(), (int) event.getY());
                if(selectCell != null && !selectCells.contains(selectCell))
                {
                    selectCells.add(selectCell);
                    this.postInvalidate();
                }
                break;
            }
            case MotionEvent.ACTION_MOVE:
            {
                Cell selectCell = inWhichCircle((int) event.getX(), (int) event.getY());
                if(selectCell != null && !selectCells.contains(selectCell))
                {
                    selectCells.add(selectCell);
                }
                lastTouchX = (int) event.getX();
                lastTouchY = (int) event.getY();
                this.postInvalidate();
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                lastTouchX = 0;
                lastTouchY = 0;
                if(selectCells.size() < minSeleted)//抬起手的时候，判断选中的点的数量是否小小于最小选中数
                {
                    selectCells.clear();
                }else if(onSelectListener != null)
                {
                    List<Cell> list = new ArrayList<>();
                    list.addAll(selectCells);
                    onSelectListener.onSelected(list);
                }
                this.postInvalidate();
                break;
            }
        }
        return true;
    }

    /**
     * 判断某个触摸点，是否在某个宫格内
     * @param x
     * @param y
     * @return
     */
    private Cell inWhichCircle(int x, int y)
    {
        Cell result = null;
        for (int i = 0; i < cells.length; i++)
        {
            for (int j = 0; j < cells[i].length; j++)
            {
                //选择画笔&&画圆
                Cell cell = cells[i][j];
                if (isInCell(cell,x,y))
                {
                    return cell;
                }
            }
        }
        return result;
    }

    /**
     * 这个点是否在这个宫格里
     * @param cell
     * @param x
     * @param y
     * @return
     */
    private boolean isInCell(Cell cell,int x,int y)
    {
        return (Math.abs(x - cell.x) < RADIUS) && Math.abs(y - cell.y) < RADIUS;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        drawCell(canvas);
        drawLine(canvas);
    }

    /**
     * 画线
     * @param canvas
     */
    private void drawLine(Canvas canvas)
    {
        mPaint.setColor(lineColor);
        mPaint.setStrokeWidth(lineStrokeWidth);
        Cell lastCell = null;
        for (Cell cell:selectCells)
        {
            if(lastCell != null)
            {
                canvas.drawLine(cell.x, cell.y, lastCell.x, lastCell.y, mPaint);
            }
            lastCell = cell;
        }
        if(showInLastCircle)
        {
            if((lastTouchX != 0 || lastTouchY != 0)
                    && lastCell != null)
            {
                canvas.drawLine(lastCell.x, lastCell.y,lastTouchX,lastTouchY, mPaint);
                mPaint.setColor(topLineColor);
                //线的顶端，画一个圆角的视觉效果，我喜欢
                canvas.drawCircle(lastTouchX, lastTouchY,0.8f, mPaint);
            }
        }else
        {
            if((lastTouchX != 0 || lastTouchY != 0)
                    && lastCell != null
                    && !isInCell(lastCell,lastTouchX,lastTouchY))
            {
                canvas.drawLine(lastCell.x, lastCell.y,lastTouchX,lastTouchY, mPaint);
                mPaint.setColor(topLineColor);
                //线的顶端，画一个圆角的视觉效果，我喜欢
                canvas.drawCircle(lastTouchX, lastTouchY,0.8f, mPaint);
            }
        }

    }

    /**
     * 画出圆
     * @param canvas
     */
    private void drawCell(Canvas canvas)
    {
        for (int i = 0; i < cells.length; i++)
        {
            for (int j = 0; j < cells[i].length; j++)
            {
                //选择画笔&&画圆
                Cell cell = cells[i][j];
                if (selectCells.contains(cell))
                {
                    mPaint.setColor(selectCricleColor);
                    mPaint.setStrokeWidth(circleStrokeWidth);
                    canvas.drawCircle(cell.x, cell.y, RADIUS, mPaint);
                    mPaint.setColor(selectCriclePointColor);
                    canvas.drawCircle(cell.x, cell.y, circleStrokeWidth/2, mPaint);
                } else
                {
                    mPaint.setColor(normalCricleColor);
                    mPaint.setStrokeWidth(circleStrokeWidth);
                    canvas.drawCircle(cell.x, cell.y, RADIUS, mPaint);
                    mPaint.setColor(normalCriclePointColor);
                    canvas.drawCircle(cell.x, cell.y, circleStrokeWidth/2, mPaint);
                }

            }
        }
    }

    public static class Cell
    {
        public int x;
        public int y;
    }

    /**
     * 设置宫格数量 X * X
     * @param pointCount
     */
    public void setPointCount(int pointCount)
    {
        this.pointCount = pointCount;
    }

    /**
     * 设置普通宫格颜色
     * @param normalCricleColor
     */
    public void setNormalCricleColor(@ColorInt int normalCricleColor)
    {
        this.normalCricleColor = normalCricleColor;
    }

    /**
     * 设置选中宫格颜色
     * @param selectCricleColor
     */
    public void setSelectCricleColor(@ColorInt int selectCricleColor)
    {
        this.selectCricleColor = selectCricleColor;
    }

    /**
     * 设置普通宫格内点的颜色
     * @param normalCriclePointColor
     */
    public void setNormalCriclePointColor(@ColorInt int normalCriclePointColor)
    {
        this.normalCriclePointColor = normalCriclePointColor;
    }

    /**
     * 设置选中宫格内点的颜色
     * @param selectCriclePointColor
     */
    public void setSelectCriclePointColor(@ColorInt int selectCriclePointColor)
    {
        this.selectCriclePointColor = selectCriclePointColor;
    }

    /**
     * 设置线条颜色
     * @param lineColor
     */
    public void setLineColor(@ColorInt int lineColor)
    {
        this.lineColor = lineColor;
    }

    /**
     * 设置线条顶端颜色
     * @param topLineColor
     */
    public void setTopLineColor(@ColorInt int topLineColor)
    {
        this.topLineColor = topLineColor;
    }

    /**
     * 设置宫格环宽度
     * @param circleStrokeWidth
     */
    public void setCircleStrokeWidth(int circleStrokeWidth)
    {
        this.circleStrokeWidth = circleStrokeWidth;
    }

    /**
     * 设置线条宽度
     * @param lineStrokeWidth
     */
    public void setLineStrokeWidth(int lineStrokeWidth)
    {
        this.lineStrokeWidth = lineStrokeWidth;
    }

    /**
     * 设置线条是否在最后一个宫格内显示
     * @param showInLastCircle
     */
    public void setShowInLastCircle(boolean showInLastCircle)
    {
        this.showInLastCircle = showInLastCircle;
    }

    /**
     * 设置最小选中数量
     * @param minSeleted
     */
    public void setMinSeleted(int minSeleted)
    {
        this.minSeleted = minSeleted;
    }

    public void setOnSelectListener(OnSelectListener onSelectListener)
    {
        this.onSelectListener = onSelectListener;
    }

    /**
     * 选中完成监听事件
     */
    public static interface OnSelectListener
    {
        void onSelected(List<Cell> list);
    }
}

