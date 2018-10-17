import com.alibaba.fastjson.JSON;
import com.barrage.common.Log;
import com.barrage.message.DouyuSerializeUtil;
import com.barrage.netty.Connection;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.*;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author liyang
 * @description:
 * @date 2018/4/4
 */
public class TetsJunit {

    public static void main(String[] args) {

        String hexString = "sofioshfuewh0129e80923sdofhso";
        byte[] decode1 = HexBin.decode(hexString);

        System.out.println();

    }

    public static void login(Connection connection) {

    }

    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset+1] & 0xFF)<<8)
                | ((src[offset+2] & 0xFF)<<16)
                | ((src[offset+3] & 0xFF)<<24));
        return value;
    }
}
