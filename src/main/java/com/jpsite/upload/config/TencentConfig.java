package com.jpsite.upload.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author jiangpeng
 * @date 2019/11/159:38
 */
@Configuration
@PropertySource(value = {"classpath:tencent.properties"})
@ConfigurationProperties(prefix = "tencent")
@Data
public class TencentConfig {
    private String secretId;
    private String secretKey;
    private String bucketName;
    private String urlPrefix;

    @Bean
    public COSClient ossTencentClient() {
        // 初始化用户身份信息（secretId, secretKey）。
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        // 设置 bucket 的区域
        Region region = new Region("ap-guangzhou");
        ClientConfig clientConfig = new ClientConfig(region);
        // 3 生成 cos 客户端。
        return new COSClient(cred, clientConfig);
    }
}