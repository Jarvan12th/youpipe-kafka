package com.jarvanlee.youpipekafka.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "spring.datasource.oss.encoded")
@Component
public class EncodedOssConfiguration {
    private String provider;

    private String endpoint;

    private String accessKey;

    private String secretKey;

    private String bucket;

    private String region;

    private String objectDirPrefix;
}
