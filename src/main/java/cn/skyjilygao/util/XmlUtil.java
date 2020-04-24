package cn.skyjilygao.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.*;
import org.dom4j.io.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * xml工具类
 * <p> 将xml文件内容转换成json对象(这里使用的是com.alibaba.fastjson) </p>
 * <p> 直接调用xmlToJson方法即可，参数：xml文件路径或xml文件。转换成功返回json对象 </p>
 * <p> 注意: 如果文件路径错误或文件内容错误，将返回null，并打印日志 </p>
 * @since 20180612
 * @author skyjilygao
 */
@Slf4j
public class XmlUtil {

    /**
     *
     * @param filePath xml文件绝对路径
     * @return Json对象: com.alibaba.fastjson.JSONObject
     * @throws FileNotFoundException 路径错误导致文件不存在
     */
    public static JSONObject xmlToJson(String filePath) {
        return xmlToJson(new File(filePath));
    }

    /**
     *
     * @param file xml文件
     * @return Json对象: com.alibaba.fastjson.JSONObject
     * @throws FileNotFoundException 文件内容错误导致转换错误
     */
    public static JSONObject xmlToJson(File file) {
        log.info("convert start...");
        if (!file.exists()) {
            try {
                throw new FileNotFoundException(file.getAbsolutePath());
            } catch (FileNotFoundException e) {
                log.error(file.getAbsolutePath(), e);
                return null;
            }
        }
        JSONObject json = new JSONObject();
        try {
            SAXReader reader = new SAXReader();
            Document doc = reader.read(file);
            Element root = doc.getRootElement();

            iterateElement(root,json);
        } catch (Exception e) {
            log.error("处理文件错误", e);
            return null;
        }
        log.info("convert end...");
        log.info(json.toJSONString());
        return json;
    }

    /**
     * 开始转换。使用递归，遇到子元素，重复调用
     * @param element 元素
     * @param json 输出json
     */
    private static void iterateElement(Element element, JSONObject json){
        List node = element.elements();
        // attributes
        List<Attribute> atts = element.attributes();
        if (atts != null && atts.size() > 0) {
            for(Attribute att : atts){
                json.put(att.getName(), att.getValue());
            }
        }
        // element
        JSONArray array = new JSONArray();
        String attName = "";
        for(int i=0;i<node.size();i++){
            Element element1 = (Element) node.get(i);
            attName = element1.getName();
            JSONObject attJson = new JSONObject();
            iterateElement(element1, attJson);
            array.add(attJson);
        }
        if (StringUtils.isNotBlank(attName)){
            json.put(attName, array);
        }
        return;
    }
}
