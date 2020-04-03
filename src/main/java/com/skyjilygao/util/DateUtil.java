package com.skyjilygao.util;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
@Slf4j
public class DateUtil {
    public static final String YYYYMMDD = "yyyy-MM-dd";
    /**
     * 一年中有31天的月份
     */
    public final static List<Integer> daysOfMonth31 = new ArrayList<>();
    /**
     * 一年中有30天的月份
     */
    public final static List<Integer> daysOfMonth30 = new ArrayList<>();

    static {
        daysOfMonth31.add(1);
        daysOfMonth31.add(3);
        daysOfMonth31.add(5);
        daysOfMonth31.add(7);
        daysOfMonth31.add(8);
        daysOfMonth31.add(10);
        daysOfMonth31.add(12);
        daysOfMonth30.add(4);
        daysOfMonth30.add(6);
        daysOfMonth30.add(9);
        daysOfMonth30.add(11);
    }

    public static void main(String[] args) {

        LocalDate date = LocalDate.now();
        System.out.println(date.getDayOfMonth());

        /*System.out.println("当天24点时间：" + getTimesnight().toLocaleString());
        System.out.println("当前时间：" + new Date().toLocaleString());
        System.out.println("当天0点时间：" + getTimesmorning().toLocaleString());
        System.out.println("昨天0点时间：" + getYesterdaymorning().toLocaleString());
        System.out.println("近7天时间：" + getWeekFromNow().toLocaleString());
        System.out.println("本周周一0点时间：" + getTimesWeekmorning().toLocaleString());
        System.out.println("本周周日24点时间：" + getTimesWeeknight().toLocaleString());
        System.out.println("本月初0点时间：" + getTimesMonthmorning().toLocaleString());
        System.out.println("本月未24点时间：" + getTimesMonthnight().toLocaleString());
        System.out.println("上月初0点时间：" + getLastMonthStartMorning().toLocaleString());
        System.out.println("本季度结束点时间：" + getCurrentQuarterEndTime().toLocaleString());
        System.out.println("本年开始点时间：" + getCurrentYearStartTime().toLocaleString());
        System.out.println("本年结束点时间：" + getCurrentYearEndTime().toLocaleString());
        System.out.println("上年开始点时间：" + getLastYearStartTime().toLocaleString());*/
//        System.out.println("周几：" + dayOfWeek() + "," + dayOfWeek(dayOfWeek()));
        System.out.println("本季度开始点时间：" + getCurrentQuarterStartTime());
    }

    /**
     * @return 返回数值类型周几。
     */
    public static int dayOfWeek() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * 根据传入的数值类型周几，返回英文。
     * DayOfWeek
     *
     * @param day 周几，如1，就是周一；2就是周二
     * @return 枚举类DayOfWeek的值
     */
    public static String dayOfWeek(int day) {
        return DayOfWeek.of(day).name();
    }

    // 获得当天0点时间
    public static Date getTimesmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();


    }

    // 获得昨天0点时间
    public static Date getYesterdaymorning() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getTimesmorning().getTime() - 3600 * 24 * 1000);
        return cal.getTime();
    }

    // 获得当天近7天时间
    public static Date getWeekFromNow() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getTimesmorning().getTime() - 3600 * 24 * 1000 * 7);
        return cal.getTime();
    }

    // 获得当天24点时间
    public static Date getTimesnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    // 获得本周一0点时间
    public static Date getTimesWeekmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }

    // 获得本周日24点时间
    public static Date getTimesWeeknight() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getTimesWeekmorning());
        cal.add(Calendar.DAY_OF_WEEK, 7);
        return cal.getTime();
    }

    // 获得本月第一天0点时间
    public static Date getTimesMonthmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    // 获得本月最后一天24点时间
    public static Date getTimesMonthnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 24);
        return cal.getTime();
    }

    public static Date getLastMonthStartMorning() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getTimesMonthmorning());
        cal.add(Calendar.MONTH, -1);
        return cal.getTime();
    }

    public static Date getCurrentQuarterStartTime() {
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;
        SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
        Date now = null;
        try {
            if (currentMonth >= 1 && currentMonth <= 3) {
                c.set(Calendar.MONTH, 0);
            } else if (currentMonth >= 4 && currentMonth <= 6) {
                c.set(Calendar.MONTH, 3);
            } else if (currentMonth >= 7 && currentMonth <= 9) {
                c.set(Calendar.MONTH, 6);
            } else if (currentMonth >= 10 && currentMonth <= 12) {
                c.set(Calendar.MONTH, 9);
            }
            c.set(Calendar.DATE, 1);
            now = longSdf.parse(shortSdf.format(c.getTime()) + " 00:00:00");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     * 当前季度的结束时间，即2012-03-31 23:59:59
     *
     * @return
     */
    public static Date getCurrentQuarterEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getCurrentQuarterStartTime());
        cal.add(Calendar.MONTH, 3);
        return cal.getTime();
    }


    public static Date getCurrentYearStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.YEAR));
        return cal.getTime();
    }

    public static Date getCurrentYearEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getCurrentYearStartTime());
        cal.add(Calendar.YEAR, 1);
        return cal.getTime();
    }

    public static Date getLastYearStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getCurrentYearStartTime());
        cal.add(Calendar.YEAR, -1);
        return cal.getTime();
    }

    /**
     * 功能：得到当前月份月底 格式为：yyyy-mm-dd (eg: 2007-12-31)<br>
     *
     * @return String
     */
    public String thisMonthEnd() {
        // 日期属性：年,月,日
        int y, m, d;
        // 当前日期
        Calendar localTime = Calendar.getInstance();
        String strY = null;
        String strZ = null;
        boolean leap = false;
        y = localTime.get(Calendar.YEAR);
        m = localTime.get(Calendar.MONTH) + 1;
        if (daysOfMonth31.contains(m)) {
            strZ = "31";
        }
        if (daysOfMonth30.contains(m)) {
            strZ = "30";
        }
        if (m == 2) {
            leap = leapYear(y);
            if (leap) {
                strZ = "29";
            } else {
                strZ = "28";
            }
        }
        strY = m >= 10 ? String.valueOf(m) : ("0" + m);
        return y + "-" + strY + "-" + strZ;
    }

    /**
     * 功能：得到当前月份月底 格式为：yyyy-mm-dd (eg: 2007-12-31)<br>
     *
     * @return String
     */
    public static LocalDate getMonthEnd() {
        return getMonthEnd(LocalDate.now());
    }
    /**
     * 功能：得到当前月份月底 格式为：yyyy-mm-dd (eg: 2007-12-31)<br>
     *
     * @return String
     */
    public static LocalDate getMonthEnd(LocalDate date) {
        // 日期属性：年,月,日
        int y, m, d;
        // 当前日期
        String strY = null;
        String strZ = null;
        boolean leap = false;
        y = date.getYear();
        m = date.getMonthValue();
        if (daysOfMonth31.contains(m)) {
            strZ = "31";
        }
        if (daysOfMonth30.contains(m)) {
            strZ = "30";
        }
        if (m == 2) {
            leap = leapYear(y);
            if (leap) {
                strZ = "29";
            } else {
                strZ = "28";
            }
        }
        strY = m >= 10 ? String.valueOf(m) : ("0" + m);
        String monthEnd =  y + "-" + strY + "-" + strZ;
        return LocalDate.parse(monthEnd);
    }

    /**
     * 功能：判断输入年份是否为闰年<br>
     *
     * @param year
     * @return 是：true  否：false
     * @author pure
     */
    public static boolean leapYear(int year) {
        boolean leap;
        if (year % 4 == 0) {
            if (year % 100 == 0) {
                if (year % 400 == 0) {
                    leap = true;
                } else {
                    leap = false;
                }
            } else {
                leap = true;
            }
        } else {
            leap = false;
        }
        return leap;
    }
    /**
     * 取得月已经过的天数
     *
     * @param date
     * @return
     */
    public static int getPassDayOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH);
    }
    /**
     * 获取上月日期
     */
    public static Date getStatetime(int day) throws Exception{

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar c = Calendar.getInstance();

        c.add(Calendar.DATE, - day);

        Date monday = c.getTime();
        return monday;

    }
    /**
     * format date
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        return formatDate(date, null);
    }
    /**
     * format date
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String formatDate(Date date, String pattern) {
        String strDate = null;
        try {
            if (pattern == null) {
                pattern = YYYYMMDD;
            }
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            strDate = format.format(date);
        } catch (Exception e) {
            log.error("formatDate error:", e);
        }
        return strDate;
    }

    /**
     * 获取上个月第一天
     */
    public static String getLastFirstDay(Date date) throws Exception {
        int day = getPassDayOfMonth(date)+1;
        Date date1 = getStatetime(day);
        return formatDate(getFirstDateOfMonth(date1));
    }
    /**
     * 获取上个月最后一天日期
     */
    public static String getLastDay(Date date) throws Exception {
        int day = getPassDayOfMonth(date)+1;
        Date date1 = getStatetime(day);
        return formatDate(getLastDateOfSeason(date1));
    }
    /**
     * 取得季度最后一天
     *
     * @param date
     * @return
     */
    public static Date getLastDateOfSeason(Date date) {
        return getLastDateOfMonth(getSeasonDate(date)[2]);
    }
    /**
     * 取得月第一天
     *
     * @param date
     * @return
     */
    public static Date getFirstDateOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
        return c.getTime();
    }
    /**
     * 取得月最后一天
     *
     * @param date
     * @return
     */
    public static Date getLastDateOfMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        return c.getTime();
    }
    /**
     * 取得季度月
     *
     * @param date
     * @return
     */
    public static Date[] getSeasonDate(Date date) {
        Date[] season = new Date[3];

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        int nSeason = getSeason(date);
        if (nSeason == 1) {// 第一季度
            c.set(Calendar.MONTH, Calendar.JANUARY);
            season[0] = c.getTime();
            c.set(Calendar.MONTH, Calendar.FEBRUARY);
            season[1] = c.getTime();
            c.set(Calendar.MONTH, Calendar.MARCH);
            season[2] = c.getTime();
        } else if (nSeason == 2) {// 第二季度
            c.set(Calendar.MONTH, Calendar.APRIL);
            season[0] = c.getTime();
            c.set(Calendar.MONTH, Calendar.MAY);
            season[1] = c.getTime();
            c.set(Calendar.MONTH, Calendar.JUNE);
            season[2] = c.getTime();
        } else if (nSeason == 3) {// 第三季度
            c.set(Calendar.MONTH, Calendar.JULY);
            season[0] = c.getTime();
            c.set(Calendar.MONTH, Calendar.AUGUST);
            season[1] = c.getTime();
            c.set(Calendar.MONTH, Calendar.SEPTEMBER);
            season[2] = c.getTime();
        } else if (nSeason == 4) {// 第四季度
            c.set(Calendar.MONTH, Calendar.OCTOBER);
            season[0] = c.getTime();
            c.set(Calendar.MONTH, Calendar.NOVEMBER);
            season[1] = c.getTime();
            c.set(Calendar.MONTH, Calendar.DECEMBER);
            season[2] = c.getTime();
        }
        return season;
    }
    /**
     *
     * 1 第一季度 2 第二季度 3 第三季度 4 第四季度
     *
     * @param date
     * @return
     */
    public static int getSeason(Date date) {

        int season = 0;

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        switch (month) {
            case Calendar.JANUARY:
            case Calendar.FEBRUARY:
            case Calendar.MARCH:
                season = 1;
                break;
            case Calendar.APRIL:
            case Calendar.MAY:
            case Calendar.JUNE:
                season = 2;
                break;
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.SEPTEMBER:
                season = 3;
                break;
            case Calendar.OCTOBER:
            case Calendar.NOVEMBER:
            case Calendar.DECEMBER:
                season = 4;
                break;
            default:
                break;
        }
        return season;
    }
    /**
     * 获取前三十天的日期字符串，以集合的形式返回
     * @return
     */
    public static List<String> getLast30Day() {
        List<String> timeList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        Calendar theCa = Calendar.getInstance();
        int i = 30;
        while (i >0){
            theCa.setTime(today);
            theCa.add(theCa.DATE, -(i));
            Date start = theCa.getTime();
            String startDate = sdf.format(start);
            timeList.add(startDate);
            i--;
        }
        log.info("一共"+timeList.size()+"天");
        return timeList;
    }
    /**
     * 获取前三天的日期字符串，以集合的形式返回
     * @return
     */
    public static List<String> getLast3Day() {
        List<String> timeList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        Calendar theCa = Calendar.getInstance();
        int i = 3;
        while (i >0){
            theCa.setTime(today);
            theCa.add(theCa.DATE, -(i));
            Date start = theCa.getTime();
            String startDate = sdf.format(start);
            timeList.add(startDate);
            i--;
        }
        log.info("一共"+timeList.size()+"天");
        return timeList;
    }
    /**
     * 获取前七天的日期字符串，以集合的形式返回
     * @return
     */
    public static List<String> getLast7Day() {
        List<String> timeList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        Calendar theCa = Calendar.getInstance();
        int i = 7;
        while (i >0){
            theCa.setTime(today);
            theCa.add(theCa.DATE, -(i));
            Date start = theCa.getTime();
            String startDate = sdf.format(start);
            timeList.add(startDate);
            i--;
        }
        log.info("一共"+timeList.size()+"天");
        return timeList;
    }


    /**
     * 获取昨天的日期字符串，以集合的形式返回
     * @return
     */
    public static List<String> getYesterday() {
        List<String> timeList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        Calendar theCa = Calendar.getInstance();
        int i = 1;
        while (i >0){
            theCa.setTime(today);
            theCa.add(theCa.DATE, -(i));
            Date start = theCa.getTime();
            String startDate = sdf.format(start);
            timeList.add(startDate);
            i--;
        }
        log.info("一共"+timeList.size()+"天");
        return timeList;
    }

    /**
     * 获取上个月的日期
     * @return
     */
    public static List<String> getLastMonth() {
        List<String> timeList = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //获取上个月的最后一天
        Calendar cale = Calendar.getInstance();
        cale.set(Calendar.DAY_OF_MONTH,0);//设置为0,既为月的最后一天
        String lastDay = format.format(cale.getTime());
        log.info("上个月最后一天:"+lastDay);
        Integer lnum = Integer.parseInt(lastDay.split("-")[2]);
        log.info(lnum+"");
        String prefix = lastDay.substring(0, 8);
        log.info("前缀："+prefix);
        for(int i=1;i<=lnum;i++){
            String timestr = "";
            if(i<10){
                String suffix = "0"+i;
                timestr = prefix +suffix;
            }else{
                String suffix = ""+i;
                timestr = prefix +suffix;
            }
            timeList.add(timestr);
        }
        return timeList;
    }


    /**
     * 获取截止到当天的，这个月的日期
     * @return
     */
    public static List<String> getThisMonth() {
        List<String> timeList = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        String currentDay = format.format(c.getTime());
        Integer num = Integer.parseInt(currentDay.split("-")[2]);
        String timestr = "";
        String suffix = currentDay.substring(0, 8);
        for(int i=1;i<=num;i++){
            if(i<10){
                String prefix = "0"+i;
                timestr = suffix + prefix;
            }else{
                String prefix = ""+i;
                timestr = suffix + prefix;
            }
            timeList.add(timestr);
        }
        return timeList;
    }
    /**
     * 获取当前月总天数
     */
    public static Integer getThisMonthSum(String runDate) throws ParseException {
        SimpleDateFormat sm = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date(sm.parse(runDate).getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
}