package cn.skyjilygao.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * apache.commons.lang3.StringUtils;的扩展。
 * <br> 1. 增加判断字符串是否是数值或浮点类型
 *
 * @author skyjilygao
 * @since 20190610
 */
public class StringUtilsExt extends StringUtils {

    public static void main(String[] args) {
        String id = "23843410799950184";
        BigDecimal d = new BigDecimal(id);
        System.out.println(d.toBigInteger());
        System.out.println(d.toString());
        int intv = d.intValue();
        System.out.println(intv);
        System.out.println(String.valueOf(intv).equals(id));


    }
    /**
     * 判断字符串是否是数值或浮点型字符串
     * @param str
     * @return 数值或浮点型返回true，否则返回false
     */
    public static boolean isNumber(String str){
//        String reg = "^[0-9]+(.[0-9]+)?$"; 对于"1-4"这种字符串无法识别，结果为true。是错误的
        String reg = "^(-?\\d+)(\\.\\d+)?$";
        return str.matches(reg);
    }
}
