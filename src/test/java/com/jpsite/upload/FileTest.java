package com.jpsite.upload;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author jiangpeng
 * @date 2019/11/1516:59
 */
@Slf4j
public class FileTest {
    /**
     * 文件分块的流程：
     * 1、获取源文件长度
     * 2、根据设定的分块文件的大小计算出块数
     * 3、从源文件读数据依次向每一个块文件写数据。
     *
     * @throws IOException
     */
    @Test
    public void testChunk() throws IOException {
        File sourceFile = new File("D:/develop/lucene.mp3");
        String fileName = sourceFile.getName();
        // File sourceFile = new File("d:/logo.png");
        String chunkPath = "D:/develop/chunk/";
        File chunkFolder = new File(chunkPath);
        // 不存在创建文件夹
        if (!chunkFolder.exists()) {
            chunkFolder.mkdirs();
        }
        // 分块大小
        long chunkSize = 1024 * 1024 * 1;
        //计算分块数量 算法公式
        long chunkNum = (long) Math.ceil(sourceFile.length() * 1.0 / chunkSize);
        if (chunkNum <= 0) {
            chunkNum = 1;
        }
        //缓冲区大小
        byte[] b = new byte[1024];
        // 使用RandomAccessFile访问文件
        RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r");
        //分块
        for (int i = 0; i < chunkNum; i++) {
            //创建分块文件
            File file = new File(chunkPath + fileName.substring(0, fileName.lastIndexOf(".") + 1) + i);
            // 已经存在则跳过上传（断点续传）
            if(testCheckChunk(file.getName())){
                continue;
            }

            boolean newFile = file.createNewFile();
            if (newFile) {
                //向分块文件中写数据
                RandomAccessFile raf_write = new RandomAccessFile(file, "rw");
                int len;
                while ((len = raf_read.read(b)) != -1) {
                    raf_write.write(b, 0, len);
                    if (file.length() > chunkSize) {
                        break;
                    }
                }
                raf_write.close();
            }
        }
        raf_read.close();
    }

    /**
     * 文件合并流程：
     * 1、找到要合并的文件并按文件合并的先后进行排序。
     * 2、创建合并文件
     * 3、依次从合并的文件中读取数据向合并文件写入数
     *
     * @throws IOException
     */
    @Test
    public void testMerge() throws IOException {
        // 块文件目录
        File chunkFolder = new File("D:/develop/chunk/");
        // 合并文件
        File mergeFile = new File("D:/develop/lucene1.mp3");

        if (mergeFile.exists()) {
            mergeFile.delete();
        }

        //创建新的合并文件
        mergeFile.createNewFile();
        //用于写文件
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
        //指针指向文件顶端
        raf_write.seek(0);
        //缓冲区
        byte[] b = new byte[1024];
        //分块列表
        File[] fileArray = chunkFolder.listFiles();
        // 转成集合，便于排序
        List<File> fileList = Arrays.asList(Objects.requireNonNull(fileArray));
        // 从小到大排序
        fileList.sort((o1, o2) -> {
            String o1Name = o1.getName();
            String o2Name = o2.getName();
            if (Integer.parseInt(o1Name.substring(o1Name.lastIndexOf(".") + 1)) < Integer.parseInt(o2Name.substring(o2Name.lastIndexOf(".") + 1))) {
                return -1;
            }
            return 1;
        });
        //合并文件
        for (File chunkFile : fileList) {
            RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "rw");
            int len;
            while ((len = raf_read.read(b)) != -1) {
                raf_write.write(b, 0, len);
            }
            raf_read.close();
        }
        raf_write.close();
    }

    /**
     * 分块文件检查流程
     *
     * @throws IOException
     */

    public boolean testCheckChunk(String fileName) {
        // 块文件目录
        File chunkFolder = new File("D:/develop/chunk/");
        File[] fileArray = chunkFolder.listFiles();
        List<String> chunkFileNameList =
                Arrays.stream(fileArray).map(file -> file.getName()).collect(Collectors.toList());
        boolean existsFlag = chunkFileNameList.contains(fileName);
        if (existsFlag) {
            log.info("块文件已存在：" + fileName);
        }
        return existsFlag;
    }
}