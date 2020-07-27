package cn.skyjilygao.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 实现Iterable相关接口，直接通过for即可遍历文件内容
 * @author skyjilygao
 * @date 20200610
 */
public class TextFile implements Iterable<String> {

    private String filename;
    private int limit;

    /**
     * 初始化
     * @param filename 文件路径
     */
    public TextFile(String filename) {
        this(filename, 1);
    }

    /**
     * 初始化
     * @param filename 文件路径
     * @param limit 每次for的预先缓存行数。
     */
    public TextFile(String filename, int limit) {
        this.limit = limit;
        this.filename = filename;
    }

    // one method of the Iterable interface
    @Override
    public Iterator<String> iterator() {
        return new TextFileIterator(filename, limit);
    }

    class TextFileIterator implements Iterator<String> {

        /**
         * 每次缓存大小
         */
        int limit;
        /**
         * stream being read from
         */
        BufferedReader in;
        /**
         * 缓存数据
         */
        Queue<String> queue;

        /**
         * 打开文件并读取第一行 如果第一行存在获得第一行
         * @param filename
         */
        public TextFileIterator(String filename) {
            this(filename, 1);
        }

        public TextFileIterator(String filename, int line) {
            line = line < 1 ? 1 : line;
            limit = line;
            queue = new LinkedList<>();
            // 打开文件并读取第一行 如果第一行存在获得第一行
            try {
                in = new BufferedReader(new FileReader(filename));
                loadNext();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean hasNext() {
            return queue.size() > 0 || loadNext();
        }

        @Override
        public String next() {
            try {
                return queue.poll();
            } catch (Exception e) {
                throw e;
           }
        }

        boolean isFinish = false;

        private boolean loadNext() {
            try {
                for (int i = 0; i < limit && !isFinish; i++) {
                    String line = in.readLine();
                    if (line == null) {
                        in.close();
                        isFinish = true;
                        break;
                    } else {
                        isFinish = false;
                        queue.add(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(queue.size() == 0){
                return false;
            }
            return true;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
