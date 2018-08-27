package com.barrage.netty;

public class NettyClientRuntimeException extends RuntimeException {
    public NettyClientRuntimeException() {
    }

    public NettyClientRuntimeException(String message) {
        super(message);
    }

    public NettyClientRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NettyClientRuntimeException(Throwable cause) {
        super(cause);
    }

    public NettyClientRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
