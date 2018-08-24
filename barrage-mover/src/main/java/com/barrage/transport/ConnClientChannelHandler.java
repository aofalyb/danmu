package com.barrage.transport;

import com.barrage.common.Log;
import com.barrage.message.DouyuLoginMessage;
import com.barrage.message.DouyuMessage;
import com.barrage.protocol.DouyuPacket;
import io.netty.channel.*;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author liyang
 * @description:
 * @date 2018/4/3
 */
@ChannelHandler.Sharable
public class ConnClientChannelHandler extends ChannelInboundHandlerAdapter {

    private Connection connection;
    public String RID;

    private AtomicBoolean lock = new AtomicBoolean(false);



    public ConnClientChannelHandler(String rid) {
        this.RID = rid;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        DouyuPacket packet = (DouyuPacket) msg;
        DouyuMessage douyuMessage = new DouyuMessage(packet,connection);
        douyuMessage.decode();
        Map<String, String> attributes = douyuMessage.getAttributes();

        String type = attributes.get("type");
        if(Objects.equals(type,"loginres")) {
            lock.set(true);
        }



    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //  TODO
        connection = new Connection(ctx.channel(),RID);

        try {
          //  lock.lock();

            doLogin(connection,new Listener(){

                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                }
            });
        } finally {

//            lock.unlock();

        }


//        ctx.channel().writeAndFlush(DouyuPacketBuilder.build(DouyuPacket.PACKET_TYPE_LOGIN, RID)).addListener(new ChannelFutureListener() {
//             @Override
//             public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                 if(channelFuture.isSuccess()){
//                     ctx.writeAndFlush(DouyuPacketBuilder.build(DouyuPacket.PACKET_TYPE_JOINGROUP,RID)).addListener(new ChannelFutureListener() {
//                         @Override
//                         public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                             if(channelFuture.isSuccess()){
//                                 connection.hearBeat();
//                             }
//                         }
//                     });
//                 }
//             }
//         });


    }

    //登录弹幕服务器
    private void doLogin(Connection connection,Listener listener) {

        new DouyuLoginMessage(null,connection)
                .setRid(RID)
                .send()
                .addListener((future -> {
                    if(future.isSuccess()) {
                        //15秒超时时间
                        long b_t = System.currentTimeMillis();

                        while (((System.currentTimeMillis() - b_t) < DouyuLoginMessage.LOGIN_TIME_OUT) || !lock.get()) {
                            if((System.currentTimeMillis() - b_t) > DouyuLoginMessage.LOGIN_TIME_OUT) {

                                //超时了,直接退出程序
                                Log.errorLogger.error("login douyu room time out rid={},sys exit...",RID);
                                listener.onError();
                                System.exit(0);
                            }

                            if(lock.get()) {
                                break;
                            }

                            Thread.sleep(100);
                        }

                        //发送入组消息
                        joinGroup(connection,listener);
                    }
                }));
    }


    private void joinGroup(Connection connection,Listener listener) {



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
