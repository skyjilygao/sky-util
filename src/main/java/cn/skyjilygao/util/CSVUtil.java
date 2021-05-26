package cn.skyjilygao.util;


import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 创建CSV文件
 */
@Slf4j
public class CSVUtil {
    /**
     * 逗号切分csv内容，但排除双引号内的逗号
     */
    public static final String SPLIT_CHARACTER = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

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
            if(null != csvWtriter){
                try {
                    csvWtriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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


    /**
     * 读csv文件内容
     * @param file
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> List<T> read(File file, Class<T> cls){
        if(!file.getName().endsWith(".csv")){
            throw new RuntimeException(String.format("文件格式错误，不是csv文件。file:%s", file.getName()));
        }
        List<String> list = TxtUtil.txt2ListStr(file);
        log.info("read file:{}, read {} line. ", file.getAbsolutePath(), list.size());
        return list.subList(1, list.size()).stream().map(s -> {
            // csv文件以逗号分割。切分时排除双引号内的逗号
            String[] fields = s.split(SPLIT_CHARACTER);
            T dto = parse(fields, cls);
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     *
     * @param strs 当前行内容
     * @param cls
     * @param <T>
     * @return
     */
    private static <T> T parse(String[] strs, Class<T> cls) {
        Field[] fields = cls.getDeclaredFields();
        Map<Integer, Field> fieldMap = new HashMap<>();
        for (Field field : fields) {
            CsvReadColumn annotation = field.getAnnotation(CsvReadColumn.class);
            if (annotation != null) {
                int index = annotation.index();
                field.setAccessible(true);
                // 如果index相同，则以最后一个为准
                fieldMap.put(index, field);
            }
        }
        T t = null;
        try {
            t = cls.newInstance();
            for (int i = 0; i < strs.length; i++) {
                parse(i, strs[i], t, fieldMap);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     *
     * @param i column index
     * @param v column value
     * @param t class T
     * @param fieldMap class field
     * @param <T>
     */
    private static <T> void parse(int i, String v, T t, Map<Integer, Field> fieldMap) {
        try {
            Field field = fieldMap.get(i + 1);
            if(field != null){
                v = v.replaceAll("\"", "");
                field.set(t, v);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface CsvReadColumn {
        int index() default 0;
        String name() default "";
    }
}