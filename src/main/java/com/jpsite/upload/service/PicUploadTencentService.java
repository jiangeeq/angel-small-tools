package com.jpsite.upload.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpsite.upload.api.vo.PicUploadResult;
import com.jpsite.upload.config.TencentConfig;
import com.jpsite.upload.util.FileConvertUtils;
import com.jpsite.upload.util.PicValidationUtils;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;

/**
 * @author jiangpeng
 * @date 2019/11/159:52
 */
@Service
public class PicUploadTencentService {

    @Autowired
    private COSClient ossTencentClient;
    @Autowired
    private TencentConfig tencentConfig;

    private ObjectMapper objectMapper = new ObjectMapper();

    public PicUploadResult upload(MultipartFile uploadFile) {

        PicUploadResult picUploadResult = new PicUploadResult();

        if (!PicValidationUtils.isLegalPic(uploadFile)) {
            picUploadResult.setStatus("error");
            return picUploadResult;
        }

        String fileName = uploadFile.getOriginalFilename();
        // 文件新路径
        String key = createFilePath(fileName);
        // 上传文件
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(tencentConfig.getBucketName(), key,
                    FileConvertUtils.multipartFileToFile(uploadFile));

            PutObjectResult putObjectResult = ossTencentClient.putObject(putObjectRequest);
            picUploadResult.setResponse(objectMapper.writeValueAsString(putObjectResult));
        } catch (Exception e) {
            e.printStackTrace();
            picUploadResult.setStatus("error");
            return picUploadResult;
        }

        picUploadResult.setStatus("done");
        picUploadResult.setName(tencentConfig.getUrlPrefix() + key);
        picUploadResult.setUid(String.valueOf(System.currentTimeMillis()));

        return picUploadResult;
    }

    private String createFilePath(String sourceFileName) {
        LocalDate dateTime = LocalDate.now();
        return "images/" + dateTime.getYear()
                + "/" + dateTime.getMonthValue() + "/"
                + dateTime.getDayOfMonth() + "/" + System.currentTimeMillis() +
                RandomUtils.nextInt(100, 9999) + "." +
                StringUtils.substringAfterLast(sourceFileName, ".");
    }
}
