package com.joe.boot.keypair.configuration.config;

import com.joe.boot.keypair.configuration.properties.KeyPairProperties;
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
@ConditionalOnClass(KeyPair.class)
@EnableConfigurationProperties(KeyPairProperties.class)
public class KeyPairAutoConfiguration {

    @Resource
    private KeyPairProperties keyPairProperties;

    @Bean
    @ConditionalOnMissingBean(KeyPair.class)
    public KeyPair keyPairConfig() {
        return new KeyPair(keyPairProperties);
    }

}
