package com.skyjilygao.util;

import com.alibaba.fastjson.JSONObject;
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

    /**
     * test
     * @param args
     */
    public static void main(String[] args) {
        SplitListUtil splitListUtil = new SplitListUtil<String>();
        List<String> list = new ArrayList<>();
        for (int i = 0;i < 370;i++) {
            list.add("list_"+i);
        }
        List<List<String>> newList2 = splitListUtil.splitList2(list, 50);
        System.out.println(newList2.size());
        System.out.println(JSONObject.toJSONString(newList2));

        List<List<String>> newList = splitListUtil.splitList(list, 50);
        System.out.println(newList.size());
        System.out.println(JSONObject.toJSONString(newList));
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
        List<List<T>> newList = new ArrayList<>();
        List<T> temp = new ArrayList<>();
        int to = 0;
        int f = 0;
        for (int i=0; i< beforeLs; i++) {
            f = i * beforeLsPs;
            to = f + beforeLsPs;
            to = to > ls ? ls : to;
            temp = list.subList(f, to);
            newList.add(temp);
        }
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
