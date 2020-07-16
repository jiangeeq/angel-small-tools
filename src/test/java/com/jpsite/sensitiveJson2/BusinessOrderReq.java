package com.jpsite.sensitiveJson2;

import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@JsonEncrypt
public class BusinessOrderReq {

    private String accountName;


    private String bankCardNo;


    private String bankCode;


    private String bizCustomerId;


    private String bizOrderNo;


    private Integer cardType;


    private String idCard;


    private Integer identifyType;

    private String phone;
}
