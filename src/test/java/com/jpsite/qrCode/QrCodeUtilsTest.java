package com.jpsite.qrCode;

import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author jiangpeng
 * @date 2020/4/314:57
 */
public class QrCodeUtilsTest {

    /**
     * 生成二维码
     */
    @Test
    public void testQrEncode() throws IOException, WriterException {
        final String path = new File("二维码.png").getAbsolutePath();
        System.out.println(path);
        QrCodeUtils.qrEncode("请给作者一个start", null, new FileOutputStream(path));
    }

    /**
     * 读取二维码
     */
    @Test
    public void testQrReader() throws IOException, NotFoundException {
        QrCodeUtils.qrReader(new File("二维码.png"));
    }
}
