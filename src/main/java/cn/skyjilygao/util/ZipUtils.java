package cn.skyjilygao.util;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * ZipUtils
 * 参考：https://www.cnblogs.com/zeng1994/p/7862288.html
 *
 * @author skyjilygao
 * @version 1.0.1
 * @date 20190909
 * @since 1.0.0
 */
@Slf4j
public class ZipUtils {
    private static final int BUFFER_SIZE = 2 * 1024;

    /**ZipUtils
     * @param srcDir      压缩文件夹源路径
     * @throws RuntimeException      压缩失败会抛出运行时异常
     * @throws FileNotFoundException 文件找不到
     * @return 压缩文件全路径
     */
    public static String toZip(String srcDir)
            throws RuntimeException, FileNotFoundException {
        File src = new File(srcDir);
        String runTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        File zipFIle = new File(src.getAbsolutePath()+"_ct"+runTime+".zip");
        String zipFullName = zipFIle.getAbsolutePath();
        FileOutputStream fos1 = new FileOutputStream(new File(zipFullName));
        toZip(srcDir, fos1, true);
        return zipFullName;
    }

    /**ZipUtils
     * @param srcDir      压缩文件夹源路径
     * @param zipFullName 压缩文件输出全路径(即：文件夹 + 文件名)
     * @throws RuntimeException      压缩失败会抛出运行时异常
     * @throws FileNotFoundException 文件找不到
     */
    public static void toZip(String srcDir, String zipFullName)
            throws RuntimeException, FileNotFoundException {
        FileOutputStream fos1 = new FileOutputStream(new File(zipFullName));
        toZip(srcDir, fos1, true);
    }

    /**
     * @param srcDir   压缩文件夹源路径
     * @param filePath 压缩文件输出路径
     * @param fileName 压缩文件名称
     * @throws RuntimeException      压缩失败会抛出运行时异常
     * @throws FileNotFoundException 文件找不到
     */
    public static void toZip(String srcDir, String filePath, String fileName)
            throws RuntimeException, FileNotFoundException {
        toZip(srcDir, filePath + "/" + fileName);
    }

    /**
     * @param srcDir           压缩文件夹路径
     * @param out              压缩文件输出流
     * @param KeepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    private static void toZip(String srcDir, OutputStream out, boolean KeepDirStructure)
            throws RuntimeException {
        long start = System.currentTimeMillis();
        log.info("压缩开始...");
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            File sourceFile = new File(srcDir);
            compress(sourceFile, zos, sourceFile.getName(), KeepDirStructure);
            long end = System.currentTimeMillis();
            log.info("压缩完成，耗时：" + (end - start) + " ms");
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils. ", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 递归压缩方法
     *
     * @param sourceFile       源文件
     * @param zos              zip输出流
     * @param name             压缩后的名称
     * @param KeepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws Exception
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name,
                                 boolean KeepDirStructure) throws Exception {
        byte[] buf = new byte[BUFFER_SIZE];
        if (sourceFile.isFile()) {
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            try(FileInputStream in = new FileInputStream(sourceFile)){
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                // Complete the entry
                zos.closeEntry();
//                in.close();
            }catch (Exception e){
                throw e;
            }
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if (KeepDirStructure) {
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }
            } else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (KeepDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(), KeepDirStructure);
                    } else {
                        compress(file, zos, file.getName(), KeepDirStructure);
                    }
                }
            }
        }
    }

    /**
     * 给压缩文件设置访问密码
     *
     * @param sourceFile  源文件
     * @param zipFilePath 压缩文件路径，包含文件名。例如："path/to/your/encrypted.zip"
     * @param password    ZIP文件的密码
     */
    public static void toZip(File sourceFile, String zipFilePath, String password) throws IOException {
        // 创建ZipFile对象
        try (ZipFile zipFile = new ZipFile(zipFilePath, password.toCharArray())) {
            // 创建ZipParameters对象，并设置密码和加密方法
            ZipParameters parameters = new ZipParameters();
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD); // 或者使用AES加密
            // 添加文件到ZIP，并应用参数
            zipFile.addFile(sourceFile, parameters);
            log.info("ZIP file created with password successfully. >>> {}", zipFilePath);
        }
    }

}