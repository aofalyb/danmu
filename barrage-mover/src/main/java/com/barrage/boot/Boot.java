package com.barrage.boot;

import com.barrage.common.Log;
import com.barrage.config.CC;
import com.barrage.elastic.EsClient;
import com.barrage.netty.Listener;
import com.barrage.netty.NettyClientException;
import com.barrage.util.ClientManager;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.Date;

/**
 * @author liyang
 * @description:弹幕抓取启动类
 * @date 2018/4/3
 */
public class Boot {

    public static void main(String[] args) {

        //String roomIds = CC.douyu.rid;
        String[] roomIdArray = args;
        if(args == null || args.length < 1) {
            Log.errorLogger.error("server will exit because there is no any room id....");
            System.exit(0);
            return;
        }



        System.out.println("===================================================================");
        System.out.println("===================== BARRAGE SERVER START NOW ====================");
        System.out.println("===================================================================");

        try {
            EsClient.getInstance().init();
        } catch (Exception e) {
            Log.errorLogger.error("EsClient start fail,exit now...",e);
            System.exit(0);
        }

        for (int i = 0; i < roomIdArray.length; i++) {
            String roomId = roomIdArray[i];

            DouyuNettyClient douyuRoom = new DouyuNettyClient(roomId);

            try {
                douyuRoom.login();
                ClientManager.put(roomId,douyuRoom);
                System.out.println("ROOM="+roomId+" login success at "+new Date());
            } catch (NettyClientException e) {
                Log.errorLogger.error("server start fail,exit now...",e);
                System.exit(0);
            }

        }
        System.out.println("===================================================================");
        System.out.println("=================== BARRAGE SERVER START SUCCESS ==================");
        System.out.println("===================================================================");

        addShutdownHook();

    }

    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("===================================================================");
            System.out.println("====================== BARRAGE SERVER STOP ========================");
            System.out.println("===================================================================");
            Log.errorLogger.error("server stop now...");
            ClientManager.closeAll();

        }));

    }
}
