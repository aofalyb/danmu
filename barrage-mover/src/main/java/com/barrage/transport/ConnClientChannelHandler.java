package com.barrage.transport;

import com.barrage.common.Log;
import com.barrage.message.DouyuMessage;
import com.barrage.util.DouyuPacketBuilder;
import com.barrage.protocol.DouyuPacket;
import io.netty.channel.*;

/**
 * @author liyang
 * @description:
 * @date 2018/4/3
 */
@ChannelHandler.Sharable
public class ConnClientChannelHandler extends ChannelInboundHandlerAdapter {

    private Connection connection;

    public String RID;


    public ConnClientChannelHandler(String rid) {
        this.RID = rid;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        DouyuPacket packet = (DouyuPacket) msg;
        DouyuMessage douyuMessage = new DouyuMessage(packet,connection);
        douyuMessage.decode();
        //Map<String, String> attributes = douyuMessage.getAttributes();


    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //  TODO
        connection = new Connection(ctx.channel(),RID);

         ctx.writeAndFlush(DouyuPacketBuilder.build(DouyuPacket.PACKET_TYPE_LOGIN, RID)).addListener(new ChannelFutureListener() {
             @Override
             public void operationComplete(ChannelFuture channelFuture) throws Exception {
                 if(channelFuture.isSuccess()){
                     ctx.writeAndFlush(DouyuPacketBuilder.build(DouyuPacket.PACKET_TYPE_JOINGROUP,RID)).addListener(new ChannelFutureListener() {
                         @Override
                         public void operationComplete(ChannelFuture channelFuture) throws Exception {
                             if(channelFuture.isSuccess()){
                                 connection.hearBeat();
                             }
                         }
                     });
                 }
             }
         });


    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        Log.errorLogger.error(ctx,cause);
        //TODO 检测链接可用性，不可用尝试重连 988 952595 3857053

    }
}
