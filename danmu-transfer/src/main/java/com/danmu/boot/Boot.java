package com.danmu.boot;

/**
 * @author liyang
 * @description:
 * @date 2018/4/3
 */
public class Boot {

    public static void main(String[] args) throws Throwable{
        DouyuNettyClient douyuNettyClient = new DouyuNettyClient();

        douyuNettyClient.doStart(new DouyuNettyClient.Listener() {
            @Override
            public void onSuccess(Object... args) {
                douyuNettyClient.connect("openbarrage.douyutv.com",8601);
            }

            @Override
            public void onFailure(Throwable cause) {

            }
        });


    }
}
