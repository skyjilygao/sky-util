package cn.skyjilygao.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author sky
 * @since 20171127
 */
public class JsonUtil {
    private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    /**
     * read json text from file
     * @param filePath
     * @return
     */
    public static String readJsonFile(String filePath){
        File file = new File(filePath);
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null) {//使用readLine方法，一次读一行
                result.append(System.lineSeparator() + s);
            }
            br.close();
        } catch (Exception e) {
            logger.error("This File Content Is Not JSON Text. " + e.getMessage());
//            e.printStackTrace();
        }
        return result.toString();
    }

    /**
     * 根据key获取json串中的对于value
     * @param source 源json串
     * @param key JsonObject key
     * @return
     */
    public static String readJsonArray(String source, String key) {
        JSONObject object = JSONObject.parseObject(source);
        return object.get(key).toString(); // data
    }
    /**
     * 根据key获取json串中的对于value
     * @param source 源json串
     * @param key JsonObject key
     * @return Object
     */
    public static Object readJsonObject(String source, String key) {
        JSONObject object = JSONObject.parseObject(source);
        return object.get(key); // data
    }

    /**
     * 根据filePaht 和 指定获取的json key
     * @param filePath json file path
     * @param key jsonObject的key.一般情况下：值为data
     * @return
     */
    public static List readJsonFile(String filePath, String key, Class clazz) {
        String json = JsonUtil.readJsonFile(filePath);
        String data = JsonUtil.readJsonArray(json, key);
        return JSONArray.parseArray(data, clazz);
    }

    /**
     * 替换 JSONObject 的key中的点为下划线，如：act.type -> act_type
     * @param json
     * @return
     */
    public static JSONObject replacePointsInKey(JSONObject json) {
        JSONObject newJson = new JSONObject();
        Set<String> set = json.keySet();
        for(String s : set){
            if(s.indexOf(".") > -1){
                newJson.put(s.replaceAll("\\.","_"),json.get(s));
            }else{
                newJson.put(s,json.get(s));
            }
        }
        return newJson;
    }

    /**
     * 替换 JSONArray 的key中的点为下划线，如：act.type -> act_type
     * @param array
     * @return
     */
    public static JSONArray replacePointsInKey(JSONArray array) {
        JSONArray newArray = new JSONArray();
        Iterator it = array.iterator();
        while (it.hasNext()){
            JSONObject o = replacePointsInKey((JSONObject)it.next());
            newArray.add(o);
        }
        return newArray;
    }

    /**
     * 替换 JSONArray字符串 的key中的点为下划线，如：act.type -> act_type
     * @param arrayString
     * @return
     */
    public static JSONArray replacePointsInKey(String arrayString) {
        JSONArray jo = JSONObject.parseArray(arrayString);
        return replacePointsInKey(jo);
    }
}
