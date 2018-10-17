package com.barrage.boot;

public interface Listener {
    void onSuccess(Object... args);

    void onFailure(Throwable cause);
}