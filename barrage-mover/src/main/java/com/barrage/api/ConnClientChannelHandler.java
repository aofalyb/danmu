package com.barrage.api;

import com.alibaba.fastjson.JSON;
import com.barrage.common.DouyuMessage;
import com.barrage.common.DouyuPacketBuilder;
import com.barrage.common.Log;
import com.barrage.config.CC;
import com.barrage.protocol.DouyuPacket;
import io.netty.channel.*;

import java.util.Map;

/**
 * @author liyang
 * @description:
 * @date 2018/4/3
 */
@ChannelHandler.Sharable
public class ConnClientChannelHandler extends ChannelInboundHandlerAdapter {

    private Connection connection;

    public static final String RID = CC.douyu.rid;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        DouyuPacket packet = (DouyuPacket) msg;
        DouyuMessage douyuMessage = new DouyuMessage(packet,connection);
        douyuMessage.decode();
        Map<String, String> attributes = douyuMessage.getAttributes();
        if(attributes.get("rid") == null) {
            Log.d(JSON.toJSONString(attributes));
        }else {
            Log.d(attributes.get("rid"));
        }
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
