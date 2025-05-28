package cn.skyjilygao.util;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public interface ThreadTask {

    Logger logger = LoggerFactory.getLogger(ThreadTask.class);

    /**
     * 定义线程池。
     * <p> 最大线程数量= 核心线程数 * 3 / 2，超出核心线程存活时间为 30，单位seconds</p>
     * <p> maximumPoolSize = 3 * corePoolSize / 2 </p>
     *
     * @param corePoolSize 最大线程数
     * @return
     */
    default ThreadPoolExecutor threadPoolExecutor(int corePoolSize) {
        int maximumPoolSize = 3 * corePoolSize / 2;
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 30,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(30),
                new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    /**
     * 定义线程池。
     * <p> 最大线程数量= 核心线程数 * 3 / 2，超出核心线程存活时间为 30，单位seconds</p>
     * <p> maximumPoolSize = 3 * corePoolSize / 2 </p>
     *
     * @param corePoolSize 最大线程数
     * @return
     */
    default ThreadPoolExecutor threadPoolExecutor(long corePoolSize) {
        return threadPoolExecutor(Integer.parseInt(String.valueOf(corePoolSize)));
    }

    /**
     * 定义线程池。
     * <p> 最大线程数量= 核心线程数 * 3 / 2，超出核心线程存活时间为 30，单位seconds</p>
     * <p> maximumPoolSize = 3 * corePoolSize / 2 </p>
     *
     * @param corePoolSize 最大线程数
     * @return
     */
    default ThreadPoolExecutor threadPoolExecutor(int corePoolSize, String threadName) {
        return threadPoolExecutor(corePoolSize, new DefaultThreadFactory(threadName));
    }

    /**
     * 定义线程池。
     * <p> 最大线程数量= 核心线程数 * 3 / 2，超出核心线程存活时间为 30，单位seconds</p>
     * <p> maximumPoolSize = 3 * corePoolSize / 2 </p>
     *
     * @param corePoolSize 最大线程数
     * @return
     */
    default ThreadPoolExecutor threadPoolExecutor(long corePoolSize, String threadName) {
        return threadPoolExecutor(Integer.parseInt(String.valueOf(corePoolSize)), threadName);
    }

    /**
     * 定义线程池。
     * <p> 最大线程数量= 核心线程数 * 3 / 2，超出核心线程存活时间为 30，单位seconds</p>
     * <p> maximumPoolSize = 3 * corePoolSize / 2 </p>
     *
     * @param corePoolSize 最大线程数
     * @return
     */
    default ThreadPoolExecutor threadPoolExecutor(int corePoolSize, ThreadFactory threadFactory) {
        int maximumPoolSize = 3 * corePoolSize / 2;
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 30,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(30),
                threadFactory,
                new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    /**
     * 定义线程池。
     * <p> 最大线程数量= 核心线程数 * 3 / 2，超出核心线程存活时间为 30，单位seconds</p>
     * <p> maximumPoolSize = 3 * corePoolSize / 2 </p>
     *
     * @param corePoolSize 最大线程数
     * @return
     */
    default ThreadPoolExecutor threadPoolExecutor(long corePoolSize, ThreadFactory threadFactory) {
        return threadPoolExecutor(Integer.parseInt(String.valueOf(corePoolSize)), threadFactory);
    }

    /**
     * 解析list类型
     *
     * @param futureList
     * @param <T>
     * @return
     */
    default <T> List<T> parseFutureList(List<Future<List<T>>> futureList) {
        List<T> parseResult = new ArrayList<>();
        futureList.forEach(f -> {
            try {
                parseResult.addAll(f.get());
            } catch (Exception e) {
                logger.error("{}", e.getMessage(), e);
            }
        });
        return parseResult;
    }

    /**
     * 解析泛型，指定某个对象
     *
     * @param futureList
     * @param <T>
     * @return
     */
    default <T> List<T> parseFutureObject(List<Future<T>> futureList) {
        List<T> list = new ArrayList<>();
        futureList.forEach(f -> {
            try {
                list.add(f.get());
            } catch (Exception e) {
                logger.error("{}", e.getMessage(), e);
            }
        });
        return list;
    }

    /**
     * 解析 JSONArray
     *
     * @param futureList
     * @return
     */
    default JSONArray parseCompletableFutureJSONArray(List<CompletableFuture<JSONArray>> futureList) {
        JSONArray parseResult = new JSONArray();
        futureList.forEach(f -> {
            try {
                parseResult.addAll(f.get());
            } catch (Exception e) {
                logger.error("{}", e.getMessage(), e);
            }
        });
       return parseResult;
    }

    /**
     * 线程池关闭方法，
     *
     * @param executor
     * @return
     * @throws InterruptedException
     */
    default boolean shutdown(ExecutorService executor) {
        return shutdown(executor, 3, TimeUnit.SECONDS);
    }

    default boolean shutdown(ExecutorService executor, long timeout, TimeUnit timeUnit) {
        if (executor == null) {
            return false;
        }
        executor.shutdown();
        try {
            return executor.awaitTermination(timeout, timeUnit);
        } catch (InterruptedException e) {
            logger.error("executer shutdown error. error message: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 利用返回BiConsumer创建多线程处理
     *
     * @return BiConsumer
     */
    default BiConsumer<Long, Runnable> createBiConsumerWithRunAsync() {
        return (poolSize, processing) -> {
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutor(poolSize);
            List<CompletableFuture<Void>> futureList = new ArrayList<>();
            for (long i = 0; i < poolSize; i++) {
                futureList.add(CompletableFuture.runAsync(processing, threadPoolExecutor));
            }
            CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
            shutdown(threadPoolExecutor);
        };
    }

    /**
     * 创建多线程处理
     *
     * @param collection 待处理数据集
     * @param <T>        数据集类型
     * @return {@linkplain BiConsumer} <线程池大小, Consumer<T>>
     */
    default <T> BiConsumer<Integer, Consumer<T>> createRunAsync(Collection<T> collection) {
        return (poolSize, consumer) -> processRunAsync(poolSize, collection, consumer);
    }

    /**
     * 创建多线程处理
     *
     * @param poolSize   线程池大小
     * @param collection 待处理数据集
     * @param <T>        数据集类型
     * @return {@linkplain Consumer}
     */
    default <T> Consumer<Consumer<T>> createRunAsync(int poolSize, Collection<T> collection) {
        return consumer -> processRunAsync(poolSize, collection, consumer);
    }

    /**
     * 创建多线程处理
     *
     * @param poolSize   线程池大小
     * @param collection 待处理数据集
     * @param consumer   消费者，无返回参数
     * @param <T>        数据集类型
     */
    default <T> void processRunAsync(int poolSize, Collection<T> collection, Consumer<T> consumer) {
        if (CollectionUtils.isEmpty(collection)) {
            return;
        }
        int corePoolSize = Math.min(collection.size(), poolSize);
        ConcurrentLinkedDeque<T> linkedDeque = new ConcurrentLinkedDeque<>(collection);
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutor(corePoolSize);
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (long i = 0; i < corePoolSize; i++) {
            futureList.add(CompletableFuture.runAsync(() -> {
                while (CollectionUtils.isNotEmpty(linkedDeque)) {
                    T poll = linkedDeque.poll();
                    if (poll == null) {
                        break;
                    }
                    consumer.accept(poll);
                }
            }, threadPoolExecutor));
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
        shutdown(threadPoolExecutor);
    }

    /**
     * 创建带有返回值的多线程
     *
     * @param list       需要多线程处理的list
     * @param otherParam 处理list需要的其他参数，例如请求fb的accessToken
     * @param function   函数式
     * @param poolSize   线程池大小，默认：1
     * @param <T>        需要多线程处理的list
     * @param <R>        返回类型
     * @param <D>        处理list需要的其他参数，例如请求fb的accessToken
     * @return 返回值 List<R>
     */
    default <T, R, D> List<R> createSupplyAsync(List<T> list, D otherParam, BiFunction<T, D, R> function, int poolSize) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        poolSize = Math.max(poolSize, 1);
        int corePoolSize = Math.min(list.size(), poolSize);
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutor(corePoolSize);
        ConcurrentLinkedDeque<T> linkedDeque = new ConcurrentLinkedDeque<>(list);
        List<CompletableFuture<List<R>>> futureList = new ArrayList<>();
        for (int i = 0; i < corePoolSize; i++) {
            futureList.add(CompletableFuture.supplyAsync(() -> {
                List<R> rs = new ArrayList<>();
                while (CollectionUtils.isNotEmpty(linkedDeque)) {
                    T poll = linkedDeque.poll();
                    if (poll == null) {
                        break;
                    }
                    rs.add(function.apply(poll, otherParam));
                }
                return rs;
            }, threadPoolExecutor));
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        List<R> rs = parseCompletableFutureList(futureList);
        shutdown(threadPoolExecutor);
        return rs;
    }

    /**
     * 创建带有返回值的多线程
     *
     * @param list       需要多线程处理的list
     * @param otherParam 处理list需要的其他参数，例如请求fb的accessToken
     * @param function   函数式
     * @param poolSize   线程池大小，默认：1
     * @param <T>        需要多线程处理的list
     * @param <D>        处理list需要的其他参数，例如请求fb的accessToken
     * @return 返回值 List<R>
     */
    default <T, D> JSONObject createSupplyAsyncJSONObject(Collection<T> list, D otherParam, BiFunction<T, D, JSONObject> function, int poolSize) {
        if (CollectionUtils.isEmpty(list)) {
            return JSONObject.of();
        }
        poolSize = Math.max(poolSize, 1);
        int corePoolSize = Math.min(list.size(), poolSize);
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutor(corePoolSize);
        ConcurrentLinkedDeque<T> linkedDeque = new ConcurrentLinkedDeque<>(list);
        List<CompletableFuture<JSONObject>> futureList = new ArrayList<>();
        for (int i = 0; i < corePoolSize; i++) {
            futureList.add(CompletableFuture.supplyAsync(() -> {
                JSONObject rtJson = JSONObject.of();
                while (CollectionUtils.isNotEmpty(linkedDeque)) {
                    T poll = linkedDeque.poll();
                    if (poll == null) {
                        break;
                    }
                    rtJson.putAll(function.apply(poll, otherParam));
                }
                return rtJson;
            }, threadPoolExecutor));
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        JSONObject rs = parseCompletableFutureJSONObject(futureList);
        shutdown(threadPoolExecutor);
        return rs;
    }

    default <T> List<T> parseCompletableFutureList(List<CompletableFuture<List<T>>> futureList) {
        List<T> parseResult = new ArrayList<>();
        futureList.forEach(f -> {
            try {
                parseResult.addAll(f.get());
            } catch (Exception var3) {
                logger.error("{}", var3.getMessage(), var3);
            }
        });
        return parseResult;
    }

    default JSONObject parseCompletableFutureJSONObject(List<CompletableFuture<JSONObject>> futureList) {
        JSONObject jsonObject = JSONObject.of();
        futureList.forEach(f -> {
            try {
                jsonObject.putAll(f.get());
            } catch (Exception e) {
                logger.error("{}", e.getMessage(), e);
            }
        });
        return jsonObject;
    }

    class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory(String name) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = name + "-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }
}
