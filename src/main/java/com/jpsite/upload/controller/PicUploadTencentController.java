package com.jpsite.upload.controller;

import com.jpsite.upload.api.vo.PicUploadResult;
import com.jpsite.upload.service.PicUploadTencentService;
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
@RequestMapping("pic/upload/tencent")
public class PicUploadTencentController {
    @Autowired
    private PicUploadTencentService picUploadTencentService;

    @PostMapping("/")
    @ResponseBody
    public PicUploadResult uploadTencent(@RequestParam("file") MultipartFile uploadFile) {
        return picUploadTencentService.upload(uploadFile);
    }
}
