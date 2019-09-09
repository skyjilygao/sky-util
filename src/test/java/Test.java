
import com.skyjilygao.util.VideoConvert;
import com.skyjilygao.util.ZipUtils;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Properties;


public class Test {
    public static void main(String[] args) throws FileNotFoundException {
        Map<String, String> map = System.getenv();
//        convertVideo();
        zipDir();
    }

    public static void zipDir() throws FileNotFoundException {
        String zipdir = "E:\\ideaWorkSpace\\business-management-master\\business-bills\\files\\tmp\\invoice\\";
        String dir = "E:\\ideaWorkSpace\\business-management-master\\business-bills\\files\\tmp\\invoice\\2019-09-09\\";
        String zipName = ZipUtils.toZip(dir);
        System.out.println(zipName);
//        ZipUtils.toZip(dir, zipdir, zipName);
    }
    /**
     * test ffmpeg
     */
    public static void convertVideo(){
        // 使用环境变量
        VideoConvert convert = new VideoConvert("ffmpeg.exe");
        // 直接使用全路径
//        VideoConvert convert = new VideoConvert("D:\\ffmpeg-20181127-1035206-win64-static\\ffmpeg-20181127-1035206-win64-static\\bin\\ffmpeg.exe");
        String source = "E:\\test\\video2.mp4";
        String target = "E:\\test\\video2.m3u8";
        try {
            convert.start(source, target);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
