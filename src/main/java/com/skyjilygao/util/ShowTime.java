package com.skyjilygao.util;

import com.ocpsoft.pretty.time.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ShowTime extends PrettyTime {
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * 1天毫秒数
     */
    private static long dayTimeMillis = 24 * 60 * 60 * 1000;
    private static int default_days = 90;
    /**
     * 90天毫秒数（默认）
     */
    private static long threeMonth = dayTimeMillis * default_days;

    /**
     * 返回多久以前：几分钟前，几小时前，几天前等等.
     * 默认：如果超过90天以前，就直接显示时间：yyyy-MM-dd HH:mm:ss
     * @param date
     * @return
     */
    public static String showTimeStr(Date date){
        return showTimeStr(date, default_days);
    }
    /**
     * 返回多久以前：几分钟前，几小时前，几天前等等.
     * 默认：如果超过90天以前，就直接显示时间：yyyy-MM-dd HH:mm:ss
     * @param date
     * @param days 多少天：表示如果date早于指定days以前，就直接显示时间字符串
     * @return
     */
    public static String showTimeStr(Date date, int days){
        long time = date.getTime();
        if (System.currentTimeMillis() - time > days * dayTimeMillis) {
            return df.format(date);
        }
        return prettyTime(date);
    }
    /**
     * 返回多久以前：几分钟前，几小时前，几天前等等
     * @param date
     * @return 几分钟前，几小时前，几天前等等。时间越长，精度可能不准
     */
    public static String prettyTime(Date date){
        PrettyTime p = new PrettyTime();
        return p.format(date);
    }
}
