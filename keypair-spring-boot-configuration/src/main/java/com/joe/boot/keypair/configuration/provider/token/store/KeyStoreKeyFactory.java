package com.joe.boot.keypair.configuration.provider.token.store;

import org.springframework.core.io.Resource;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.RSAPublicKeySpec;

/**
 * @author QiangQiang Gu
 */
public class KeyStoreKeyFactory {
    private Resource resource;
    private char[] password;
    private KeyStore store;
    private final Object lock = new Object();

    public KeyStoreKeyFactory(Resource resource, char[] password) {
        this.resource = resource;
        this.password = password;
    }

    public KeyPair getKeyPair(String alias) {
        return this.getKeyPair(alias, this.password);
    }

    public KeyPair getKeyPair(String alias, char[] password) {
        try {
            synchronized (this.lock) {
                if (this.store == null) {
                    synchronized (this.lock) {
                        this.store = KeyStore.getInstance("jks");
                        this.store.load(this.resource.getInputStream(), this.password);
                    }
                }
            }

            RSAPrivateCrtKey key = (RSAPrivateCrtKey) this.store.getKey(alias, password);
            RSAPublicKeySpec spec = new RSAPublicKeySpec(key.getModulus(), key.getPublicExponent());
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(spec);
            return new KeyPair(publicKey, key);
        } catch (Exception var9) {
            throw new IllegalStateException("Cannot load keys from store: " + this.resource, var9);
        }
    }
}
