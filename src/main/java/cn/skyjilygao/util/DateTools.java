package cn.skyjilygao.util;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static cn.skyjilygao.util.DateTools.Pattern.YYYY_MM_DD;
import static cn.skyjilygao.util.DateTools.Pattern.YYYY_MM_DD_HH_MM_SS;

/**
 * Title: 日期转换
 * <p>
 * Description: 进行日期转换的工具类
 * <p>
 *
 * @author skyjilygao
 * @version 1.0.0
 * @since
 */
public final class DateTools {

    /**
     * 日期字符串格式yyyyMMddHHmmss,得到的小时数0~23
     */
    public static final String DATE_24_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    /**
     * 注释内容
     */
    public static final int YEAR = 0;

    /**
     * 注释内容
     */
    public static final int MONTH = 1;

    /**
     * 注释内容
     */
    public static final int WEEK = 2;

    /**
     * 注释内容
     */
    public static final int DAY = 3;

    /**
     * 注释内容
     */
    public static final int HOUR = 4;

    /**
     * 注释内容
     */
    public static final int MINUTE = 5;

    /**
     * 注释内容
     */
    public static final int SECOND = 6;

    /**
     * 注释内容
     */
    public static final int MILLISECOND = 7;

    /**
     * 注释内容
     */
    public static final int MINUTEOFDAY = 8;

    /**
     * 毫秒
     */
    public static final int MSEL = 1000;

    /**
     * 小时
     */
    public static final int HOURS = 60;

    public DateTools() {
    }

    /**
     * 日期类型字符串 通过LocalDate转Date
     * @param dateStr 日期类型字符串
     * @return Date etc: 2018-06-06
     */
    public static Date str2Date(String dateStr) {
        LocalDate localDate =  LocalDate.parse(dateStr);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime dateTime = localDate.atStartOfDay(zoneId);
        return Date.from(dateTime.toInstant());
    }

    public long tt() {
        return System.currentTimeMillis();
    }

    /**
     * 获取指定格式的当前日期,格式描述符的含义参见JDK simpleDateFormat
     *
     * @param dateTimePattern String 指定日期格式，如:yyyyMMddHHmmss
     * @return String
     */
    public static String getCurrentDateTime(String dateTimePattern){
        if (dateTimePattern == null) {
            throw new IllegalArgumentException("input string parameter is null");
        }
        LocalDateTime dt = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dateTimePattern);
        return dt.format(dtf);
    }

    /**
     * 获取指定格式的当前日期,格式描述符的含义参见JDK simpleDateFormat
     * <p> 建议使用 getCurrentDateTime()
     * @param pattern String 日期格式，如:yyyyMMddHHmmss
     * @return String
     * @deprecated 建议使用 getCurrentDateTime()
     */
    @Deprecated
    public static String getCurrentDate(String pattern) {

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date now = new Date();
        return sdf.format(now);
    }

    /**
     * 返回昨天此时此刻
     * @return
     */
    public static Date getYesterday(){
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }
    /**
     * 根据服务器时间来 推算出 local 时间
     * <p>
     * 服务器时间要定义成 UTC 时间
     *
     * @param offsetHourStr
     * @return
     */
    public static long getCurrentLocalTime(int offsetHourStr) {
        return System.currentTimeMillis() + offsetHourStr * 60 * 60 * 1000;
    }

    /**
     * 获取指定格式的当前日期,格式描述符的含义参见JDK simpleDateFormat
     *
     * @param pattern String 日期格式，如:yyyyMMddHHmmss
     * @return String
     */
    public static String getCurrentDate(String pattern, int utcOffsetHour) {
        if (pattern == null) {
            throw new IllegalArgumentException("input string parameter is null");
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, utcOffsetHour);

        Date now = calendar.getTime();

        return sdf.format(now);
    }

    /**
     * 获取指定格式的当前日期,格式描述符的含义参见JDK simpleDateFormat
     *
     * @param pattern String 日期格式，如:yyyyMMddHHmmss
     * @return String
     */
    public static String getCurrentDate(String pattern, String time_zone_id) {
        if (pattern == null) {
            throw new IllegalArgumentException("input string parameter is null");
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone(time_zone_id));
        Date now = new Date();

        return sdf.format(now);
    }

    /**
     * 将日期增加一个增量，目前只能是，年，月，周，日，时，分，秒，毫秒
     *
     * @param date  long 原始时间
     * @param delta int 增量的大小
     * @param unit  int 增量的单位，YEAR, MONTH, DAY, HOUR, MINUTE, SECOND, MILLISECOND
     * @return long 从格林威治时间：1970年1月1日0点起的毫秒数
     */
    public static long getDateByOffset(long date, int delta, int unit) {
        if ((unit < YEAR) || (unit > MILLISECOND)) {
            throw new IllegalArgumentException("time unit must in [YEAR, MONTH, " + "WEEK, DAY, HOUR, MINUTE, SECOND, MILLISECOND], others not support");
        }
        Date dt = new Date(date);
        Calendar calendar = getLocalCalendar(dt);
        // 增加增量
        switch (unit) {
            case YEAR:
                calendar.add(Calendar.YEAR, delta);
                break;
            case MONTH:
                calendar.add(Calendar.MONTH, delta);
                break;
            case WEEK:
                calendar.add(Calendar.DAY_OF_WEEK, delta);
                break;
            case DAY:
                calendar.add(Calendar.DAY_OF_MONTH, delta);
                break;
            case HOUR:
                calendar.add(Calendar.HOUR, delta);
                break;
            case MINUTE:
                calendar.add(Calendar.MINUTE, delta);
                break;
            case SECOND:
                calendar.add(Calendar.SECOND, delta);
                break;
            case MILLISECOND:
                calendar.add(Calendar.MILLISECOND, delta);
                break;
            default:
                break;
        }
        // 获取新的时间，并转换成长整形
        Date ndt = calendar.getTime();
        return ndt.getTime();
    }

    /**
     * 获得东八时区的日历，并设置日历的当前日期
     *
     * @param date Date 日期
     * @return Calendar
     */
    public static Calendar getLocalCalendar(Date date) {
        // 设置为GMT+08:00时区
        String[] ids = TimeZone.getAvailableIDs(MINUTEOFDAY * HOURS * HOURS * MSEL);
        if (ids.length == 0) {
            throw new IllegalArgumentException("get id of GMT+08:00 time zone failed");
        }
        // SimpleTimeZone stz = new SimpleTimeZone(8 * 60 * 60 * 1000, ids[0]);
        // 创建Calendar对象，并设置为指定时间
        // Calendar calendar = new GregorianCalendar(stz);
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        // 设置成宽容方差
        if (!calendar.isLenient()) {
            calendar.setLenient(true);
        }
        // 设置SUNDAY为每周的第一天
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        // 设置日历的当前时间
        calendar.setTime(date);
        return calendar;
    }

    /**
     * 将日期长整型转换成字符串
     *
     * @param time    long 从格林威治时间：1970年1月1日0点起的毫秒数
     * @param pattern String 转换的目标格式
     * @return String
     */
    public static String long2TimeStr(long time, String pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("pattern parameter can not be null");
        }
        Date dt = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(dt);
    }

    /**
     * 将字符串转换成日期长整型
     *
     * @param timeString String 要转换的字符串
     * @param formate    String 转换的目标格串
     * @return long 从格林威治时间：1970年1月1日0点起的毫秒数
     * @throws ParseException ParseException
     */
    public static long timeStr2Long(String timeString, String formate)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(formate);
        Date myDay = sdf.parse(timeString);
        return myDay.getTime();
    }

    /**
     * 按要求转化时间的显示格式
     *
     * @param time       String 时间
     * @param oldpattern String 旧日期格式，:yyyyMMddHHmmss 格式描述符的含义参见JDK
     * @param newpattern String 新日期格式
     * @return String
     */
    public static String timeTransform(String time, String oldpattern, String newpattern) {
        if (oldpattern == null) {
            throw new IllegalArgumentException("the old pattern is null");
        }
        if (newpattern == null) {
            throw new IllegalArgumentException("the new pattern is null");
        }
        if (time == null) {
            return null;
        }
        SimpleDateFormat olddatepattern = new SimpleDateFormat(oldpattern);
        Date now = null;
        try {
            now = olddatepattern.parse(time);
            // 用原来的pattern解析日期，再和原来的比较，由此来检查时间是否合格
            String oldtime = olddatepattern.format(now);
            if (!oldtime.equals(time)) {
                throw new IllegalArgumentException("using Illegal time");
            }
        } catch (ParseException e) {

        }
        SimpleDateFormat newdatepattern = new SimpleDateFormat(newpattern);

        return newdatepattern.format(now);
    }

    /**
     * 将时间yyyyMMddhhmmss转为yyyy-MM-dd
     * hh:mm:ss、yyyyMMdd转为yyyy-MM-dd、yyyyMM转为yyyy-MM
     *
     * @param dateStr
     * @return
     */
    public static String toChangeMyDate(String dateStr) {
        String str = "";

        if (StringUtils.isBlank(dateStr)) {
            return str;
        }

        SimpleDateFormat sdf1 = null;
        SimpleDateFormat sdf2 = null;
        dateStr = dateStr.trim();
        if (14 == dateStr.length()) {
            sdf1 = new SimpleDateFormat(DATE_24_YYYYMMDDHHMMSS);
            sdf2 = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        } else if (8 == dateStr.length()) {
            sdf1 = new SimpleDateFormat("yyyyMMdd");
            sdf2 = new SimpleDateFormat(YYYY_MM_DD);
        } else if (6 == dateStr.length()) {
            sdf1 = new SimpleDateFormat("yyyyMM");
            sdf2 = new SimpleDateFormat("yyyy-MM");
        }

        if (!StringUtils.isBlank(dateStr) && null != sdf1 && null != sdf2) {
            try {
                str = sdf2.format(sdf1.parse(dateStr));
            } catch (ParseException e) {
            }
        }

        return str;
    }

    /**
     * 将日期型转换成字符串
     *
     * @param time    Date
     * @param pattern String 转换的目标格式
     * @return String
     */
    public static String date2TimeStr(Date time, String pattern) {
        if (pattern == null) {

            throw new IllegalArgumentException("pattern parameter can not be null");
        }
        if (time == null) {

            throw new IllegalArgumentException("time parameter can not be null");
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(time);
    }

    /**
     * 将日期增加一个增量，目前只能是，年，月，周，日，时，分，秒，毫秒
     *
     * @param date    long 原始时间
     * @param delta   int 增量的大小
     * @param unit    int 增量的单位，YEAR, MONTH, WEEK,DAY, HOUR, MINUTE, SECOND,
     *                MILLISECOND
     * @param pattern String 转换的目标格式
     * @return String 指定格式的日期字符串
     */
    public static String addDate(long date, int delta, int unit, String pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("pattern parameter can not be null");
        }
        return long2TimeStr(getDateByOffset(date, delta, unit), pattern);
    }

    /**
     * 比较两个yyyyMMddhhmmss格式的日期之间相差多少(以豪秒为单位)
     *
     * @param time1 第一个时间
     * @param time2 第二个时间
     * @return 时间差
     */
    public static long timeDistinction(String time1, String time2) {
        long distinction = 0;
        try {
            distinction = timeStr2Long(time1, DATE_24_YYYYMMDDHHMMSS) - timeStr2Long(time2, DATE_24_YYYYMMDDHHMMSS);
        } catch (ParseException e) {
        }

        return distinction;
    }

    /**
     * 比较两个yyyyMMddhhmmss格式的日期之间相差多少(以豪秒为单位)
     *
     * @param time1 第一个时间
     * @param time2 第二个时间
     * @return 时间差
     */
    public static long timeDistinction(String time1, String formate1, String time2, String formate2) {
        long distinction = 0;
        try {
            distinction = timeStr2Long(time1, formate1) - timeStr2Long(time2, formate2);
        } catch (ParseException e) {
        }

        return distinction;
    }

    /**
     * 2010-01-21 00:00:00时间格式转为20100121000000
     *
     * @param date 源字符串
     * @return String 目标字符串
     */
    public static String chageDate(String date) {

        date = date.replaceAll("-", "");
        date = date.replaceAll(" ", "");
        date = date.replaceAll(":", "");

        return date;
    }

    /**
     * 算出两个小时差的所有时间点
     *
     * @param end
     * @return
     */
    public static String[] getAllDateByHour(String start, String end) {
        long distinction = DateTools.timeDistinction(end, start);

        int hour = (int) (distinction / 1000 / 3600) + 1;

        String datatimes[] = new String[hour];

        long endtime;

        try {
            endtime = DateTools.timeStr2Long(end, "yyyyMMddHHmmss");

            for (int i = 0; i < hour; i++) {
                datatimes[hour - i - 1] = addDate(endtime, -1 * i, DateTools.HOUR, "yyyyMMddHHmmss");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return datatimes;
    }

    /**
     * 找到相差几天
     *
     * @param time1
     * @param time2
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static int getDistinctionDay(long time1, long time2) {
        long ttt = time2 - time1;

        int tt = 0;

        if (ttt > 0 && ttt <= 86400000) {
            tt = (int) (ttt / 1000 / 3600 / 24) + 1;
        } else {
            tt = (int) (ttt / 1000 / 3600 / 24);
        }
        // s

        return tt;
    }

    /**
     * 算出两个时间的天差的所有时间点
     *
     * @param end
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String[] getAllDateByDay(String start, String end) {
        long distinction = DateTools.timeDistinction(end, start);

        int day = (int) (distinction / 1000 / (3600 * 24)) + 1;

        String datatimes[] = new String[day];

        long endtime;

        try {
            endtime = DateTools.timeStr2Long(end, "yyyyMMddHHmmss");

            for (int i = 0; i < day; i++) {
                datatimes[day - i - 1] = DateTools.addDate(endtime, -1 * i, DateTools.DAY, "yyyy-MM-dd");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return datatimes;
    }

    /**
     * 算出两个时间的天差的所有时间点
     *
     * @param end
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static List<String> getAllDateByMonth(String start, String end) {

        List<String> list = new ArrayList<String>();
        list.add(start);
        try {
            String f = "yyyy年MM月";
            long temp_end_time;
            temp_end_time = DateTools.timeStr2Long(start, f);

            while (true) {

                String temp_end = DateTools.addDate(temp_end_time, 1, DateTools.MONTH, f);
                temp_end_time = DateTools.timeStr2Long(temp_end, f);
                if (temp_end.compareTo(end) == 0) {
                    break;
                }

                list.add(temp_end);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        list.add(end);
        return list;
    }

    /**
     * Date 转换为 LocalDate
     * @param date
     * @since jdk8
     * @return
     */
    public static LocalDate date2LocalDate(Date date){
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.toLocalDate();
    }

    /**
     * 返回x天x时x分x秒x毫秒
     * @param ms
     * @return
     */
    public static String long2HMS(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuffer sb = new StringBuffer();
        if(day > 0) {
            sb.append(day+"天");
        }
        if(hour > 0) {
            sb.append(hour+"小时");
        }
        if(minute > 0) {
            sb.append(minute+"分");
        }
        if(second > 0) {
            sb.append(second+"秒");
        }
        if(milliSecond > 0) {
            sb.append(milliSecond+"毫秒");
        }
        return sb.toString();
    }

    /**
     * 时间格式化
     */
    public class Pattern{
        /**
         * 标准格式：yyyy-MM-dd HH:mm:ss
         */
        public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
        /**
         * 标准格式：yyyyMMddHHmmss
         */
        public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
        /**
         * 指定到毫秒
         * 标准格式：yyyyMMddHHmmssSSS
         */
        public static final String YYYYMMDDHHMMssSSS = "yyyyMMddHHmmssSSS";
        /**
         * 时间标准格式(没有日期)：HH:mm:ss
         */
        public static final String HH_MM_SS = "HH:mm:ss";
        /**
         * 日期标准格式(没有时分秒)：yyyy-MM-dd
         */
        public static final String YYYY_MM_DD = "yyyy-MM-dd";
    }
}
