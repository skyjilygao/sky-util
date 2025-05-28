package cn.skyjilygao.util.csv;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;

/**
 * 生成csv文件
 *
 * @author skyjilygao
 * @date 20210917
 */
public class CsvWriteUtil {
    /**
     * 保证有序，必须是 linked hash
     * <br> key: 与 JSONArray中的 jsonobject的key对应
     * <br> value: 显示的名称
     */
    private LinkedHashMap<String, String> headMap;
    private JSONArray content;

    /**
     * 指定表头，和内容
     *
     * @param headMap 必须是有序的 LinkedHashMap
     * @param content 不可为空。数据的 key必须根据 LinkedHashMap的key一致
     */
    public CsvWriteUtil(LinkedHashMap<String, String> headMap, JSONArray content) {
        this.headMap = headMap;
        this.content = content;
    }

    /**
     * @param fileFullPath 不能为空，文件全路径。
     */
    public void generate(String fileFullPath) {
        LinkedHashSet<String> keySet = new LinkedHashSet<>(headMap.keySet());
        ArrayList<String> strings = new ArrayList<>(headMap.values());
        String[] head = new String[strings.size()];
        head = strings.toArray(head);
        File file = new File(fileFullPath);
        file.getParentFile().mkdirs();
        List<List<Object>> allList = new ArrayList<>();
        Iterator<Object> it = content.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (null == o) {
                continue;
            }
            JSONObject json = (JSONObject) o;
            List<Object> list = new ArrayList<>();
            keySet.stream().forEach(key -> {
                String string = json.getString(key);
                string = StringUtils.defaultString(string, "");
                list.add(string);
            });
            allList.add(list);
        }
        createCSV(head, allList, fileFullPath);
    }

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
     *
     * @param row       数据列表
     * @param csvWriter
     * @throws IOException
     */
    private static void writeRow(List<Object> row, BufferedWriter csvWriter) throws IOException {
        for (Object data : row) {
            StringBuffer sb = new StringBuffer();
            String rowStr = sb.append("\"").append(data == null ? "" : data).append("\",").toString();
            csvWriter.write(rowStr);
        }
        csvWriter.newLine();
    }
}
