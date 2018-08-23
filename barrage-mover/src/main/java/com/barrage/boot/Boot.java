package com.barrage.boot;

import com.barrage.common.Log;
import com.barrage.config.CC;
import com.barrage.util.ClientManager;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * @author liyang
 * @description:弹幕抓取启动类
 * @date 2018/4/3
 */
public class Boot {

    public static void main(String[] args) throws Throwable{

        //String roomIds = CC.douyu.rid;
        String[] roomIdArray = args;
        if(args == null || args.length < 1) {
            Log.errorLogger.error("server will exit because there is no any room id....");
            System.exit(0);
            return;
        }

        for (int i = 0; i < roomIdArray.length; i++) {
            String roomId = roomIdArray[i];

            DouyuNettyClient douyuRoom = new DouyuNettyClient(roomId);

            douyuRoom.doStart(new DouyuNettyClient.Listener() {
                @Override
                public void onSuccess(Object... args) {
                    ChannelFuture connect = douyuRoom.connect("openbarrage.douyutv.com", 8601);

                    connect.addListener((future) -> {
                       if(future.isSuccess()) {
                           ClientManager.put(roomId,douyuRoom);
                       } else {

                           Log.errorLogger.error("server start error at room {}.",roomId);
                           Log.errorLogger.error(connect);
                           System.exit(0);
                       }
                    });


                }
                @Override
                public void onFailure(Throwable cause) {
                    Log.errorLogger.error("server start error at room {}.",roomId,cause);
                    System.exit(0);
                }
            });

        }
        System.out.println("===================================================================");
        System.out.println("=================== BARRAGE SERVER STARTING =======================");
        System.out.println("===================================================================");

    }
}
