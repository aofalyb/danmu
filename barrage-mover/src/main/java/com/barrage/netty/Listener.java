package com.barrage.netty;

public interface Listener {

    void onSuccess(Object... args);

    void onFailure(Throwable cause);

}