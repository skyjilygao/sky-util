package cn.skyjilygao.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

public class TextFile implements Iterable<String> {

    private String filename;
    private int limit;

    public TextFile(String filename) {
        this(filename, 1);
    }

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

        int limit;
        // stream being read from
        BufferedReader in;
        Queue<String> queue = new PriorityQueue<>();

        // the structure method of TextFileItrator
        public TextFileIterator(String filename) {
            // 打开文件并读取第一行 如果第一行存在获得第一行
            this(filename, 1);
        }

        public TextFileIterator(String filename, int line) {
            line = line < 1 ? 1 : line;
            limit = line;
            queue = new PriorityQueue<>();
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

                for (int i = 0; i < limit; i++) {
                    String line = in.readLine();
                    if (line == null) {
                        in.close();
                        isFinish = true;
                        return false;
                    } else {
                        isFinish = false;
                        queue.add(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
