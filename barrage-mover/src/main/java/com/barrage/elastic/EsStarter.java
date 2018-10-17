package com.barrage.elastic;

public class EsStarter {

    public static void main(String[] args) throws Exception {
        EsClient instance = EsClient.getInstance();
        instance.init();
        instance.insert("qwer","今天开心","阿斯加德把手机爱卡"+System.currentTimeMillis());
        //instance.query(null);
    }
}
