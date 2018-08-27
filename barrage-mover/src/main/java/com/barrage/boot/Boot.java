package com.barrage.boot;

import com.barrage.common.Log;
import com.barrage.config.CC;
import com.barrage.netty.Listener;
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


            douyuRoom.login(new Listener() {
                @Override
                public void onSuccess(Object... args) {

                    ClientManager.put(roomId,douyuRoom);

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
