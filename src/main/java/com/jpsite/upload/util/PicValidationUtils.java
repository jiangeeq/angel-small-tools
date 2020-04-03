package com.jpsite.upload.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 校验图片合法工具类
 * @author jiangpeng
 * @date 2019/11/1514:46
 */
public class PicValidationUtils {
    /**
     * 允许上传的格式
     */
    private static final String[] IMAGE_TYPES = new String[]{".bmp", ".jpg", ".jpeg", ".gif", ".png"};

    public static boolean isLegalPic(MultipartFile uploadFile) {
        boolean isLegal = false;

        for (String imageType : IMAGE_TYPES) {
            if (StringUtils.endsWithIgnoreCase(uploadFile.getOriginalFilename(), imageType)) {
                isLegal = true;
                break;
            }
        }
        return isLegal;
    }
}
