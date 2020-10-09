package com.jpsite.utils;

import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.DigestUtils;

/**
 * @author jiangpeng
 * @date 2020/9/2110:38
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SignatureUtilsTest {

    @Test
    public void dd() {
        val sign = "d1ad11e475c33e8ea0ce354b3727113a";
        val url = "http://www.kashangwl.com/api/product";
        val ksw = new Kashangwl().setProduct_id(41553).setTimestamp(System.currentTimeMillis());
        val map = SignatureUtils.objectToMap(ksw);
        val str = sign + SignatureUtils.paramAppendValue(map, "");
        System.out.println("加密源串：" + str);
        val md5Digest = DigestUtils.md5DigestAsHex(str.getBytes());
        map.put("sign", md5Digest);
        System.out.println(url+"请求参数：" + map);
        final String result = RestTemplateUtils.executeHttpPost(url, map);
        System.out.println(result);

    }
}
