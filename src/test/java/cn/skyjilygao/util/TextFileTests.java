package cn.skyjilygao.util;

public class TextFileTests {
    public static void main(String[] args){
        String filename="E:\\ideaWorkSpace\\logs\\fbads-crm-api\\error_log.log";
        //使用增强for循环进行文件的读取
        System.out.println("开始read==================================");
        for(String line:new TextFile(filename, 1100)){
            System.out.println("str="+line);
        }
    }
}
