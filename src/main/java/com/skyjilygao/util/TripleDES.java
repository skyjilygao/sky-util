package com.skyjilygao.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.Security;

/**
 * TripleDES 加密与解密
 * <p>
 * 模式：ECB 填充：无填充
 * <p>
 * key 为24位，msg 必须为8的倍数（无填充的必须是8的倍数，其他填充模式 无此要求）
 * <p>
 * 3DES实现： 主要有CBC,ECB实现，java默认是ECB
 * <p>
 * 对于待加密解密的数据的填充方式：NoPadding、PKCS5Padding、SSL3Padding，默认填充方式为，PKCS5Padding
 * <p>
 * java中要求key的size必须为24；对于CBC模式下的向量iv的size两者均要求必须为8, 所以在处理8字节的key的时候，直接使用DES三次，
 * 加密时候为（加密－－解密－－加密），解密时候为：（解密－－加密－－解密）
 *
 * @author skyjilygao
 */
@Slf4j
public class TripleDES {

    private static Logger logger = log;

    /**
     * 加密算法
     */
    private static String algorithm = "TripleDES/ECB/NoPadding";

    public static String key = "123456781234567812345678";

    private static String des = "TripleDES";

    static {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
    }

    /**
     * 3DES加密方法
     *
     * @param msg 加密对象
     * @param key 加密key
     * @return 加密后的对象
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public static byte[] encrypt(byte[] msg, byte[] key)
            throws Exception {
        byte[] input = msg;
        // 如果加密的字节流不是8的倍数需要补0处理
        if (input.length % 8 != 0) {
            ByteBuffer buffer = ByteBuffer.allocate(input.length + (8 - input.length % 8));
            buffer = buffer.put(input);
            input = buffer.array();
        }
        byte[] keyBytes = key;
        SecretKeySpec k = new SecretKeySpec(keyBytes, des);
        Cipher cipher = Cipher.getInstance(algorithm); // DES/ECB/NOPADDING
        cipher.init(Cipher.ENCRYPT_MODE, k);
        byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
        int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
        ctLength += cipher.doFinal(cipherText, ctLength);
        return cipherText;

    }

    /**
     * 3des 解密
     *
     * @param msg 加密内容
     * @param key 加密key
     * @return
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public static byte[] decrypt(byte[] msg, byte[] key)
            throws Exception {
        byte[] input = msg;
        byte[] keyBytes = key;
        SecretKeySpec k = new SecretKeySpec(keyBytes, des);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, k);
        byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
        int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
        ctLength += cipher.doFinal(cipherText, ctLength);
        return cipherText;
    }

    /**
     * 将byte数组转换为表示16进制值的字符串， 如：byte[]{8,18}转换为：0813， 和public static byte[]
     * hexStr2ByteArr(String strIn) 互为可逆的转换过程
     *
     * @param arrB 需要转换的byte数组
     * @return 转换后的字符串
     * @throws Exception 本方法不处理任何异常，所有异常全部抛出
     */
    public static String byteArr2HexStr(byte[] arrB)
            throws Exception {
        int iLen = arrB.length;
        // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
        StringBuffer sb = new StringBuffer(iLen * 2);
        for (int i = 0; i < iLen; i++) {
            int intTmp = arrB[i];
            // 把负数转换为正数
            while (intTmp < 0) {
                intTmp = intTmp + 256;
            }
            // 小于0F的数需要在前面补0
            if (intTmp < 16) {
                sb.append("0");
            }
            sb.append(Integer.toString(intTmp, 16));
        }
        return sb.toString();
    }

    /**
     * 将表示16进制值的字符串转换为byte数组， 和public static String byteArr2HexStr(byte[] arrB)
     * 互为可逆的转换过程
     *
     * @param strIn 需要转换的字符串
     * @return 转换后的byte数组
     * @throws Exception 本方法不处理任何异常，所有异常全部抛出
     * @author <a href="mailto:zhangji@aspire-tech.com">ZhangJi</a>
     */
    public static byte[] hexStr2ByteArr(String strIn)
            throws Exception {
        byte[] arrB = strIn.getBytes();
        int iLen = arrB.length;

        // 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i = i + 2) {
            String strTmp = new String(arrB, i, 2);
            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
        }
        return arrOut;
    }

    /**
     * 加密String明文输入,String密文输出
     *
     * @param strMing
     * @return
     */
    public static String getEncString(String strMing) {
        byte[] t;
        try {
            t = encrypt(strMing.getBytes("UTF-8"), key.getBytes());
            String des = byteArr2HexStr(t);
            return des;
        } catch (Exception e) {
            logger.error("getEncString Exception", e);
        }
        return null;
    }

    /**
     * test
     * @param args
     * @throws Exception
     */
    public static void main(String[] args)
            throws Exception {
        String content = "adminadmin!@#456";
        System.out.println("明文" + content);
        String des = getEncString(content);
        System.out.println("密文" + des);
        byte ss[] = hexStr2ByteArr(des);
        String con2 = new String(decrypt(ss, key.getBytes()));
        System.out.println("解密" + con2);
    }
}