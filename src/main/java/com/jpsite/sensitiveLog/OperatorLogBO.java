package com.jpsite.sensitiveLog;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统日志
 * @author jiangpeng
 * @date 2019/12/1710:18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OperatorLogBO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 操作IP
     */
    private String requestIp;

    /**
     * 操作类型 1 操作记录 2异常记录
     */
    private Integer type;

    /**
     * 操作人ID
     */
    private String userName;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 请求方法
     */
    private String actionMethod;

    /**
     * 请求url
     */
    private String actionUrl;

    /**
     * 请求参数
     */
    private String params;

    /**
     * 浏览器
     */
    private String ua;

    /**
     * 类路径
     */
    private String classPath;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

    /**
     * 消耗时间
     */
    private Long consumingTime;

    /**
     * 异常详情信息 堆栈信息
     */
    private String exDetail;

    /**
     * 异常描述 e.getMessage
     */
    private String exDesc;
}
