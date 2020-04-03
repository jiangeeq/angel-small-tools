package com.jpsite.upload.api.vo;

import lombok.Data;

/**
 * 该类用于返回给前端的数据结构定义。
 * @author jiangpeng
 * @date 2019/11/159:50
 */
@Data
public class PicUploadResult {
    /** 文件唯一标识 */
    private String uid;
    /** 文件名 */
    private String name;
    /** 状态有：uploading done error removed */
    private String status;
    /** 服务端响应内容，如：'{"status": "success"}' */
    private String response;
}
