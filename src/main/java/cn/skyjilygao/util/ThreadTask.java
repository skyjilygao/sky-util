package cn.skyjilygao.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public interface ThreadTask {

    /**
     * 定义线程池。
     * <p> 最大线程数量= 核心线程数 * 3 / 2，超出核心线程存活时间为 30，单位seconds</p>
     * <p> maximumPoolSize = 3 * corePoolSize / 2 </p>
     * @param corePoolSize 最大线程数
     * @return
     */
    default ThreadPoolExecutor threadPoolExecutor(int corePoolSize) {
        int maximumPoolSize = 3 * corePoolSize / 2;
        ThreadPoolExecutor exec = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 30,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(30),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        return exec;
    }
}
