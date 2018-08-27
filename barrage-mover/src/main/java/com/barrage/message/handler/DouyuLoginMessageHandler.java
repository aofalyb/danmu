package com.barrage.message.handler;

import com.barrage.message.DouyuJoinGroupMessage;
import com.barrage.message.DouyuLoginMessage;
import com.barrage.message.DouyuMessage;
import com.barrage.netty.Connection;
import com.barrage.netty.Listener;

import java.util.Objects;

public class DouyuLoginMessageHandler implements IMessageHandler<DouyuMessage> {

    private Listener listener;

    @Override
    public boolean handle(Connection connection, DouyuMessage message, Listener listener) {
        this.listener = listener;

        if(Objects.equals(message.getMessageType(),"loginres")) {
            //login success , join group
            connection.refreshState(Connection.ConnectionState.LOGINED);
            joinGroup(connection);

        }
        return false;
    }



    private void joinGroup(Connection connection) {
        new DouyuJoinGroupMessage(null,connection)
                .send()
                .addListener((future -> {
                    if(future.isSuccess()) {
                        connection.refreshState(Connection.ConnectionState.JOINED);
                        listener.onSuccess();
                    } else {
                        listener.onFailure(null);
                    }
                }));

    }

}
