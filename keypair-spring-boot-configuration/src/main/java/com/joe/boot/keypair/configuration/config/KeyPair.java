package com.joe.boot.keypair.configuration.config;

import com.joe.boot.keypair.configuration.properties.KeyPairProperties;
import com.joe.boot.keypair.configuration.provider.token.store.KeyStoreKeyFactory;
import lombok.Data;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.jwt.crypto.sign.Signer;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Objects;

/**
 * @author QiangQiang Gu
 * @date 2022-03-15 20:24:35
 */
public class KeyPair {
    /**
     * 非对称加密技术——RSA算法
     * 对称加密与非对称加密
     * 对称加密特点:文件或数据的加密解密使用同一个密钥
     * 非对称加密特点:加密需要两个密钥,公钥(PublicKey)与私钥(PrivateKey)
     * 公钥与私钥是一对
     * 如果使用公钥对数据加密,需要对应的私钥解密
     * 如果使用私钥对数据加密,需要对应的公钥解密
     * 两种用法:公钥加密,私钥解密。私钥签名,公钥验签。
     * <p>
     * https://blog.csdn.net/y506798278/article/details/104145607
     * <p>
     * <p>
     * keytool -genkeypair -alias joekey -keyalg RSA -keypass joekey -keystore joekey.keystore -storepass joekeystore
     * <p>
     * -alias:密钥的别名
     * <p>
     * -keyalg：使用的hash算法,this use rsa
     * <p>
     * -keypass：密钥的访问密码,jdk1.8 keytool 至少六位字符
     * <p>
     * -keystore：密钥库的文件名
     * <p>
     * storepass：密钥库的访问密码
     * <p>
     * -storepass：密钥库的访问密码
     * <p>
     * <p>
     * Warning:
     * JKS 密钥库使用专用格式。建议使用 "keytool -importkeystore -srckeystore joekey.keystore -destkeystore joekey.keystore -deststoretype pkcs12" 迁移到行业标准格式 PKCS12。
     */
    private String keyStorePassword;
    private String keyPassword;
    private String keyAlias;
    private String keystoreName;

    private KeyPair() {
    }

    private String getKeyStorePassword() {
        return keyStorePassword;
    }

    private void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    private String getKeyPassword() {
        return keyPassword;
    }

    private void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }

    private String getKeyAlias() {
        return keyAlias;
    }

    private void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }

    private String getKeystoreName() {
        return keystoreName;
    }

    private void setKeystoreName(String keystoreName) {
        this.keystoreName = keystoreName;
    }

    private java.security.KeyPair getKeyPair() {
        return keyPair;
    }

    private void setKeyPair(java.security.KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    private void setRsaSigner(Signer rsaSigner) {
        this.rsaSigner = rsaSigner;
    }

    private void setRsaVerifier(SignatureVerifier rsaVerifier) {
        this.rsaVerifier = rsaVerifier;
    }

    public KeyPair(KeyPairProperties properties) {
        keyStorePassword = properties.getKeyStorePassword();
        keyPassword = properties.getKeyPassword();
        keyAlias = properties.getKeyAlias();
        keystoreName = properties.getKeystoreName();
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource(keystoreName), keyStorePassword.toCharArray());
        keyPair = keyStoreKeyFactory.getKeyPair(keyAlias, keyPassword.toCharArray());
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
        rsaSigner = new RsaSigner(((RSAPrivateKey) privateKey));
        rsaVerifier = new RsaVerifier(((RSAPublicKey) publicKey));
    }

    private java.security.KeyPair keyPair;

    private PrivateKey privateKey;

    private PublicKey publicKey;

    private Signer rsaSigner;

    private SignatureVerifier rsaVerifier;

    /**
     * 签名KEY
     *
     * @return RSAPrivateKey
     */
    public PrivateKey getSignerKey() {
        return privateKey;
    }

    /**
     * 验签KEY
     *
     * @return RSAPublicKey
     */
    public PublicKey getVerifierKey() {
        return publicKey;
    }


    /**
     * simple RsaSigner
     * <p>
     * RSA算法加签
     *
     * @return KeyPair
     */
    public Signer getRsaSigner() {
        return rsaSigner;
    }

    /**
     * simple RsaVerifier
     * <p>
     * RSA算法验签
     *
     * @return KeyPair
     */
    public SignatureVerifier getRsaVerifier() {
        return rsaVerifier;
    }

}