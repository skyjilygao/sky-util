package cn.skyjilygao.util;


import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * 创建CSV文件
 */
public class CSVUtil {

    /**
     * 创建CSV文件
     */
    public static void createCSV(Object[] head, List<List<Object>> dataList, String fileName) {
        
        List<Object> headList = Arrays.asList(head);
        
        File csvFile = null;
        BufferedWriter csvWtriter = null;
        try {
            csvFile = new File(fileName);
            File parent = csvFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            csvFile.createNewFile();

            // GB2312使正确读取分隔符","
            csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "GB2312"), 1024);
            int num = headList.size() / 2;
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < num; i++) {
                buffer.append(" ,");
            }
            // 写入文件头部
            writeRow(headList, csvWtriter);
            // 写入文件内容
            for (List<Object> row : dataList) {
                writeRow(row, csvWtriter);
            }
            csvWtriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                csvWtriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 写一行数据
     * @param row 数据列表
     * @param csvWriter
     * @throws IOException
     */
    public static void writeRow(List<Object> row, BufferedWriter csvWriter) throws IOException {
        for (Object data : row) {
            StringBuffer sb = new StringBuffer();
            String rowStr = sb.append("\"").append(data == null ? "" : data).append("\",").toString();
            csvWriter.write(rowStr);
        }
        csvWriter.newLine();
    }
}