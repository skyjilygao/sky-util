package com.skyjilygao.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证工具类
 * @author skyjilygao
 * @since 2018/2/24
 */
public class ValidationUtils {
    /**
     * 登录校验，用户名密码是否为空
     * @param account
     * @param pwd
     * @return 都不为空返回true，任意否则返回false
     */
    public static Boolean login(String account, String pwd, Boolean isNewLogin) {
        if(StringUtils.isAnyBlank(account,pwd) || isNewLogin == null){
            return false;
        }
        return true;
    }


    /**
     * 验证邮箱地址
     * @param email
     * @return 正确返回true，错误返回false
     */
    public static boolean email(String email){
//        String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        String check = "^[a-z0-90-9]+([._\\\\-]*[a-z0-90-9])*@([a-z0-90-9]+[-a-z0-90-9]*[a-z0-90-9]+.){1,63}[a-z0-90-9]+$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(email);
        return matcher.matches();
    }


    /**
     *
     * @param email
     * @return
     */
    public static boolean isNotEmail(String email){
        return !email(email);
    }
}
