package com.skyjilygao.util;

import java.util.*;

public class MapSortUtil {
    public static void main(String[] args) {

        Map<String, Object> map = new TreeMap<>();

        map.put("2019-01-02", "kfc");
        map.put("2019-01-12", "kfc");
        map.put("2018-12-25", "kfc");

        System.out.println("--------sortMapByKey----------");
        Map<String, Object> resultMap = sortMapByKey(map);    //按Key进行排序
        for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        System.out.println("--------sortMapByValue----------");
        resultMap = sortMapByValue(map);
        for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }

    /**
     * 使用 Map按key进行排序
     *
     * @param map
     * @return
     */
    public static Map<String, Object> sortMapByKey(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, Object> sortMap = new TreeMap<>(new MapKeyComparator());
        sortMap.putAll(map);
        return sortMap;
    }

    /**
     * 使用 Map按value进行排序
     * @param oriMap
     * @return
     */
    public static Map<String, Object> sortMapByValue(Map<String, Object> oriMap) {
        if (oriMap == null || oriMap.isEmpty()) {
            return null;
        }
        Map<String, Object> sortedMap = new LinkedHashMap<>();
        List<Map.Entry<String, Object>> entryList = new ArrayList<>(
                oriMap.entrySet());
        Collections.sort(entryList, new MapValueComparator());
        Iterator<Map.Entry<String, Object>> iter = entryList.iterator();
        Map.Entry<String, Object> tmpEntry = null;
        while (iter.hasNext()) {
            tmpEntry = iter.next();
            sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
        }
        return sortedMap;
    }
}

/**
 * 按 key 比较类
 */
class MapKeyComparator implements Comparator<String> {
    @Override
    public int compare(String str1, String str2) {
        return str1.compareTo(str2);
    }
}

/**
 * 按 value 比较类
 */
class MapValueComparator implements Comparator<Map.Entry<String, Object>> {
    @Override
    public int compare(Map.Entry<String, Object> me1, Map.Entry<String, Object> me2) {
        return me1.getValue().toString().compareTo(me2.getValue().toString());
    }
}