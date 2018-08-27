package com.barrage.netty;

public class NettyClientException extends Exception {

    public NettyClientException() {
        super();
    }

    public NettyClientException(String message) {
        super(message);
    }

    public NettyClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public NettyClientException(Throwable cause) {
        super(cause);
    }

    protected NettyClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
