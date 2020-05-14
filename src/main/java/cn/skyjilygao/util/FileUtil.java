package cn.skyjilygao.util;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hpsf.Filetime;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;

/**
 * 文件工具类
 */
@Slf4j
public class FileUtil {

    /**
     * 下载图片到本地
     *
     * @param uri      网络图片url
     * @param fileNmae 文件名
     * @param savePath 保存到底的路径
     * @return file AbsolutePath (include file name)
     */
    public static String download(String uri, String fileNmae, String savePath) {
        File sf = new File(savePath);
        if (!sf.exists()) {
            sf.mkdirs();
        }
        try {
            URL url = new URL(uri);
            // 打开连接
            URLConnection con = url.openConnection();
            //设置请求超时为5s
            con.setConnectTimeout(5 * 1000);
            // 输入流
            InputStream is = con.getInputStream();

            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 输出的文件流

            String filePath = sf.getPath() + "/" + fileNmae;
            log.info("file save path=" + filePath);

            try (OutputStream os = new FileOutputStream(filePath)) {
                // 开始读取
                while ((len = is.read(bs)) != -1) {
                    os.write(bs, 0, len);
                }
                // 完毕，关闭所有链接
//                os.close();
//                is.close();
            } catch (Exception e) {

            }
        } catch (Exception e) {
            log.error("", e);
        }
        return sf.getAbsolutePath() + "/" + fileNmae;
    }

    /**
     * 从上传MultipartFile获取文件
     *
     * @param file
     * @return localFile
     * @throws IOException
     */
    public static File transferToLocalFile(MultipartFile file, String localPath) throws IOException {
        String fileName = file.getOriginalFilename();
        if (StringUtils.isNotBlank(fileName)) {
            File pf = new File(localPath);
            pf.mkdirs();

            String fileName1 = System.currentTimeMillis() + "." + getSuffix(fileName);
            File localFile = new File(pf.getAbsolutePath() + "/" + fileName1);
            try {
                localFile.getParentFile().mkdirs();
                log.info("localFile=" + localFile.getPath());
                log.info("pf=" + pf.getAbsolutePath());
                file.transferTo(localFile);
                String osName = System.getProperty("os.name").toLowerCase();
                if (osName.contains("linux")) {
                    Runtime.getRuntime().exec("chmod 775 -R " + localFile.getParent());
                } else if (osName.contains("windows")) {
                }
                return localFile;
            } catch (IOException e) {
                throw e;
            }
        }
        return null;
    }

    /**
     * 取文件名的后缀
     *
     * @param m 文件带后缀的原名
     * @return
     */
    private static String getSuffix(String m) {
        if (m.contains(".")) {
            String[] split = m.split("\\.");
            String suffix = split[1];
            return suffix;
        }
        return "";
    }

    /**
     * 解析文件类型
     *
     * @param file
     * @return
     */
    public static FileType parseFileType(File file) {
        String fileName = file.getName();
        String suffix = getSuffix(fileName);
        return FileType.parse(suffix);
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath
     * @param fileName
     * @return
     */
    public static boolean exists(String filePath, String fileName) {
        return exists(filePath + "/" + fileName) ? true : false;
    }

    /**
     * 判断文件是否存在
     *
     * @param fileFullPath 文件全路径
     * @return
     */
    public static boolean exists(String fileFullPath) {
        File file = new File(fileFullPath);
        return file.exists() ? true : false;
    }

    /**
     * 文件类型
     */
    public enum FileType {

        /**
         * 未知类型
         */
        UNKNOW(0),
        /**
         * 图片
         */
        IMAGE(1),
        /**
         * 视频
         */
        VIDEO(2);

        private int index;

        private FileType(int index) {
            this.index = index;
        }

        public static FileType parse(String suffix) {
            switch (suffix.toLowerCase()) {
//                bmp,jpg,png,tif,gif,pcx,tga,exif,fpx,svg,psd,cdr,pcd,dxf,ufo,eps,ai,raw,WMF,webp
                case "bmp":
                    return IMAGE;
                case "jpg":
                    return IMAGE;
                case "png":
                    return IMAGE;
                case "tif":
                    return IMAGE;
                case "gif":
                    return IMAGE;
                case "mp4":
                    return VIDEO;
                case "rmvb":
                    return VIDEO;
                default:
                    return UNKNOW;
            }
        }

        public int getIndex() {
            return index;
        }
    }

}
