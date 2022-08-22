package com.joe.boot.minio.configuration.config;

import com.joe.boot.minio.configuration.client.MinIoClient;
import com.joe.boot.minio.configuration.properties.MinIoProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author QiangQiang Gu
 * @date 2022-03-12 00:40:10
 */
@Configuration
@ConditionalOnClass(MinIoClient.class)
@EnableConfigurationProperties(MinIoProperties.class)
public class MinIoAutoConfiguration {

    @Resource
    private MinIoProperties minIoProperties;

    @Bean
    @ConditionalOnMissingBean(MinIoClient.class)
    public MinIoClient joeMinIoClient() {
        return new MinIoClient(minIoProperties);
    }

}
