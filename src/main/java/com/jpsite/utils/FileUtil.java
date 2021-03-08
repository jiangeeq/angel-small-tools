package com.jpsite.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * 文件操作工具类
 */
@Slf4j
public class FileUtil {
    /**
     * 读取文件内容为二进制数组
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static byte[] read(String filePath) throws IOException {

        InputStream in = new FileInputStream(filePath);
        byte[] data = inputStream2ByteArray(in);
        in.close();

        return data;
    }

    /**
     * 流转二进制数组
     *
     * @param in
     * @return
     * @throws IOException
     */
    private static byte[] inputStream2ByteArray(InputStream in) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

    /**
     * 保存文件
     *
     * @param filePath
     * @param fileName
     * @param content
     */
    public static File save(String filePath, String fileName, byte[] content) {
        try {
            File filedir = new File(filePath);
            if (!filedir.exists()) {
                filedir.mkdirs();
            }
            File file = new File(filedir, fileName);
            OutputStream os = new FileOutputStream(file);
            os.write(content, 0, content.length);
            os.flush();
            os.close();
            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取路径
     *
     * @return
     * @throws Exception
     */
    public static String getFilePath() throws Exception {
        //获取当前文件的根路径
        File path = new File(ResourceUtils.getURL("classpath:").getPath());
        if (!path.exists()) {
            path = new File("");
        }

        //盘符路径
        StringBuilder codeUrl = new StringBuilder();
        codeUrl.append(path.getAbsolutePath()).append(File.separator).append("static").append(File.separator).append("video").append(File.separator);
        File file = new File(codeUrl.toString());
        if (!file.exists()) {
            file.mkdirs();
        }
        return codeUrl.toString();
    }

    /**
     * 是否是音频文件
     *
     * @param fileName
     * @return
     */
    public static boolean isAudio(String fileName) {
        String[] audioTypes = new String[]{".wav", ".flac", ".opus", ".m4a", ".mp3"};

        boolean isLegal = false;

        for (String audioType : audioTypes) {
            if (StringUtils.endsWithIgnoreCase(fileName, audioType)) {
                isLegal = true;
                break;
            }
        }
        return isLegal;
    }

    /**
     * 是否是图片
     *
     * @param fileName
     * @return
     */
    public static boolean isLegalPic(String fileName) {
        String[] IMAGE_TYPES = new String[]{".bmp", ".jpg", ".jpeg", ".gif", ".png"};

        boolean isLegal = false;

        for (String imageType : IMAGE_TYPES) {
            if (StringUtils.endsWithIgnoreCase(fileName, imageType)) {
                isLegal = true;
                break;
            }
        }
        return isLegal;
    }

    /**
     * 文件太大上传时使用该方法
     * 在服务器中生成一个临时文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static File multipartBigFileToFile(MultipartFile file) throws Exception {
        File toFile = null;

        if (file == null || file.getSize() <= 0) {
            Assert.isNull(file, "没有文件！");
        } else {
            toFile = save(getFilePath(), file.getOriginalFilename(), file.getBytes());
        }
        return toFile;
    }

    /**
     * MultipartFile 转 File
     * 如果文件太大，则无法读入内存， file.getInputStream()将报错 @See
     *
     * @param file
     * @return File
     * @throws Exception
     */
    public static File multipartFileToFile(MultipartFile file) throws Exception {
        File toFile = null;

        if (file == null || file.getSize() <= 0) {
            Assert.isNull(file, "没有文件！");
        } else {
            InputStream ins = file.getInputStream();
            toFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
            inputStreamToFile(ins, toFile);
            ins.close();
        }
        return toFile;
    }

    private static void inputStreamToFile(InputStream ins, File file) {
        int byteSize = 8192;
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead;
            byte[] buffer = new byte[byteSize];
            while ((bytesRead = ins.read(buffer, 0, byteSize)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
