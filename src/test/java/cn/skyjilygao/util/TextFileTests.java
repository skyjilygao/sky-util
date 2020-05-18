package cn.skyjilygao.util;

public class TextFileTests {
    public static void main(String[] args){
        String filename="E:\\ideaWorkSpace\\logs\\collection-insights-jobb\\run_log.log";
        //使用增强for循环进行文件的读取
        System.out.println("开始read==================================");
        int a = 0;
        for(String line:new TextFile(filename, 2)){
            System.out.println("line num: "+(++a)+", str="+line);
        }

    }
}
