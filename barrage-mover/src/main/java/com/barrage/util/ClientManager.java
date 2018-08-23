package com.barrage.util;

import com.barrage.boot.NettyClient;

import java.util.HashMap;
import java.util.Map;

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


}
