package com.joe.boot.minio.configuration.exception;

/**
 * @author QiangQiang Gu
 */
public class MinIoClientException extends RuntimeException {

    public MinIoClientException(String message) {
        super(message);
    }

    public MinIoClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public MinIoClientException(Throwable cause) {
        super(cause);
    }

}
