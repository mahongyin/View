package com.mhy.view.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 项目名 View
 * 所在包 com.mhy.view.utils
 * 作者 mahongyin
 * 时间 2020-03-08 9:36
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */
public class DateUtil {
    /**
     * //今天日期
     *
     * @return //2019-1-1
     */
    public static String getdateForm() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        return sdf.format(d);
    }

    /**
     * //获取1-7  今天是一周第几天 得到1-7
     *
     * @param datetime 2019-10-13
     * @return 周一 1 周日 7
     */
    public static int postionToWeek(String datetime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //0-6
        Calendar cal = Calendar.getInstance();
        Date date;
        try {
            date = sdf.parse(datetime);
            cal.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 日0 123456
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w == 0) {
            w = 7;
        }
        return w;
    }

}
