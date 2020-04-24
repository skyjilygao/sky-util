package cn.skyjilygao.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Token工具类，相同源字符串执行结果不同，不使用密码使用
 * @since 20200319
 * @author skyjilygao
 */
public class TokenUtil {

    private final static String STR_DIC ="ABCDE0FG1HI3JK4LM5NO6PQ7RS8TU9VWXYZ";
    private final static int STR_DIC_LEN = STR_DIC.length();
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
            stringBuffer.append(randStr());
            if (temp.length() / 2 == 0) {
                stringBuffer.append(temp);
            }
            stringBuffer.append(randStr());
        }
        return stringBuffer.toString();
    }

    private static String randStr(){
        Random random=new Random();
        int nint = random.nextInt(STR_DIC_LEN);
        String str = ""+STR_DIC.charAt(random.nextInt(STR_DIC_LEN));
        if(nint / 2 == 0){
            return str.toLowerCase();
        }
        return str.toUpperCase();
    }

    public static void main(String[] args) {
        String str = "asdf";
        try {
            System.out.println(SHA512(str));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
