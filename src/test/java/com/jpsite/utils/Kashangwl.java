package com.jpsite.utils;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiangpeng
 * @date 2020/9/2110:50
 */
@Data
@Accessors(chain = true)
public class Kashangwl{
    private long timestamp;
    private int product_id;
    private int customer_id=852723;

}
