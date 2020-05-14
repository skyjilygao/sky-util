package cn.skyjilygao.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 生成随机数工具类:由当前时间（18位）+ 指定长度（len）的随机数
 * <p>默认随机数（23位：由当前时间（18位）+ 默认长度（5）的随机数）
 * 由纯数字组成
 *
 * @author skyjilygao
 * @since 20190412
 */
public class RandomUtil {

    /**
     * 默认长度
     */
    private final static Integer DEFAULT_LEN = 5;
    public static void main(String[] args) {
        System.out.println(defaultNo());
    }

    /**
     *
     * @return 默认随机数（23位：由当前时间（18位）+ 默认长度（5）的随机数）
     */
    public static String defaultNo(){
        return random("");
    }

    /**
     * 生成订单的随机数:23位：由当前时间（18位）+ 默认长度（5）的随机数
     * @param len 指定随机数的长度
     * @return 携带指定长度的随机数（除去指定长度，其他长度共18位）
     */
    public static String random(int len){
        return random("");
    }

    /**
     *
     * @param prefix 指定前缀
     * @return 携带指定前缀的随机数（除去前缀长度，其他长度共23位）
     */
    public static String random(String prefix){
        return random(prefix, DEFAULT_LEN);
    }

    /**
     *
     * @param prefix 指定前缀
     * @param len 指定随机数的长度
     * @return 携带指定前缀的随机数（除去前缀长度和指定长度，其他长度共18位）
     */
    public static String random(String prefix, int len){
        int r = generate(len);
        StringBuffer buffer = new StringBuffer(prefix);
        buffer.append(nowDateTimeStr()).append(r);
        return buffer.toString();
    }

    /**
     * @return 获取当前时间字符串：YYYYMMDDHHMMSS
     */
    private static String nowDateTimeStr(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateTools.Pattern.YYYYMMDDHHMMssSSS);
        return now.format(dateTimeFormatter);
    }

    /**
     * 根据指定长度生成随机数
     * @param len 长度（位数）
     * @return
     */
    private static int generate(int len){
        double d  = Math.pow(10, (double) len - 1);
        return (int)((Math.random() * 9 + 1) * d);
    }
}
