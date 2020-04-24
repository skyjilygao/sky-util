package cn.skyjilygao.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据允许最大线程数量最优切分list
 * 除数：Divisor
 * 被除数：dividend
 * 商：quotient
 * 余数：remainder
 * @param <T>
 */
public class SplitListUtil<T> {
    private static final Logger log = LoggerFactory.getLogger(SplitListUtil.class);

    public static void main(String[] args) {
        SplitListUtil splitListUtil = new SplitListUtil<String>();
        List<String> list = new ArrayList<>();
        for (int i = 0;i < 7;i++) {
            list.add("list_"+i);
        }
        System.out.println("总数据大小="+list.size());
        List<List<String>> newList2 = splitListUtil.splitListByPerMaxSize(list, 3);
        System.out.println(newList2.size());
        System.out.println(JSONObject.toJSONString(newList2));

        /*newList2 = splitListUtil.splitList2(list, 3);
        System.out.println(newList2.size());
        System.out.println(JSONObject.toJSONString(newList2));*/
    }

    /**
     * 特点：子list大小固定，newList大小不固定。
     * 每个子list大小固定为maxSizePerlist。
     * 将一个大list按照最大线程大小切割，合理分配每个子list大小。保证最后包含子list的大list的size不大于maxThreadSize
     * @param list
     * @param maxSizePerlist 切分后每个子list固定大小
     * @return
     */
    public List<List<T>> splitListByPerMaxSize(List<T> list, int maxSizePerlist) {
        if(CollectionUtils.isEmpty(list) || maxSizePerlist <= 0){
            return null;
        }
        int mSize = maxSizePerlist;
        List<List<T>> newList = new ArrayList<>();
        List<T> childList = new ArrayList<>();
        for (T t : list) {
            childList.add(t);
            if(childList.size() == mSize){
                newList.add(childList);
                childList = new ArrayList<>();
            }
        }
        if(childList.size() > 0 ){
            newList.add(childList);
        }
        return newList;
    }

    /**
     * 特点：newList大小固定，子List大小不固定。
     * 截取list
     * @param max 循环最大数
     * @param increment 增量/步长，每次循环，计算to的值
     * @param list 原始list
     * @return newList，截取后新的子list素组
     */
    private List<List<T>> subList(int max, int increment, List<T> list){
        int ls = list.size();
        List<T> temp;
        int to,f;
        List<List<T>> newList = new ArrayList<>();
        for (int i = 0; i< max; i++) {
            f = i * increment;
            to = f + increment;
            to = to > ls ? ls : to;
            temp = list.subList(f, to);
            newList.add(temp);
        }
        return newList;
    }
    /**
     * 将一个大list按照最大线程大小切割，合理分配每个子list大小。保证最后包含子list的大list的size不大于maxThreadSize
     * @param list
     * @param maxThreadSize
     * @return
     */
    public List<List<T>> splitList2(List<T> list, int maxThreadSize) {
        int ls = list.size();
        int mSize = maxThreadSize;
        int beforeLs;
        int beforeLsPs;
        int afterLs;
        int afterLsPs;

        if (ls > mSize) {
            int rmd = ls % mSize;
            int qt = ls / mSize;
            beforeLs = mSize - rmd;
            beforeLsPs = qt;
            afterLs = rmd;
            afterLsPs = ++qt;
        } else {
            beforeLs = ls;
            beforeLsPs = 1;
            afterLs = 0;
            afterLsPs = 0;
        }
        log.info("前半段list的大小：beforeLs="+beforeLs);
        log.info("前半段list每个子list大小：beforeLsPs="+beforeLsPs);
        log.info("后半段list的大小：afterLs="+afterLs);
        log.info("后半段list每个子list的大小：afterLsPs="+afterLsPs);
        List<List<T>> newList;
        List<T> temp;
        int to,f;
        /*for (int i=0; i< beforeLs; i++) {
            f = i * beforeLsPs;
            to = f + beforeLsPs;
            to = to > ls ? ls : to;
            temp = list.subList(f, to);
            newList.add(temp);
        }*/
        newList = subList(beforeLs, beforeLsPs, list);
        int afterFromIndex = beforeLsPs * beforeLs;
        for (int i=0; i< afterLs; i++) {
            f = i * afterLsPs + afterFromIndex;
            to = f + afterLsPs;
            to = to > ls ? ls : to;
            temp = list.subList(f, to);
            newList.add(temp);
        }

        return newList;
    }
    /**
     * 根据最大线程数，分割 list
     * <p> 分割后的 newList 大小不能大于最大线程数 maximumPoolSize
     *
     * @param list
     * @return 分割后的 newList
     * @deprecated 切分后大小可能大于maxThreadSize，并且分配不均.建议使用新的方法splitList2(...)
     */
    @Deprecated
    public List<List<T>> splitList(List<T> list, int maxThreadSize) {

        int size = list.size();
        // 切分开始位置。默认0
        int index = 0;
        // 每次增量，即步长。默认3
        int add = 3;
        if (size / 2 - maxThreadSize < 0) {
            log.info("计算方式1，线程数小于总处理数量一半(" + size + "/2 = " + size / 2 + ")" + maxThreadSize);
            index = maxThreadSize * 2 - list.size();
            index = index < 0 ? 0 : index;
            add = list.size() / maxThreadSize + (index == 0 ? 0 : 1);
        } else {
            log.info("计算方式2，线程数(" + maxThreadSize + ")大于总处理数量一半(" + size + "/2 = " + size / 2 + ")" + maxThreadSize);
            index = size % maxThreadSize;
            add = size / maxThreadSize + (index == 0 ? 0 : 1);
        }
        List<List<T>> newList = new ArrayList<>();
        int to = 1;
        boolean b = true;
        for (int i = 0; i < list.size(); ) {
            if (i >= index) {
                to = i + add;
            } else {
                if (index <= 1) {
                    to = i + add;
                }
            }
            if (to >= list.size()) {
                to = list.size();
            }
            List<T> n = list.subList(i, to);
            newList.add(n);
            if (index <= 1) {
                b = false;
            }
            if (i < index - 1) {
                i++;
                to = i + 1;
                b = true;
            } else {
                if (b) {
                    i++;
                    b = false;
                } else {
                    i += add;
                }
            }
        }
        log.info("切分后newList大小是否小于等于最大线程数=" + (newList.size() <= maxThreadSize));
        log.info("切分后newList大小=" + newList + ", newList=" + newList.toString());
        return newList;
    }
}
