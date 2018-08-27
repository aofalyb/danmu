package com.barrage.netty;

import com.barrage.common.Constants;
import com.barrage.common.Log;
import com.barrage.message.DouyuLoginMessage;
import com.barrage.message.DouyuMessage;
import com.barrage.message.handler.DouyuLoginMessageHandler;
import com.barrage.message.handler.MessageHandlerDispatcher;
import com.barrage.protocol.DouyuPacket;
import io.netty.channel.*;

import java.util.concurrent.CountDownLatch;

/**
 * @author liyang
 * @description:
 * @date 2018/4/3
 */
@ChannelHandler.Sharable
public class DouyuConnClientChannelHandler extends ChannelInboundHandlerAdapter {

    private Connection connection;
    public String RID;

    private MessageHandlerDispatcher messageHandlerDispatcher;

    private Listener listener;



    public DouyuConnClientChannelHandler(String rid,Listener listener) {
        this.RID = rid;
        this.listener = listener;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        DouyuPacket packet = (DouyuPacket) msg;
        DouyuMessage douyuMessage = new DouyuMessage(packet,connection);
        messageHandlerDispatcher.dispatch(douyuMessage,listener);


    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        connection = new Connection(ctx.channel(),RID);
        //init handler
        messageHandlerDispatcher = new MessageHandlerDispatcher(connection);
        messageHandlerDispatcher.register("loginres|loginreq",new DouyuLoginMessageHandler());

        doLogin(connection);

    }

    //登录弹幕服务器
    private void doLogin(Connection connection) throws NettyClientException {

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
        Log.errorLogger.error("client {} offline. try reconnect...",RID);
        Log.errorLogger.error("ctx : {} ",ctx);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        Log.errorLogger.error(ctx,cause);
        //TODO 检测链接可用性，不可用尝试重连 988 952595 3857053

    }
}
