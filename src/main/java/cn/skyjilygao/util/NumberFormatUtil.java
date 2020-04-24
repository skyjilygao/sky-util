package cn.skyjilygao.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;

/**
 * 数值格式化，保留小数
 * @author skyjilygao
 * @since 20180516
 */
public class NumberFormatUtil {

    private static final Logger log = LoggerFactory.getLogger(NumberFormatUtil.class);
    /**
     *
     * 默认保留2位小数，非千位符表示
     * @param number 数值型字符串
     * @return
     */
    public static String numberFormat(String number){
        try {
            return numberFormat(Float.parseFloat(number));
        } catch (Exception e) {
            log.warn(number + " is not numerice, will be ignore. msg=" + e.getMessage());
            return number;
        }
    }
    /**
     *
     * 默认保留2位小数
     * @param number 数值型字符串
     * @return
     */
    public static String numberFormat(String number, boolean groupingUsed){
        try {
            return numberFormat(Float.parseFloat(number),groupingUsed);
        } catch (Exception e) {
            log.warn(number + " is not numerice, will be ignore. msg=" + e.getMessage());
            return number;
        }
    }

    /**
     *
     * 默认保留2位小数, 非千位符表示
     * @param number float浮点型数
     * @return
     */
    public static String numberFormat(float number){
        return numberFormat(number,2, false);
    }


    /**
     *
     * 默认保留2位小数, 非千位符表示
     * @param number float浮点型数
     * @param groupingUsed 是否千位符表示：true表示是，false表示否
     * @return
     */
    public static String numberFormat(float number, boolean groupingUsed){
        return numberFormat(number,2, groupingUsed);
    }

    /**
     *
     * 默认保留2位小数, 非千位符表示
     * @param number double浮点型数
     * @return
     */
    public static String numberFormat(double number){
        return numberFormat(number,2, false);
    }
    /**
     *
     * 默认保留2位小数, 非千位符表示
     * @param number double浮点型数
     * @param groupingUsed 是否千位符表示：true表示是，false表示否
     * @return
     */
    public static String numberFormat(double number, boolean groupingUsed){
        return numberFormat(number,2, groupingUsed);
    }
    /**
     *
     * @param number 浮点型数
     * @param num 保留小数位数
     * @param groupingUsed 是否千位符表示：true表示是，false表示否
     * @return
     */
    public static String numberFormat(double number, int num, boolean groupingUsed){
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(num);
        nf.setGroupingUsed(groupingUsed);
        return nf.format(number);
    }

    /**
     * 判断是否为数值，或浮点数
     * @param str
     * @return
     */
    public static boolean isNumber(String str){
        String reg = "^[0-9]+(.[0-9]+)?$";
        return str.matches(reg);
    }
}
