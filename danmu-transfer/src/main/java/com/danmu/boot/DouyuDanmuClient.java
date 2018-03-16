package com.danmu.boot;

import com.danmu.codec.DouyuPacketDecoder;
import com.danmu.common.DouyuJoingroupMessage;
import com.danmu.common.DouyuLoginMessage;
import com.danmu.common.DouyuPacketBuilder;
import com.danmu.common.OnMessageSendListener;
import com.danmu.protocol.DouyuPacket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Set;

/**
 * @author liyang
 * @description:
 * @date 2018/3/7
 */
public class DouyuDanmuClient {
    static Charset charset = Charset.forName("UTF-8");
    private volatile boolean stop = false;

    static Selector selector = null;

    static SocketChannel channel = null;

    public static void main(String[] args){
        try {
            Selector selector = Selector.open();
             channel = SocketChannel.open();
            // 设置为非阻塞模式，这个方法必须在实际连接之前调用(所以open的时候不能提供服务器地址，否则会自动连接)
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress("openbarrage.douyutv.com",8601));
            //channel.connect(new InetSocketAddress("192.168.1.229",6378));
            channel.register(selector, SelectionKey.OP_CONNECT);

            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            if(selectionKeys.iterator().hasNext()){
                SelectionKey selectionKey = selectionKeys.iterator().next();
                handleConnect(selectionKey);
            }

            channel.register(selector,SelectionKey.OP_READ);

            while (true){
                selector.select();
                Set<SelectionKey> msgKeys = selector.selectedKeys();

                if(msgKeys.iterator().hasNext()){
                    SelectionKey selectionKey = msgKeys.iterator().next();
                    handleMessage(selectionKey);
                }
            }


        } catch (Exception e){
            e.printStackTrace();
        }

    }


    private static void handleConnect(SelectionKey key) throws IOException{


        if(key.isConnectable()){
            if(channel.isConnectionPending()){
                System.out.println("成功连接服务端："+channel.getRemoteAddress());
                channel.finishConnect();
                doLogin();

            }

        }

    }

    //发送登录认证消息 78561
    private static void doLogin() {

        DouyuLoginMessage douyuLoginMessage = new DouyuLoginMessage(DouyuPacketBuilder.build(DouyuPacket.PACKET_TYPE_LOGIN,"61372"),channel);

        douyuLoginMessage.encode();
        douyuLoginMessage.send(new OnMessageSendListener() {
            public void onSuccess() {
                System.out.println("login success...");
            }

            public void onError() {
                System.out.println("login error...");
            }
        });

    }

    private static boolean isFristConnect = false;

    private static void handleMessage(SelectionKey key) throws IOException{

        if(key.isReadable()){

            SocketChannel channel  = (SocketChannel) key.channel();

            DouyuPacket decode = DouyuPacketDecoder.decode(channel);


            if(!isFristConnect){
                DouyuJoingroupMessage joingroupMessage = new DouyuJoingroupMessage(DouyuPacketBuilder.build(DouyuPacket.PACKET_TYPE_JOINGROUP,"61372"),channel);

                joingroupMessage.encode();
                joingroupMessage.send(new OnMessageSendListener() {
                    public void onSuccess() {
                        System.out.println("join group success...");
                        isFristConnect = true;
                    }

                    public void onError() {
                        System.out.println("join group error...");
                    }
                });
            }




        }


    }

    private  void doWrite(SocketChannel sc,String data) throws IOException{
        byte[] req =data.getBytes();
        ByteBuffer byteBuffer = ByteBuffer.allocate(req.length);
        byteBuffer.put(req);
        byteBuffer.flip();
        sc.write(byteBuffer);
        if(!byteBuffer.hasRemaining()){
            System.out.println("Send 2 client successed");
        }
    }
}
