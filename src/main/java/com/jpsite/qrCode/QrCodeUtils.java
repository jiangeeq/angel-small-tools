package com.jpsite.qrCode;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码生产工具类
 *
 * @author jiangpeng
 * @date 2019/11/28 0028
 */
@Slf4j
public class QrCodeUtils {
    /**
     * 生成二维码
     *
     * @Param Content 二维码内容
     * @Param outputStream 目标输出流
     */
    public static void qrEncode(String content, File logoFile, OutputStream outputStream) throws WriterException,
            IOException {
        int width = 200;
        int height = 200;
        String imageType = "png";

        Map<EncodeHintType, Object> hints = new HashMap<>(8);
        //内容编码格式
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // 指定纠错等级
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 设置二维码边的边缘空白，非负数
        hints.put(EncodeHintType.MARGIN, 1);
        // 生产二维码
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
//        MatrixToImageWriter.writeToPath(bitMatrix, format, new File("D:\\zxing.png").toPath());// 输出二维码图片到本地
//        MatrixToImageWriter.writeToStream(bitMatrix, format, outputStream);// 输出二维码图片到目标输出流
        // 解决二维码log黑白色问题
        MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig(0xFF000001, 0xFFFFFFFF);

        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix, matrixToImageConfig);
        if (logoFile != null) {
            // 给二维码添加logo
            logoMatrix(bufferedImage, logoFile);
        }
        // 输出二维码图片到目标输出流
        ImageIO.write(bufferedImage, imageType, outputStream);
        log.info("二维码生成成功！");
    }

    /**
     * 识别二维码
     *
     * @param file 二维码图片
     * @throws IOException
     * @throws NotFoundException
     */
    public static void qrReader(File file) throws IOException, NotFoundException {
        MultiFormatReader formatReader = new MultiFormatReader();
        // 读取二维码图片内容
        BufferedImage bufferedImage = ImageIO.read(file);
        // 解析二维码成 binaryBitmap
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(bufferedImage)));
        // 定义二维码解析配置参数
        Map<DecodeHintType, String> hints = new HashMap<>(8);
        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");

        Result result = formatReader.decode(binaryBitmap, hints);

        log.info("解析结果：[{}]", result.toString());
        log.info("二维码格式类型：[{}]", result.getBarcodeFormat());
        log.info("二维码文本内容：[{}]", result.getText());
        // 刷新缓冲区
        bufferedImage.flush();
    }

    /**
     * 二维码添加logo
     *
     * @param matrixImage 原二维码图片
     * @param logoFile    logo图片
     * @return 返回带有logo的二维码图片
     * 参考：https://blog.csdn.net/weixin_39494923/article/details/79058799
     */
    private static BufferedImage logoMatrix(BufferedImage matrixImage, File logoFile) throws IOException {
        /*
         * 读取二维码图片，并构建绘图对象
         */
        Graphics2D g2 = matrixImage.createGraphics();

        int matrixWidth = matrixImage.getWidth();
        int matrixHeight = matrixImage.getHeight();
        /*
         * 读取Logo图片
         */
        BufferedImage logo = ImageIO.read(logoFile);

        int logoWidth = matrixWidth / 4;
        int logoHeight = matrixHeight / 4;

        int x = matrixWidth / 10 * 4;
        int y = matrixHeight / 10 * 4;

        //开始绘制图片
        g2.drawImage(logo, x, y, logoWidth, logoHeight, null);
        BasicStroke stroke = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        // 设置笔画对象
        g2.setStroke(stroke);
        //指定弧度的圆角矩形
        RoundRectangle2D.Float round = new RoundRectangle2D.Float(x, y, logoWidth, logoHeight, 20, 20);
        g2.setColor(Color.white);
        // 绘制圆弧矩形
        g2.draw(round);
        //设置logo 有一道灰色边框
        BasicStroke stroke2 = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        // 设置笔画对象
        g2.setStroke(stroke2);
        RoundRectangle2D.Float round2 = new RoundRectangle2D.Float(x + 2, y + 2, logoWidth - 4, logoHeight - 4, 20, 20);
        g2.setColor(new Color(128, 128, 128));
        // 绘制圆弧矩形
        g2.draw(round2);

        g2.dispose();
        matrixImage.flush();
        return matrixImage;
    }
}
