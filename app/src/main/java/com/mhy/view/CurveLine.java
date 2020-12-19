package com.mhy.view;

/**
 * Created By Mahongyin
 * Date    2020/12/19 13:06
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maoyujiao on 2020/4/14.
 * 参考 https://www.jianshu.com/p/c4601bab860a
 * 折线的平滑处理
 * 绘制曲线，最常用的参数曲线函数就是贝塞尔曲线。
 *  二次贝塞尔曲线 quadTo
 * 三次贝塞尔曲线 cubicTo ,每个中间点的的切线上都会扩展2个点，然后以3个点画曲线
 *
 * 令 A0表示第一个点 B3表示第三个点 A2表示第二个点
 A0和B3连线的斜率 k = (B3Y - A0Y) / (B3X - A0X)
 常数 b = A3Y - k * A3X
 则
 A2的X坐标 A2X = A3X - (A3X - A0X) * rate
 A2的Y坐标 A2Y = k * A2X + b
 B1的X坐标 B1X = A3X + (B3X - A3X) * rate
 B1的Y坐标 B1Y = k * B1X + b

 rate是一个（0, 0.5）区间内的值，数值越大，数值点之间的曲线弧度越小。

 */

public class CurveLine extends View {
    private static final String TAG = "CurveLine";
    private final ArrayList<PointF> cubicToPoints = new ArrayList<>();
    List<PointF> points = new ArrayList<>();
    Path path = new Path();
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    float rate = 0.4f;

    public CurveLine(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        initPoint();
        initControlPoint();


    }

    /**
     * //构造3次贝塞儿曲线
     */
    private void initControlPoint() {

        for (int i = 0; i < points.size() ; i++) {
            if(i == 0){
                cubicToPoints.add(points.get(i));
                //后控点
                PointF pointF_2 = new PointF();
                pointF_2.x = points.get(i).x + (points.get(i + 1).x - points.get(i).x)*rate;
                pointF_2.y = points.get(i).y;
                cubicToPoints.add(pointF_2);

            }else if(i == points.size() - 1){
                //前控点
                PointF pointF_1 = new PointF();
                pointF_1.x = points.get(i).x - (points.get(i).x - points.get(i - 1).x)*rate;
                pointF_1.y = points.get(i).y;
                cubicToPoints.add(pointF_1);
                cubicToPoints.add(points.get(i));

            }else {
                float k = (points.get(i + 1).y - points.get(i - 1).y) / (points.get(i + 1).x - points.get(i - 1).x);
                float b = points.get(i).y - k * points.get(i).x;
                Log.d(TAG, "CurveLine: k" + k + ",b" + b);

                PointF point_1 = new PointF();
                point_1.x = points.get(i).x - (points.get(i).x - points.get(i - 1).x) * rate;
                point_1.y = k * point_1.x + b;
                cubicToPoints.add(point_1);//前控制点

                cubicToPoints.add(points.get(i));//当前点

                PointF point_2 = new PointF();
                point_2.x = points.get(i).x + (points.get(i + 1).x - points.get(i).x) * rate;
                point_2.y = k * point_2.x + b;
                cubicToPoints.add(point_2);//后控制点
            }
        }
    }

    private void initPoint() {
        PointF point1 = new PointF();
        point1.set(100, 400);
        points.add(point1);

        PointF point2 = new PointF();
        point2.set(200, 300);
        points.add(point2);

        PointF point3 = new PointF();
        point3.set(300, 400);
        points.add(point3);

        PointF point4 = new PointF();
        point4.set(400, 300);
        points.add(point4);

        PointF point5 = new PointF();
        point5.set(500, 400);
        points.add(point5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.moveTo(points.get(0).x,points.get(0).y);
        for (int i = 0; i < points.size(); i++) {
            canvas.drawCircle(points.get(i).x,points.get(i).y,4,paint);
        }
        //每3个点画一条贝塞尔曲线
        //从1开始，将第二个点在第一条贝塞尔曲线上。
        for (int i = 1; i < cubicToPoints.size() - 2; i+=3) {
            path.cubicTo(cubicToPoints.get(i).x,cubicToPoints.get(i).y,
                    cubicToPoints.get(i + 1).x,cubicToPoints.get(i + 1).y,
                    cubicToPoints.get(i+ 2).x,cubicToPoints.get(i + 2).y);
        }
        canvas.drawPath(path,paint);

        // 二次贝塞尔没有经过这些点
//        for (int i = 1; i < points.size(); i++) {
//            path.quadTo(points.get(i-1).x,
//                    points.get(i-1).y,
//                    (points.get(i-1).x + points.get(i).x) / 2,
//                    (points.get(i-1 ).y + points.get(i ).y) / 2
//                    );
//        }
    }
}

