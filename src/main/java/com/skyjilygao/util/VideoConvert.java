package com.skyjilygao.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author skyjilygao
 * @since 20181128
 * @version 1.0
 */
public class VideoConvert {
    private String ffmpeg = "ffmpeg";
    private List<String> cmdList;
    private static Process p;
    private String status;

    private String source;
    private String target;
    private VideoConvert() {}

    /**
     * 初始化：指定ffmpeg路径
     * @param ffmpegPath
     */
    public VideoConvert(String ffmpegPath){
        this.ffmpeg = ffmpegPath;
    }

    /**
     * 开始转换
     * @param source 目前只支持mp4格式，源视频路径
     * @param target 输出路径，必须是m3u8格式。
     * @throws InterruptedException
     * @throws FileNotFoundException
     */
    public void start(String source, String target) throws InterruptedException, FileNotFoundException {
        this.source = source;
        this.target = target;
//        checkFfmpeg();
        checkSource();
        processM3U8(source, target);
    }

    private void checkSource() throws FileNotFoundException {
        File fm = new File(source);
        if (!fm.exists()) {
            throw new FileNotFoundException("source不存在：" + source);
        }
    }

    /**
     * 拼接ffmpeg命令：ffmpeg -i test.mp4 -c:v libx264 -hls_time 60 -hls_list_size 0 -c:a aac -strict -2 -f hls output.m3u8
     * @param source
     * @return
     */
    private boolean processM3U8(String source, String target) {
        File targetFile = new File(target);
        File parentDir  = targetFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        List<String> commend = new ArrayList<String>();
        commend.add(ffmpeg);
        commend.add("-i");
        commend.add(source);
        commend.add("-c:v");
        commend.add("libx264");
        commend.add("-hls_time");
        commend.add("60");
        commend.add("-hls_list_size");
        commend.add("0");
        commend.add("-c:a");
        commend.add("aac");
        commend.add("-strict");
        commend.add("-2");
        commend.add("-f");
        commend.add("hls");
        commend.add(target);
        this.cmdList = commend;
        // 通过ProcessBuilder创建
//        processBuilder(commend);

        // 通过runtime创建
        runtimeBuilder(getCommand());
        return true;
    }

    /**
     * 使用ProcessBuilder类调用cmd
     * @param command cmd命令组合，list集合，元素不能包含空格，否则可能会报错
     * @return
     */
    private static boolean processBuilder(List<String> command){
        try {
            final ProcessBuilder builder = new ProcessBuilder(command);
            builder.command(command);
            new Thread(){
                @Override
                public void run(){
                    try {
                        Process p = builder.start();
                        VideoConvert.p = p;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 检查ffmpeg是否有效
     * @throws FileNotFoundException
     */
    private void checkFfmpeg() throws FileNotFoundException {
        File fm = new File(ffmpeg);
        if (!fm.exists()) {
            throw new FileNotFoundException("ffmpeg 不存在：" + ffmpeg);
        }
    }

    /**
     * 获取状态
     * @return
     */
    public String getStatus(){
        if (p == null) {
            setStatus(VideoStatus.NO_STARTED);
        }
        if (p.isAlive()) {
            setStatus(VideoStatus.PROCESSING);
        } else {
            setStatus(VideoStatus.COMPLETED);
        }
        return status;
    }


    /**
     * 使用runtime形式调用cmd
     * @param cmdStr
     */
    private static void runtimeBuilder(String cmdStr){
        try {
            Process process = Runtime.getRuntime().exec(cmdStr);
            VideoConvert.p = process;
            VideoThread myThread = new VideoThread(process);
            myThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setStatus(String status){
        this.status = status;
    }

    /**
     * 获取执行的cmd命令
     * @return
     */
    public String getCommand(){
        StringBuffer bf = new StringBuffer();
        for (String str : cmdList) {
            bf.append(" ").append(str);
        }
        return bf.toString();
    }

    public static class VideoStatus{
        public static final String COMPLETED = "completed";
        public static final String PROCESSING = "processing";
        public static final String NO_STARTED = "no started";
    }
}