package com.danmu.api;

import com.alibaba.fastjson.JSON;
import com.danmu.common.DouyuMessage;
import com.danmu.common.DouyuPacketBuilder;
import com.danmu.common.Log;
import com.danmu.protocol.DouyuPacket;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;

/**
 * @author liyang
 * @description:
 * @date 2018/4/3
 */
public class ConnClientChannelHandler extends ChannelInboundHandlerAdapter {

    private Connection connection;

    public static final String RID = "78561";


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        DouyuPacket packet = (DouyuPacket) msg;
        DouyuMessage douyuMessage = new DouyuMessage(packet,connection);
        douyuMessage.decode();
        Map<String, String> attributes = douyuMessage.getAttributes();
        Log.d(JSON.toJSONString(attributes));
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
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

}
