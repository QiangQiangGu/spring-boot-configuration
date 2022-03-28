package com.joe.boot.xxljob.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author QiangQiang Gu
 */
@Data
@Component
@ConfigurationProperties(prefix = "xxl.job")
public class XxlJobProperties {

    private String accessToken;

    private Admin admin = new Admin();

    private Executor executor = new Executor();

    @Data
    public static class Executor {
        private String appname = "xxl-job-executor-sample";
        private String address;
        private String ip;
        private int port = 9999;
        private String logpath = "/data/applogs/xxl-job/jobhandler";
        private int logretentiondays = 30;
    }

    @Data
    public static class Admin {
        private String addresses = "http://127.0.0.1:8080/xxl-job-admin";
    }

}
