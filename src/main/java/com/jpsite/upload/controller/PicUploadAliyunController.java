package com.jpsite.upload.controller;

import com.jpsite.upload.api.vo.PicUploadResult;
import com.jpsite.upload.service.PicUploadAliyunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author jiangpeng
 * @date 2019/11/1510:17
 */
@RestController
@RequestMapping("pic/upload/aliyun")
public class PicUploadAliyunController {
    @Autowired
    private PicUploadAliyunService picUploadAliyunService;

    @PostMapping("/")
    @ResponseBody
    public PicUploadResult uploadAliyun(@RequestParam("file") MultipartFile uploadFile) {
        return picUploadAliyunService.upload(uploadFile);
    }
}
