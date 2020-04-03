package com.skyjilygao.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 对字符串加密
 * @since 20190409
 * @author skyjilygao
 */
public class EncryptionUtil {

    /**
     * SHA-512加密
     * @param originStr 原字符串
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String SHA512(String originStr) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return encryption(originStr, "SHA-512");
    }

    /**
     * SHA-256加密
     * @param originStr 原字符串
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String SHA256(String originStr) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return encryption(originStr, "SHA-256");
    }

    /**
     * MD5加密
     * @param originStr 原字符串
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String MD5(String originStr) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return encryption(originStr, "MD5");
    }

    /**
     *
     * @param originStr 原字符串
     * @return 加密后字符串
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    private static String encryption(String originStr, String algorithm) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest;
        messageDigest = MessageDigest.getInstance(algorithm);
        messageDigest.update(originStr.getBytes("UTF-8"));
        byte[] bytes = messageDigest.digest();
        return byte2Hex(bytes);
    }

    /**
     * 转string
     * @param bytes
     * @return
     */
    private static String byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
}
