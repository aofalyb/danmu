package com.barrage.netty;

import com.barrage.boot.NettyClient;
import com.barrage.common.Constants;
import com.barrage.common.Log;
import com.barrage.message.DouyuLoginMessage;
import com.barrage.message.DouyuMessage;
import com.barrage.message.handler.DouyuDefaultMessageHandler;
import com.barrage.message.handler.DouyuLoginMessageHandler;
import com.barrage.message.handler.MessageHandlerDispatcher;
import com.barrage.protocol.DouyuPacket;
import com.barrage.util.ClientManager;
import io.netty.channel.*;


/**
 * @author liyang
 * @description: 430489 276685 606118 99999 520 88188 5221750 911 71017 688
 * @date 2018/4/3
 */
@ChannelHandler.Sharable
public class DouyuConnClientChannelHandler extends ChannelInboundHandlerAdapter {

    private Connection connection;
    public String RID;

    private MessageHandlerDispatcher messageHandlerDispatcher;


    public DouyuConnClientChannelHandler(String rid) {
        this.RID = rid;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        DouyuPacket packet = (DouyuPacket) msg;
        DouyuMessage douyuMessage = new DouyuMessage(packet,connection);
        messageHandlerDispatcher.dispatch(douyuMessage);
        connection.refreshRead();
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        if(connection == null) {
            connection = new Connection(ctx.channel(),RID);
        }
        //init handler
        messageHandlerDispatcher = new MessageHandlerDispatcher(connection);
        messageHandlerDispatcher.register("loginres|loginreq",new DouyuLoginMessageHandler());
        messageHandlerDispatcher.register("def",new DouyuDefaultMessageHandler());


        doLogin(connection);

    }

    //登录弹幕服务器
    private void doLogin(Connection connection) {

        new DouyuLoginMessage(null,connection)
                .send()
                .addListener((future -> {
                    if(future.isSuccess()) {
                        connection.refreshState(Connection.ConnectionState.LOGIN_PRE);
                    } else {
                        throw new NettyClientException("send login req fail.");
                    }
                }));
    }





    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        connection.refreshState(Connection.ConnectionState.INACTIVE);
        Log.errorLogger.error("client {} offline. try reconnect...",RID);
        Log.errorLogger.error("ctx : {} ",ctx);

        NettyClient nettyClient = ClientManager.get(RID);
        nettyClient.reLogin(connection);


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        Log.errorLogger.error(ctx,cause);
        //TODO 检测链接可用性，不可用尝试重连 988 952595 3857053

    }
}
