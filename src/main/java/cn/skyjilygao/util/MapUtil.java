package com.powerwin.adorado.util;

import java.util.HashMap;
import java.util.Map;

/**
 * map工具类
 *
 * @author skyjilygao
 * @since 20231228
 */
public class MapUtil {

    /**
     * 将普通map转换成平铺map：
     * <p> 平铺map：要求如果遍历到n层时，flatMap的key=第1层的key+第2层的key+...+第n层的key（第1到n层的key中间用句点拼接）。flatMap的value=第n层的value
     *
     * @param originalMap 原始map
     * @return flatMap 转换后的平铺map
     */
    public static Map<String, Object> toFlattenMap(Map<String, Object> originalMap) {
        Map<String, Object> flatMap = new HashMap<>();
        toFlattenMap("", originalMap, flatMap);
        return flatMap;
    }

    /**
     * @param currentKey  当前key
     * @param originalMap 原始map
     * @param flatMap     转换后的平铺map
     */
    private static void toFlattenMap(String currentKey, Map<String, Object> originalMap, Map<String, Object> flatMap) {
        for (Map.Entry<String, Object> entry : originalMap.entrySet()) {
            String newKey = currentKey.isEmpty() ? entry.getKey() : currentKey + "." + entry.getKey();
            if (entry.getValue() instanceof Map) {
                toFlattenMap(newKey, (Map<String, Object>) entry.getValue(), flatMap);
            } else {
                flatMap.put(newKey, entry.getValue());
            }
        }
    }
}
