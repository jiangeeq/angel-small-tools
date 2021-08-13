package com.jpsite.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileAppender;
import cn.hutool.core.io.file.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Objects;


/**
 * @author jiangpeng
 * @date 2021/8/1316:19
 */

//@SpringBootTest
//@RunWith(BlockJUnit4ClassRunner.class)
public class FileUtilTest {
    //    @Test
    public static void test1() throws Exception {
        final File file = FileUtil.touch(new File("./lock.txt"));
        FileAppender appender = new FileAppender(file, 1, true);
        appender.append("123");
        appender.append("abc");
        appender.append("xyz");
        appender.flush();
        FileReader fileReader = new FileReader(file);
        String result = fileReader.readString();
        System.out.println(result);

        final FileLock fileLock = getFileLock(file);
        if (Objects.isNull(fileLock)) {
            System.out.println("not get lock");
        } else {
            final String lockType = fileLock.isShared() ? "share lock" : "exclusive lock";
            System.out.println(String.format("getted lock %s", lockType));
            int i = 0;
            while (true) {
                Thread.sleep(1000);
                i++;
                System.out.println(lockType + "no release");
                if (i == 30) {
                    fileLock.release();
                    if (!fileLock.isValid()) {
                        System.out.println("lock is valid ,return while");
                        break;
                    }
                }
            }
        }
    }

    //    @Test
    public static void test2() throws Exception {
        test1();
    }

    public static FileLock getFileLock(File file) throws IOException {
        final RandomAccessFile rw = new RandomAccessFile(file, "rw");
        final FileChannel channel = rw.getChannel();
        return channel.tryLock();
    }

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            try {
                test1();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(3000);
        new Thread(() -> {
            try {
                test2();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
