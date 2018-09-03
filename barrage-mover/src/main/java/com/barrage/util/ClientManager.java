package com.barrage.util;

import com.barrage.boot.NettyClient;
import com.barrage.common.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 客户端管理工具
 */
public class ClientManager {

    public static Map<String,NettyClient> clientPool = new HashMap();


    public static boolean put(String roomId,NettyClient client) {

        if(clientPool.get(roomId) != null) {
            return false;
        }
        clientPool.put(roomId,client);
        return true;

    }

    public static NettyClient get(String roomId) {
        return clientPool.get(roomId);
    }

    public static void closeAll() {

        Set<String> keySet = clientPool.keySet();

        for (String key :
                keySet) {

            NettyClient nettyClient = clientPool.get(key);
            if(nettyClient != null) {
                try {
                    nettyClient.doStop();
                } catch (Throwable throwable) {
                    Log.errorLogger.error("client stop fail.",throwable);
                }
            }

        }


    }


}
