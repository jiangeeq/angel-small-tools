package com.jpsite.sensitiveJson;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;


@Data
@Accessors(chain = true)
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
