package com.joe.boot.minio.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 */
@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinIoProperties {

    private String endpoint = "http://127.0.0.1";
    private Integer port = 9000;
    private String accessKey = "minioadmin";
    private String secretKey = "minioadmin";
    private String bucketName;
    private String bucketNodes;

}
