package cn.skyjilygao.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 使用多线程输出，防止阻塞
 * @author skyjilygao
 * @since 20181128
 * @version 1.0
 */
//@Slf4j
public class VideoThread extends Thread {
    Logger log = LoggerFactory.getLogger(VideoThread.class);
    private Process p;
    public VideoThread(Process p){
        this.p = p;
    }

    @Override
    public void run(){
        BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        String line = null;
        try {
            while ((line = err.readLine()) != null) {
                log.info(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                err.close();
                try {
                    p.waitFor();
                    p.destroy();
                    log.info("video convert completed...");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
