package com.barrage.message.handler;

public class MessageHandleRuntimeException extends RuntimeException {


    public MessageHandleRuntimeException() {
        super();
    }

    public MessageHandleRuntimeException(String message) {
        super(message);
    }

    public MessageHandleRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageHandleRuntimeException(Throwable cause) {
        super(cause);
    }

    protected MessageHandleRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
