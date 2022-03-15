package com.joe.boot.keypair.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author QiangQiang Gu
 */
@Data
@Component
@ConfigurationProperties(prefix = "keypair")
public class KeyPairProperties {

    private String keyStorePassword = "joekeystore";
    private String keyPassword = "joekey";
    private String keyAlias = "joekey";
    private String keystoreName = "joekey.keystore";

}
