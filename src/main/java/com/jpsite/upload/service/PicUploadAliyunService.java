package com.jpsite.upload.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpsite.upload.api.vo.PicUploadResult;
import com.jpsite.upload.config.AliyunConfig;
import com.jpsite.upload.util.PicValidationUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;

/**
 * @author jiangpeng
 * @date 2019/11/159:52
 */
@Service
public class PicUploadAliyunService {

    @Autowired
    private OSS ossAliyunClient;
    @Autowired
    private AliyunConfig aliyunConfig;

    private ObjectMapper objectMapper = new ObjectMapper();

    public PicUploadResult upload(MultipartFile uploadFile) {

        PicUploadResult picUploadResult = new PicUploadResult();

        if (!PicValidationUtils.isLegalPic(uploadFile)) {
            picUploadResult.setStatus("error");
            return picUploadResult;
        }

        String fileName = uploadFile.getOriginalFilename();
        // 文件新路径
        String filePath = createFilePath(fileName);
        // 上传文件
        try {
            PutObjectResult putObjectResult = ossAliyunClient.putObject(aliyunConfig.getBucketName(), filePath,
                    new ByteArrayInputStream(uploadFile.getBytes()));
            picUploadResult.setResponse(objectMapper.writeValueAsString(putObjectResult));
        } catch (Exception e) {
            e.printStackTrace();
            picUploadResult.setStatus("error");
            return picUploadResult;
        }

        picUploadResult.setStatus("done");
        picUploadResult.setName(aliyunConfig.getUrlPrefix() + filePath);
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
