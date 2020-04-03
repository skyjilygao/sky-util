package com.skyjilygao.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * txt文件
 * @author skyjilygao
 * @since 20190527
 */
@Slf4j
public class TxtUtil {

    /**
     * 读取txt文件的内容：按行读取，没读取一行，add 到list中
     * @param file 想要读取的文件对象
     * @return list
     */
    public static List<String> txt2ListStr(File file){
        List<String> list = new ArrayList<>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                list.add(s);
            }
            br.close();    
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 读取txt文件的内容：按行读取，没读取一行，add 到list中
     * @param file 想要读取的文件对象
     * @return string
     */
    public static String txt2String(File file){
        StringBuffer sb = new StringBuffer();
        try{
            // 构造一个BufferedReader类来读取文件
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s;
            // 使用readLine方法，一次读一行
            while((s = br.readLine())!=null){
                sb.append(s);
            }
            br.close();
        }catch(Exception e){
            log.error("");
        }
        return sb.toString();
    }


    public static void main(String[] args){
        File file = new File("E:\\!work\\ac_id.log");
        List<String> str = txt2ListStr(file);
        System.out.println(str);
    }

    /**
     * 使用BufferedWriter类写文本文件
     * @param content 文件内容
     * @param filePath 文件父路径（不包含文件名）
     * @param fileName 文件名称
     * @return
     */
    public static boolean writeJsonFile(String content, String filePath, String fileName) {
        try {
            File f = new File(filePath);
            if (!f.exists()) {
                f.mkdirs();
                f.createNewFile();
            }
            fileName = filePath + "/" + fileName;
            f = new File(fileName);
            OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(f), "gbk");
            BufferedWriter out = new BufferedWriter(write);
            String characterSet = "utf8";
            content = new String(content.getBytes(characterSet), characterSet);
            out.write(content);
            out.close();
            return true;
        } catch (IOException e) {
            log.error("写json错误：" + e.getMessage());
            return false;
        }
    }
}